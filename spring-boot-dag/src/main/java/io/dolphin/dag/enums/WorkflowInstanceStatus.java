package io.dolphin.dag.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年03月15日 10:01
 * @description Workflow 任务运行状态
 */
@Getter
@AllArgsConstructor
public enum WorkflowInstanceStatus {
    /**
     * 初始状态为等待调度
     */
    WAITING(1, "等待调度"),
    RUNNING(2, "运行中"),
    FAILED(3, "失败"),
    SUCCEED(4, "成功"),
    STOPPED(10, "手动停止");

    /**
     * 广义的运行状态
     */
    public static final List<Integer> GENERALIZED_RUNNING_STATUS = Lists.newArrayList(WAITING.v, RUNNING.v);
    /**
     * 结束状态
     */
    public static final List<Integer> FINISHED_STATUS = Lists.newArrayList(FAILED.v, SUCCEED.v, STOPPED.v);

    private final int v;

    private final String des;

    public static WorkflowInstanceStatus of(int v) {
        for (WorkflowInstanceStatus is : values()) {
            if (v == is.v) {
                return is;
            }
        }
        throw new IllegalArgumentException("WorkflowInstanceStatus has no item for value " + v);
    }
}
