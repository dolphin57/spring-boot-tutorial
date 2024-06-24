package io.dolphin.dag.core;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author dolphin
 * @date 2024年03月14日 11:41
 * @description Points & edges for DAG, making it easier to describe or transfer.
 * 采用 点-线图的交互方式 到 DAG
 */
@Data
@NoArgsConstructor
public class PEWorkflowDAG implements Serializable {
    /**
     * Nodes of DAG diagram
     */
    private List<Node> nodes;
    /**
     * Edges of DAG diagram
     */
    private List<Edge> edges;

    /**
     * Ponit
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Node implements Serializable {
        /**
         * Node id
         */
        private Long nodeId;
        /**
         * Node name
         */
        private String nodeName;
        /**
         * Node type
         */
        private Integer nodeType;
        /**
         * Node params
         */
        private String nodeParams;
        /**
         * Job id
         */
        private Long jobId;

        @JsonSerialize(using= ToStringSerializer.class)
        private Long instanceId;

        private Integer status;

        private String result;
        /**
         * instanceId will be null if disable .
         */
        private Boolean enable;

        private Boolean skipWhenFailed;
        public Node(Long nodeId) {
            this.nodeId = nodeId;
        }
     }

    /**
     * Edge formed by two node ids.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Edge implements Serializable {
        private Long from;
        private Long to;
    }

    public PEWorkflowDAG(@NonNull List<Node> nodes, @NonNull List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges == null ? Lists.newArrayList() : edges;
    }
}
