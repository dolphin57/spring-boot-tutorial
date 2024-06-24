package io.dolphin.leaf;

import com.sankuai.inf.leaf.service.SegmentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
class LeafApplicationTest {
    @Resource
    private SegmentService segmentService;

    @Test
    void contextLoads() {
        // 生成 1000 个ID
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < 1000; i++) {
            long dolphinId = segmentService.getId("dolphin").getId();
            long ericId = segmentService.getId("eric").getId();
            log.info("dolphinId: {}, ericId: {}", dolphinId, ericId);
        }
        sw.stop();
        log.info(sw.prettyPrint());
    }
}