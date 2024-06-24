package io.dolphin.dag.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dolphin
 * @date 2024年03月14日 14:23
 * @description 支持开/关的状态，如 任务状态（JobStatus）和工作流状态（WorkflowStatus）
 */
@Getter
@AllArgsConstructor
public enum SwitchableStatus {
    ENABLE(1),
    DISABLE(2),
    DELETED(99);

    private final int v;

    public static SwitchableStatus of(int v) {
        for (SwitchableStatus type : values()) {
            if (type.v == v) {
                return type;
            }
        }
        throw new IllegalArgumentException("unknown SwitchableStatus of " + v);
    }
}
