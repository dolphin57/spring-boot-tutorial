package io.dolphin.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Dolphin
 * @Date 2024/6/14 下午2:44
 * @Description TODO
 */
@Slf4j
@RestController
public class Resilience4jController {

    @GetMapping("/rate-limiter")
    @RateLimiter(name = "rateLimiterApi", fallbackMethod = "fallback")
    public ResponseEntity<String> rateLimiterApi() {
        log.info("request ratelimitApi");
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    public ResponseEntity<String> fallback(Throwable e) {
        log.info("fallback exception, {}", e.getMessage());
        return new ResponseEntity<String>("您请求过于频繁，稍后再试", HttpStatus.OK);
    }
}
