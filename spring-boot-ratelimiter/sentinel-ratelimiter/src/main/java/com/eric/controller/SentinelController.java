package com.eric.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Dolphin
 * @Date 2024/6/17 上午10:52
 * @Description TODO
 */
@RestController
@Slf4j
public class SentinelController {
    private static final String RESOURCE_NAME = "HelloWorld";

    @PostConstruct
    private static void initFlowRules() {
        // 配置规则
        List<FlowRule> rules = new ArrayList<>();
        FlowRule helloRule = new FlowRule();
        // 设置受保护的资源
        helloRule.setResource(RESOURCE_NAME);
        // 设置流控规则 QPS
        helloRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置受保护的资源阈值
        // Set limit QPS to 20
        helloRule.setCount(1);
        rules.add(helloRule);
        FlowRule testRule = new FlowRule();
        testRule.setResource("test");
        testRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        testRule.setCount(1);
        rules.add(testRule);
        // 加载配置好的规则
        FlowRuleManager.loadRules(rules);
    }

    @RequestMapping("/sentinel")
    public String hello() {
        try (Entry entry = SphU.entry(RESOURCE_NAME)){
            log.info("hello world");
            return "hello world";
        } catch (BlockException ex) {
            log.info("blocked");
            return "blocked";
        }
    }

    @SentinelResource(value = "test", blockHandler = "handleException", fallback = "fallbackException")
    @RequestMapping("/test")
    public String test() {
        log.info("test");
        return "test";
    }

    public String handleException(BlockException ex) {
        log.info("blocked");
        return "blocked";
    }

    public String fallbackException() {
        log.info("fallback");
        return "fallback";
    }
}
