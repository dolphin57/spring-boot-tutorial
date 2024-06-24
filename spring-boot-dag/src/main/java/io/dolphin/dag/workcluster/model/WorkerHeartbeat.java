package io.dolphin.dag.workcluster.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * Worker 上报健康信息（worker定时发送的heartbeat）
 *
 * @author tjq
 * @since 2020/3/25
 */
@Data
public class WorkerHeartbeat implements Serializable {

    // 本机地址 -> IP:port
    private String workerAddress;
    // 当前 appName
    private String appName;
    // 当前 appId
    private Long appId;
    // 当前时间
    private long heartbeatTime;
    // 当前加载的容器（容器名称 -> 容器版本）
    private List<DeployedContainerInfo> containerInfos;
    // worker 版本信息
    private String version;
    // 使用的通讯协议 AKKA / HTTP
    private String protocol;
    // worker tag，标识同一个 worker 下的一类集群 ISSUE: 226
    private String tag;
    // 客户端名称
    private String client;
    // 扩展字段
    private String extra;

    private SystemMetrics systemMetrics;
}
