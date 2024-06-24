package io.dolphin.controller;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Resilience4jControllerTest {
    // 假设线程池大小为10
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        // 如果需要，可以在这里设置mock的行为，例如当调用acquirePermission时返回true/false
    }

    @Test
    void rateLimiterApiSuccess() throws Exception {
        // 配置mock以模拟成功情况
        when(rateLimiter.acquirePermission()).thenReturn(true);

        mockMvc.perform(get("/rate-limiter"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("success")));
    }

    @Test
    void concurrentRequestTest() throws InterruptedException {
        int requestCount = 8; // 假设我们要发送5个请求
        for (int i = 0; i < requestCount; i++) {
            executorService.submit(() -> {
                try {
                    mockMvc.perform(get("/rate-limiter"))
                            .andExpect(status().isOk())
                            .andExpect(content().string(equalTo("success")));
                } catch (Exception e) {
                    // 处理异常，根据实际情况记录或断言
                    e.printStackTrace();
                }
            });
        }
        // 等待所有任务完成
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES); // 等待最多1分钟
    }

    @Test
    public void concurrentRateLimiterApiRequests() throws Exception {
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        try {
            IntStream.range(0, numberOfThreads)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return mockMvc.perform(get("/rate-limiter"))
                                    .andExpect(status().isOk())
                                    .andReturn();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, executor))
                    .forEach(CompletableFuture::join); // 等待所有请求完成

            // 如果所有请求都成功，这个测试将会通过。
            // 注意：实际的断言应该放在每个请求的结果上，上面的代码仅展示了如何并发执行请求。
        } finally {
            executor.shutdown(); // 关闭线程池
        }
    }

    @Test
    void fallback() throws Exception {
        // 配置mock以模拟限流失败情况
        when(rateLimiter.acquirePermission()).thenReturn(false);

        // 注意：此测试假设您已实现并配置了名为"fallback"的方法来处理降级逻辑。
        // 根据实际情况调整预期结果（如响应体、状态码等）
        mockMvc.perform(get("/rate-limiter"))
                .andExpect(status().isOk()) // 或者根据您的fallback逻辑调整期望的状态码
                .andExpect(content().string(equalTo("Your Expected Fallback Response"))); // 替换为实际的降级响应内容
    }
}