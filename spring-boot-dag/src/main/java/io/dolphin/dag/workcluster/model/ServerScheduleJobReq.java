package io.dolphin.dag.workcluster.model;

import io.dolphin.dag.common.ProtocolConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 服务端调度任务请求（一次任务处理的入口）
 *
 * @author tjq
 * @since 2020/3/17
 */
@Data
public class ServerScheduleJobReq implements Serializable {

    // 可用处理器地址，可能多值，逗号分隔
    private List<String> allWorkerAddress;

    /* *********************** 任务相关属性 *********************** */

    /**
     * 任务ID，当更换Server后需要根据 JobId 重新查询任务元数据
     */
    private Long jobId;

    private Long wfInstanceId;
    /**
     * 基础信息
     */
    private Long instanceId;

    /**
     * 任务执行处理器信息
     */
    // 任务执行类型，单机、广播、MR
    private String executeType;
    // 处理器类型（JavaBean、Jar、脚本等）
    private String processorType;
    // 处理器信息
    private String processorInfo;


    /**
     * 超时时间
     */
    // 整个任务的总体超时时间
    private long instanceTimeoutMS;

    /**
     * 任务运行参数
     */
    // 任务级别的参数，相当于类的static变量
    private String jobParams;
    // 实例级别的参数，相当于类的普通变量（API触发专用，从API触发处带入）
    private String instanceParams;

    // 每台机器的处理线程数上限
    private int threadConcurrency;
    // 子任务重试次数（任务本身的重试机制由server控制）
    private int taskRetryNum;

    /**
     * 定时执行信息
     */
    // 时间表达式类型（CRON/API/FIX_RATE/FIX_DELAY）
    private String timeExpressionType;
    // 时间表达式，CRON/NULL/LONG/LONG（单位MS）
    private String timeExpression;

    // 最大同时运行任务数，默认 1
    private Integer maxInstanceNum;

    public String path() {
        return ProtocolConstant.WORKER_PATH_DISPATCH_JOB;
    }
}
