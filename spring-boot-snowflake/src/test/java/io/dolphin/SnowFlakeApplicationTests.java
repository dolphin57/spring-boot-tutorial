package io.dolphin;

import com.sankuai.inf.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * @author dolphin
 * @date 2024年04月10日 15:17
 * @description
 */
@Slf4j
@SpringBootTest
class SnowFlakeApplicationTests {
    @Resource
    private SnowflakeService snowflakeService;

    @Test
    void contextLoads() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 1000; i++) {
            long dolphin = snowflakeService.getId("dolphin").getId();
            long eric = snowflakeService.getId("eric").getId();
            log.info("dolphin: {}, eric: {}", dolphin, eric);
        }
        stopWatch.stop();
        log.info("耗时: {}", stopWatch.prettyPrint());
    }
}
