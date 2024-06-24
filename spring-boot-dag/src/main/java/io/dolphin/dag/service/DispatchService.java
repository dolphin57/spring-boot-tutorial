package io.dolphin.dag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import io.dolphin.dag.common.RemoteConstant;
import io.dolphin.dag.common.SystemInstanceResult;
import io.dolphin.dag.core.workflow.InstanceManager;
import io.dolphin.dag.entity.InstanceInfoDO;
import io.dolphin.dag.entity.JobInfoDO;
import io.dolphin.dag.enums.DispatchStrategy;
import io.dolphin.dag.enums.ExecuteType;
import io.dolphin.dag.enums.ProcessorType;
import io.dolphin.dag.enums.TimeExpressionType;
import io.dolphin.dag.mapper.InstanceInfoMapper;
import io.dolphin.dag.workcluster.model.ServerScheduleJobReq;
import io.dolphin.dag.workcluster.model.WorkerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static io.dolphin.dag.enums.DispatchStrategy.HEALTH_FIRST;
import static io.dolphin.dag.enums.InstanceStatus.*;

/**
 * @author dolphin
 * @date 2024年03月15日 11:27
 * @description 派送服务（将任务从Server派发到Worker）
 */
@Slf4j
@Service
public class DispatchService {
    @Resource
    private InstanceInfoMapper instanceInfoMapper;
    @Resource
    private InstanceManager instanceManager;
    @Resource
    private WorkerClusterQueryService workerClusterQueryService;

    /**
     * 将任务从Server派发到Worker（TaskTracker）
     * 只会派发当前状态为等待派发的任务实例
     * **************************************************
     * 1、移除参数 当前运行次数、工作流实例ID、实例参数
     * 更改为从当前任务实例中获取获取以上信息
     * 2、移除运行次数相关的（runningTimes）处理逻辑
     * 迁移至 {@link InstanceManager#updateStatus} 中处理
     * **************************************************
     *
     * @param jobInfo    任务的元信息
     * @param instanceId 任务实例ID
     */
//    @UseSegmentLock(type = "dispatch", key = "#jobInfo.getId() ?: 0", concurrencyLevel = 1024)
    public void dispatch(JobInfoDO jobInfo, long instanceId) {
        // 检查当前任务是否被取消
        InstanceInfoDO instanceInfo = instanceInfoMapper.selectOne(new LambdaQueryWrapper<InstanceInfoDO>().eq(InstanceInfoDO::getInstanceId, instanceId));
        Long jobId = instanceInfo.getJobId();
        if (CANCELED.getV() == instanceInfo.getStatus()) {
            log.info("[Dispatcher-{}|{}] cancel dispatch due to instance has been canceled", jobId, instanceId);
            return;
        }
        // 已经被派发过则不再派发
        // fix 并发场景下重复派发的问题
        if (instanceInfo.getStatus() != WAITING_DISPATCH.getV()) {
            log.info("[Dispatcher-{}|{}] cancel dispatch due to instance has been dispatched", jobId, instanceId);
            return;
        }
        // 任务信息已经被删除
        if (jobInfo.getId() == null) {
            log.warn("[Dispatcher-{}|{}] cancel dispatch due to job(id={}) has been deleted!", jobId, instanceId, jobId);
            instanceManager.processFinishedInstance(instanceId, instanceInfo.getWfInstanceId(), FAILED, "can't find job by id " + jobId);
            return;
        }

        Date now = new Date();
        String dbInstanceParams = instanceInfo.getInstanceParams() == null ? "" : instanceInfo.getInstanceParams();
        log.info("[Dispatcher-{}|{}] start to dispatch job: {};instancePrams: {}.", jobId, instanceId, jobInfo, dbInstanceParams);

        // 查询当前运行的实例数
        long current = System.currentTimeMillis();
        Integer maxInstanceNum = jobInfo.getMaxInstanceNum();
        // 秒级任务只派发到一台机器，具体的 maxInstanceNum 由 TaskTracker 控制
        if (TimeExpressionType.frequentTypes.contains(jobInfo.getTimeExpressionType())) {
            maxInstanceNum = 1;
        }

        // 0 代表不限制在线任务，还能省去一次 DB 查询
        if (maxInstanceNum > 0) {
            // 不统计 WAITING_DISPATCH 的状态：使用 OpenAPI 触发的延迟任务不应该统计进去（比如 delay 是 1 天）
            // 由于不统计 WAITING_DISPATCH，所以这个 runningInstanceCount 不包含本任务自身
            long runningInstanceCount = instanceInfoMapper.selectCount(new LambdaQueryWrapper<InstanceInfoDO>()
                    .eq(InstanceInfoDO::getJobId,jobId)
                    .in(InstanceInfoDO::getStatus,Lists.newArrayList(WAITING_WORKER_RECEIVE.getV(), RUNNING.getV())));
            // 超出最大同时运行限制，不执行调度
            if (runningInstanceCount >= maxInstanceNum) {
                String result = String.format(SystemInstanceResult.TOO_MANY_INSTANCES, runningInstanceCount, maxInstanceNum);
                log.warn("[Dispatcher-{}|{}] cancel dispatch job due to too much instance is running ({} > {}).", jobId, instanceId, runningInstanceCount, maxInstanceNum);
                instanceInfoMapper.update(null, new LambdaUpdateWrapper<InstanceInfoDO>()
                        .set(InstanceInfoDO::getStatus, FAILED.getV())
                        .set(InstanceInfoDO::getActualTriggerTime, current)
                        .set(InstanceInfoDO::getFinishedTime, current)
                        .set(InstanceInfoDO::getTaskTrackerAddress, RemoteConstant.EMPTY_ADDRESS)
                        .set(InstanceInfoDO::getResult, result)
                        .set(InstanceInfoDO::getGmtModified, now)
                        .eq(InstanceInfoDO::getInstanceId, instanceId));
                instanceManager.processFinishedInstance(instanceId, instanceInfo.getWfInstanceId(), FAILED, result);
                return;
            }
        }

        // 获取当前最合适的 worker 列表
        List<WorkerInfo> suitableWorkers = workerClusterQueryService.getSuitableWorkers(jobInfo);
        if (CollectionUtils.isEmpty(suitableWorkers)) {
            log.warn("[Dispatcher-{}|{}] cancel dispatch job due to no worker available", jobId, instanceId);
            instanceInfoMapper.update(null, new LambdaUpdateWrapper<InstanceInfoDO>()
                    .set(InstanceInfoDO::getStatus, FAILED.getV())
                    .set(InstanceInfoDO::getActualTriggerTime, current)
                    .set(InstanceInfoDO::getFinishedTime, current)
                    .set(InstanceInfoDO::getTaskTrackerAddress, RemoteConstant.EMPTY_ADDRESS)
                    .set(InstanceInfoDO::getResult, SystemInstanceResult.NO_WORKER_AVAILABLE)
                    .set(InstanceInfoDO::getGmtModified, now)
                    .eq(InstanceInfoDO::getInstanceId, instanceId));
            instanceManager.processFinishedInstance(instanceId, instanceInfo.getWfInstanceId(), FAILED, SystemInstanceResult.NO_WORKER_AVAILABLE);
            return;
        }
        List<String> workerIpList = suitableWorkers.stream().map(WorkerInfo::getAddress).collect(Collectors.toList());

        // 构造任务调度请求
        ServerScheduleJobReq req = constructServerScheduleJobReq(jobInfo, instanceInfo, workerIpList);
        // 发送请求（不可靠，需要一个后台线程定期轮询状态）
        WorkerInfo taskTracker = selectTaskTracker(jobInfo, suitableWorkers);
        String taskTrackerAddress = taskTracker.getAddress();

//        transportService.tell(Protocol.of(taskTracker.getProtocol()), taskTrackerAddress, req);
        log.info("[Dispatcher-{}|{}] send schedule request to TaskTracker[protocol:{},address:{}] successfully: {}.", jobId, instanceId, taskTracker.getProtocol(), taskTrackerAddress, req);

        // 修改状态
        instanceInfoMapper.update(null, new LambdaUpdateWrapper<InstanceInfoDO>()
                .set(InstanceInfoDO::getStatus, WAITING_WORKER_RECEIVE.getV())
                .set(InstanceInfoDO::getActualTriggerTime, current)
                .set(InstanceInfoDO::getTaskTrackerAddress, taskTrackerAddress)
                .set(InstanceInfoDO::getGmtModified, now)
                .eq(InstanceInfoDO::getInstanceId, instanceId));

        // 装载缓存
//        instanceMetadataService.loadJobInfo(instanceId, jobInfo);
    }

    /**
     * 构造任务调度请求
     */
    private ServerScheduleJobReq constructServerScheduleJobReq(JobInfoDO jobInfo, InstanceInfoDO instanceInfo, List<String> finalWorkersIpList) {
        // 构造请求
        ServerScheduleJobReq req = new ServerScheduleJobReq();
        BeanUtils.copyProperties(jobInfo, req);
        // 传入 JobId
        req.setJobId(jobInfo.getId());
        // 传入 InstanceParams
        if (StringUtils.isEmpty(instanceInfo.getInstanceParams())) {
            req.setInstanceParams(null);
        } else {
            req.setInstanceParams(instanceInfo.getInstanceParams());
        }
        // 覆盖静态参数
        if (!StringUtils.isEmpty(instanceInfo.getJobParams())) {
            req.setJobParams(instanceInfo.getJobParams());
        }
        req.setInstanceId(instanceInfo.getInstanceId());
        req.setAllWorkerAddress(finalWorkersIpList);

        // 设置工作流ID
        req.setWfInstanceId(instanceInfo.getWfInstanceId());

        req.setExecuteType(ExecuteType.of(jobInfo.getExecuteType()).name());
        req.setProcessorType(ProcessorType.of(jobInfo.getProcessorType()).name());

        req.setTimeExpressionType(TimeExpressionType.of(jobInfo.getTimeExpressionType()).name());
        if (jobInfo.getInstanceTimeLimit() != null) {
            req.setInstanceTimeoutMS(jobInfo.getInstanceTimeLimit());
        }
        req.setThreadConcurrency(jobInfo.getConcurrency());
        return req;
    }

    private WorkerInfo selectTaskTracker(JobInfoDO jobInfo, List<WorkerInfo> workerInfos) {
        DispatchStrategy dispatchStrategy = DispatchStrategy.of(jobInfo.getDispatchStrategy());
        switch (dispatchStrategy) {
            case HEALTH_FIRST:
                return workerInfos.get(0);
            case RANDOM:
                return workerInfos.get(ThreadLocalRandom.current().nextInt(workerInfos.size()));
            default:
        }
        // impossible, indian java
        return workerInfos.get(0);
    }
}
