package io.dolphin.dag.controller;

import io.dolphin.dag.common.ResultDTO;
import io.dolphin.dag.controller.vo.SaveWorkflowRequest;
import io.dolphin.dag.service.WorkflowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;

/**
 * @author dolphin
 * @date 2024年03月14日 11:05
 * @description 工作流控制器
 */
@RestController
@RequestMapping("/workflow")
public class WorkflowController {
    @Resource
    private WorkflowService workflowService;

    @PostMapping("/save")
    public ResultDTO<Long> save(@RequestBody SaveWorkflowRequest req) throws ParseException {
        return ResultDTO.success(workflowService.saveWorkflow(req));
    }

    @GetMapping("/run")
    public ResultDTO<Long> runWorkflow(Long workflowId, Long appId) {
        return ResultDTO.success(workflowService.runWorkflow(workflowId, appId, null, 0L));
    }
}
