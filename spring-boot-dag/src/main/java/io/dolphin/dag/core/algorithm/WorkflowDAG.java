package io.dolphin.dag.core.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年03月14日 14:42
 * @description DAG 工作流对象 引用表达法的DAG图
 *  使用引用，易于计算（不再参与主运算，起辅助作用）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDAG {
    /**
     * DAG允许存在多个顶点
     */
    private List<Node> roots;

    @Data
    @NoArgsConstructor
    public static final class Node {
        public Node(List<Node> successors, Long nodeId, Long jobId, String jobName, int status) {
            this.successors = successors;
            this.nodeId = nodeId;
            this.jobId = jobId;
            this.jobName = jobName;
            this.status = status;
        }

        /**
         * 后继者，子节点
         */
        private List<Node> successors;
        /**
         * node id
         *
         * @since 20210128
         */
        private Long nodeId;

        private Long jobId;

        private String jobName;
        /**
         * 运行时信息
         */
        private Long instanceId;
        /**
         * 状态 WAITING_DISPATCH -> RUNNING -> SUCCEED/FAILED/STOPPED
         */
        private int status;

        private String result;
        /**
         * instanceId will be null if disable .
         */
        private Boolean enable;

        private Boolean skipWhenFailed;
    }
}
