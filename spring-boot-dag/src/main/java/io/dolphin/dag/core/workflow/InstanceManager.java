package io.dolphin.dag.core.workflow;

import io.dolphin.dag.enums.InstanceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author dolphin
 * @date 2024年03月15日 11:41
 * @description 管理被调度的任务实例（状态更新相关）
 */
@Slf4j
@Service
public class InstanceManager {
    @Resource
    private WorkflowInstanceManager workflowInstanceManager;

    /**
     * 收尾完成的任务实例
     *
     * @param instanceId   任务实例ID
     * @param wfInstanceId 工作流实例ID，非必须
     * @param status       任务状态，有 成功/失败/手动停止
     * @param result       执行结果
     */
    public void processFinishedInstance(Long instanceId, Long wfInstanceId, InstanceStatus status, String result) {
        log.info("[Instance-{}] process finished, final status is {}.", instanceId, status.name());
        // workflow 特殊处理
        if (wfInstanceId != null) {
            // 手动停止在工作流中也认为是失败（理论上不应该发生）
            workflowInstanceManager.move(wfInstanceId, instanceId, status, result);
        }
    }
}
