package io.dolphin.leaf.controller;

import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import com.sankuai.inf.leaf.service.SegmentService;
import com.sankuai.inf.leaf.service.SnowflakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dolphin
 * @date 2024年04月07日 16:19
 * @description
 */
@RestController
public class LeafController {
    private Logger logger = LoggerFactory.getLogger(LeafController.class);

    @Autowired
    private SegmentService segmentService;
//    @Autowired
//    private SnowflakeService snowflakeService;

    /**
     * 号段模式
     * @param key
     * @return
     */
    @RequestMapping(value = "/api/segment/get/{key}")
    public String getSegmentId(@PathVariable("key") String key) {
        return get(key, segmentService.getId(key));
    }

    /**
     * 雪花算法模式
     * @param key
     * @return
     */
//    @RequestMapping(value = "/api/snowflake/get/{key}")
//    public String getSnowflakeId(@PathVariable("key") String key) {
//        return get(key, snowflakeService.getId(key));
//    }

    private String get(@PathVariable("key") String key, Result id) {
        Result result;
        if (key == null || key.isEmpty()) {
//            throw new NoKeyException();
        }
        result = id;
        if (result.getStatus().equals(Status.EXCEPTION)) {
//            throw new LeafServerException(result.toString());
        }
        return String.valueOf(result.getId());
    }
}
