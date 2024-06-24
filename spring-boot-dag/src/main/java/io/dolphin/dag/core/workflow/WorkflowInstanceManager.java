package io.dolphin.dag.core.workflow;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.dolphin.dag.common.SystemInstanceResult;
import io.dolphin.dag.common.WorkflowContextConstant;
import io.dolphin.dag.core.PEWorkflowDAG;
import io.dolphin.dag.core.algorithm.WorkflowDAGUtils;
import io.dolphin.dag.core.uid.IdGenerateService;
import io.dolphin.dag.entity.JobInfoDO;
import io.dolphin.dag.entity.WorkflowInfoDO;
import io.dolphin.dag.entity.WorkflowInstanceInfoDO;
import io.dolphin.dag.entity.WorkflowNodeInfoDO;
import io.dolphin.dag.enums.*;
import io.dolphin.dag.exception.DolphinException;
import io.dolphin.dag.mapper.JobInfoMapper;
import io.dolphin.dag.mapper.WorkflowInstanceInfoMapper;
import io.dolphin.dag.mapper.WorkflowNodeInfoMapper;
import io.dolphin.dag.service.DispatchService;
import io.dolphin.dag.service.InstanceService;
import io.dolphin.dag.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.dolphin.dag.core.algorithm.WorkflowDAGUtils.isNotAllowSkipWhenFailed;

/**
 * @author dolphin
 * @date 2024年03月14日 16:46
 * @description 管理运行中的工作流实例
 */
@Slf4j
@Service
public class WorkflowInstanceManager {
    @Resource
    private IdGenerateService idGenerateService;
    @Resource
    private InstanceService instanceService;
    @Resource
    private DispatchService dispatchService;
    @Resource
    private WorkflowNodeInfoMapper workflowNodeInfoMapper;
    @Resource
    private JobInfoMapper jobInfoMapper;
    @Resource
    private WorkflowInstanceInfoMapper workflowInstanceInfoMapper;

    /**
     * 创建工作流任务实例
     * ********************************************
     * 通过 initParams 初始化工作流上下文（wfContext）
     * ********************************************
     *
     * @param wfInfo            工作流任务元数据（描述信息）
     * @param initParams        启动参数
     * @param expectTriggerTime 预计执行时间
     * @return wfInstanceId
     */
    public Long create(WorkflowInfoDO wfInfo, String initParams, Long expectTriggerTime) {
        Long wfId = wfInfo.getId();
        Long wfInstanceId = idGenerateService.allocate();
        // 构造实例信息
        WorkflowInstanceInfoDO newWfInstance = constructWfInstance(wfInfo, initParams, expectTriggerTime, wfId, wfInstanceId);

        PEWorkflowDAG dag = null;
        try {
            dag = JSON.parseObject(wfInfo.getPeDAG(), PEWorkflowDAG.class);
            // 校验 DAG 信息
            if (!WorkflowDAGUtils.valid(dag)) {
                log.error("[Workflow-{}|{}] DAG of this workflow is illegal! maybe you has modified the DAG info directly in database!", wfId, wfInstanceId);
                throw new DolphinException(SystemInstanceResult.INVALID_DAG);
            }

            // 初始化节点信息
            initNodeInfo(dag);
            //  最后检查工作流中的任务是否均处于可用状态（没有被删除）
            Set<Long> allJobIds = Sets.newHashSet();
            dag.getNodes().forEach(node -> {
                allJobIds.add(node.getJobId());
                // 将节点的初始状态置为等待派发
                node.setStatus(InstanceStatus.WAITING_DISPATCH.getV());
            });

            int needNum = allJobIds.size();
            long dbNum = jobInfoMapper.selectCount(new LambdaQueryWrapper<JobInfoDO>()
                    .eq(JobInfoDO::getAppId, wfInfo.getAppId())
                    .in(JobInfoDO::getStatus, SwitchableStatus.ENABLE.getV(), SwitchableStatus.DISABLE.getV())
                            .in(JobInfoDO::getId, allJobIds));
            log.debug("[Workflow-{}|{}] contains {} jobs, find {} jobs in database.", wfId, wfInstanceId, needNum, dbNum);
            if (dbNum < needNum) {
                log.warn("[Workflow-{}|{}] this workflow need {} jobs, but just find {} jobs in database, maybe you delete or disable some job!", wfId, wfInstanceId, needNum, dbNum);
                throw new DolphinException(SystemInstanceResult.CAN_NOT_FIND_JOB);
            }
            newWfInstance.setDag(JSON.toJSONString(dag));
            workflowInstanceInfoMapper.insert(newWfInstance);
        } catch (Exception e) {
            if (dag != null) {
                newWfInstance.setDag(JSON.toJSONString(dag));
            }
            onWorkflowInstanceFailed(e.getMessage(), newWfInstance);
        }
        return wfInstanceId;
    }

    /**
     * 构造工作流实例，并初始化基础信息（不包括 DAG ）
     */
    private WorkflowInstanceInfoDO constructWfInstance(WorkflowInfoDO wfInfo, String initParams, Long expectTriggerTime, Long wfId, Long wfInstanceId) {
        Date now = new Date();
        WorkflowInstanceInfoDO newWfInstance = new WorkflowInstanceInfoDO();
        newWfInstance.setAppId(wfInfo.getAppId());
        newWfInstance.setWfInstanceId(wfInstanceId);
        newWfInstance.setWorkflowId(wfId);
        newWfInstance.setStatus(WorkflowInstanceStatus.WAITING.getV());
        newWfInstance.setExpectedTriggerTime(expectTriggerTime);
        newWfInstance.setActualTriggerTime(System.currentTimeMillis());
        newWfInstance.setWfInitParams(initParams);
        // 初始化上下文
        Map<String, String> wfContextMap = Maps.newHashMap();
        wfContextMap.put(WorkflowContextConstant.CONTEXT_INIT_PARAMS_KEY, initParams);
        newWfInstance.setWfContext(JsonUtils.toJSONString(wfContextMap));
        newWfInstance.setGmtCreate(now);
        newWfInstance.setGmtModified(now);
        return newWfInstance;
    }

    /**
     * 初始化节点信息
     */
    private void initNodeInfo(PEWorkflowDAG dag) {
        for (PEWorkflowDAG.Node node : dag.getNodes()) {
            WorkflowNodeInfoDO workflowNodeInfo = workflowNodeInfoMapper.selectById(node.getNodeId());
            // 任务节点，需初始化 是否启用、是否允许失败跳过、节点参数 等信息
            if (workflowNodeInfo.getType() == null || workflowNodeInfo.getType() == WorkflowNodeType.JOB.getCode()) {
                // 任务节点缺失任务信息
                if (workflowNodeInfo.getJobId() == null) {
                    throw new DolphinException(SystemInstanceResult.ILLEGAL_NODE);
                }
                JobInfoDO jobInfo = jobInfoMapper.selectById(workflowNodeInfo.getJobId());
                node.setNodeType(WorkflowNodeType.JOB.getCode());

                // 初始化任务相关信息
                node.setJobId(workflowNodeInfo.getJobId())
                        .setNodeName(workflowNodeInfo.getNodeName())
                        .setEnable(workflowNodeInfo.getEnable())
                        .setSkipWhenFailed(workflowNodeInfo.getSkipWhenFailed());
                if (!StringUtils.isBlank(workflowNodeInfo.getNodeParams())) {
                    node.setNodeParams(workflowNodeInfo.getNodeParams());
                } else {
                    node.setNodeParams(jobInfo.getJobParams());
                }
            } else {
                // 非任务节点
                node.setNodeType(workflowNodeInfo.getType());
            }
        }
    }

    private void onWorkflowInstanceFailed(String result, WorkflowInstanceInfoDO wfInstance) {
        wfInstance.setStatus(WorkflowInstanceStatus.FAILED.getV());
        wfInstance.setResult(result);
        wfInstance.setFinishedTime(System.currentTimeMillis());
        wfInstance.setGmtModified(new Date());

        workflowInstanceInfoMapper.insert(wfInstance);

        // 报警
        try {
//            workflowInfoRepository.findById(wfInstance.getWorkflowId()).ifPresent(wfInfo -> {
//                WorkflowInstanceAlarm content = new WorkflowInstanceAlarm();
//
//                BeanUtils.copyProperties(wfInfo, content);
//                BeanUtils.copyProperties(wfInstance, content);
//                content.setResult(result);
//
//                List<UserInfoDO> userList = userService.fetchNotifyUserList(wfInfo.getNotifyUserIds());
//                alarmCenter.alarmFailed(content, userList);
//            });
        } catch (Exception ignore) {
            // ignore
        }
    }

    /**
     * 开始任务
     * ********************************************
     * 1、工作流支持配置重复的任务节点
     * 2、移除参数 initParams，改为统一从工作流实例中获取
     * 传递工作流实例的 wfContext 作为 初始启动参数
     * 3、通过 {@link WorkflowDAGUtils#listReadyNodes} 兼容原地重试逻辑
     * ********************************************
     *
     * @param wfInfo       工作流任务信息
     * @param wfInstanceId 工作流任务实例ID
     */
//    @UseSegmentLock(type = "startWfInstance", key = "#wfInfo.getId().intValue()", concurrencyLevel = 1024)
    public void start(WorkflowInfoDO wfInfo, Long wfInstanceId) {
        WorkflowInstanceInfoDO wfInstanceInfo = workflowInstanceInfoMapper.selectById(new LambdaQueryWrapper<WorkflowInstanceInfoDO>()
                .eq(WorkflowInstanceInfoDO::getWfInstanceId, wfInstanceId));
        if (wfInstanceInfo == null) {
            log.error("[WorkflowInstanceManager] can't find metadata by workflowInstanceId({}).", wfInstanceId);
            return;
        }

        // 不是等待中，不再继续执行（可能上一流程已经失败）
        if (wfInstanceInfo.getStatus() != WorkflowInstanceStatus.WAITING.getV()) {
            log.info("[Workflow-{}|{}] workflowInstance({}) needn't running any more.", wfInfo.getId(), wfInstanceId, wfInstanceInfo);
            return;
        }

        // 最大实例数量 <= 0 表示不限制
        if (wfInfo.getMaxWfInstanceNum() > 0) {
            // 并发度控制
            int instanceConcurrency = workflowInstanceInfoMapper.selectCount(new LambdaQueryWrapper<WorkflowInstanceInfoDO>()
                    .eq(WorkflowInstanceInfoDO::getWorkflowId, wfInfo.getId())
                    .in(WorkflowInstanceInfoDO::getStatus, WorkflowInstanceStatus.WAITING.getV()));
            if (instanceConcurrency > wfInfo.getMaxWfInstanceNum()) {
                onWorkflowInstanceFailed(String.format(SystemInstanceResult.TOO_MANY_INSTANCES, instanceConcurrency, wfInfo.getMaxWfInstanceNum()), wfInstanceInfo);
                return;
            }
        }

        try {
            // 从实例中读取工作流信息
            PEWorkflowDAG dag = JSON.parseObject(wfInstanceInfo.getDag(), PEWorkflowDAG.class);
            // 根节点有可能被 disable DFS深度优先遍历
            List<PEWorkflowDAG.Node> readyNodes = WorkflowDAGUtils.listReadyNodes(dag);
            // 创建所有的根任务
            readyNodes.forEach(readyNode -> {
                // 注意：这里必须保证任务实例全部创建成功，如果在这里创建实例部分失败，会导致 DAG 信息不会更新，已经生成的实例节点在工作流日志中没法展示
                // instanceParam 传递的是工作流实例的 wfContext
                Long instanceId = instanceService.create(readyNode.getJobId(), wfInfo.getAppId(), readyNode.getNodeParams(), wfInstanceInfo.getWfContext(), wfInstanceId, System.currentTimeMillis());
                readyNode.setInstanceId(instanceId);
                readyNode.setStatus(InstanceStatus.RUNNING.getV());
                log.info("[Workflow-{}|{}] create readyNode instance(nodeId={},jobId={},instanceId={}) successfully~", wfInfo.getId(), wfInstanceId, readyNode.getNodeId(), readyNode.getJobId(), instanceId);
            });

            // 持久化
            wfInstanceInfo.setStatus(WorkflowInstanceStatus.RUNNING.getV());
            wfInstanceInfo.setDag(JSON.toJSONString(dag));
            if (readyNodes.isEmpty()) {
                // 没有就绪的节点（所有节点都被禁用）
                wfInstanceInfo.setStatus(WorkflowInstanceStatus.SUCCEED.getV());
                wfInstanceInfo.setResult(SystemInstanceResult.NO_ENABLED_NODES);
                wfInstanceInfo.setFinishedTime(System.currentTimeMillis());
                log.warn("[Workflow-{}|{}] workflowInstance({}) needn't running ", wfInfo.getId(), wfInstanceId, wfInstanceInfo);
                workflowInstanceInfoMapper.updateById(wfInstanceInfo);
                return;
            }
            workflowInstanceInfoMapper.updateById(wfInstanceInfo);
            log.info("[Workflow-{}|{}] start workflow successfully", wfInfo.getId(), wfInstanceId);

            // 真正开始执行根任务
            readyNodes.forEach(this::runInstance);
        } catch (Exception e) {
            log.error("[Workflow-{}|{}] submit workflow: {} failed.", wfInfo.getId(), wfInstanceId, wfInfo, e);
            onWorkflowInstanceFailed(e.getMessage(), wfInstanceInfo);
        }
    }

    /**
     * 运行任务实例
     * 需要将创建和运行任务实例分离，否则在秒失败情况下，会发生DAG覆盖更新的问题
     *
     * @param node 节点信息
     */
    private void runInstance(PEWorkflowDAG.Node node) {
        JobInfoDO jobInfo = jobInfoMapper.selectById(node.getJobId());
        if (jobInfo == null) {
            jobInfo = new JobInfoDO();
        }
        // 洗去时间表达式类型
        jobInfo.setTimeExpressionType(TimeExpressionType.WORKFLOW.getV());
        dispatchService.dispatch(jobInfo, node.getInstanceId());
    }

    /**
     * 下一步（当工作流的某个任务完成时调用该方法）
     * ********************************************
     * 1、工作流支持配置重复的任务节点
     * 2、不再获取上游任务的结果作为实例参数而是传递工作流
     * 实例的 wfContext 作为 实例参数
     * 3、通过 {@link WorkflowDAGUtils#listReadyNodes} 支持跳过禁用的节点
     * ********************************************
     *
     * @param wfInstanceId 工作流任务实例ID
     * @param instanceId   具体完成任务的某个任务实例ID
     * @param status       完成任务的任务实例状态（SUCCEED/FAILED/STOPPED）
     * @param result       完成任务的任务实例结果
     */
    @SuppressWarnings({"squid:S3776", "squid:S2142", "squid:S1141"})
//    @UseSegmentLock(type = "processWfInstance", key = "#wfInstanceId.intValue()", concurrencyLevel = 1024)
    public void move(Long wfInstanceId, Long instanceId, InstanceStatus status, String result) {
        WorkflowInstanceInfoDO wfInstance = workflowInstanceInfoMapper.selectOne(new LambdaQueryWrapper<WorkflowInstanceInfoDO>()
                .eq(WorkflowInstanceInfoDO::getWfInstanceId, wfInstanceId));
        if (wfInstance == null) {
            log.error("[WorkflowInstanceManager] can't find metadata by workflowInstanceId({}).", wfInstanceId);
            return;
        }
        Long wfId = wfInstance.getWorkflowId();

        // 特殊处理手动终止 且 工作流实例已经不在运行状态的情况
        if (status == InstanceStatus.STOPPED && !WorkflowInstanceStatus.GENERALIZED_RUNNING_STATUS.contains(wfInstance.getStatus())) {
            // 由用户手动停止工作流实例导致，不需要任何操作
            return;
        }

        try {
            PEWorkflowDAG dag = JSON.parseObject(wfInstance.getDag(), PEWorkflowDAG.class);
            // 更新完成节点状态
            boolean allFinished = true;
            PEWorkflowDAG.Node instanceNode = null;
            for (PEWorkflowDAG.Node node : dag.getNodes()) {
                if (instanceId.equals(node.getInstanceId())) {
                    node.setStatus(status.getV());
                    node.setResult(result);
                    instanceNode = node;
                    log.info("[Workflow-{}|{}] node(nodeId={},jobId={},instanceId={}) finished in workflowInstance, status={},result={}", wfId, wfInstanceId, node.getNodeId(), node.getJobId(), instanceId, status.name(), result);
                }
                if (InstanceStatus.GENERALIZED_RUNNING_STATUS.contains(node.getStatus())) {
                    allFinished = false;
                }
            }
            if (instanceNode == null) {
                // DAG 中的节点实例已经被覆盖（原地重试，生成了新的实例信息），直接忽略
                log.warn("[Workflow-{}|{}] current job instance(instanceId={}) is dissociative! it will be ignore! ", wfId, wfInstanceId, instanceId);
                return;
            }

            wfInstance.setGmtModified(new Date());
            wfInstance.setDag(JSON.toJSONString(dag));
            // 工作流已经结束（某个节点失败导致工作流整体已经失败），仅更新最新的DAG图
            if (!WorkflowInstanceStatus.GENERALIZED_RUNNING_STATUS.contains(wfInstance.getStatus())) {
                workflowInstanceInfoMapper.updateById(wfInstance);
                log.info("[Workflow-{}|{}] workflow already finished(status={}), just update the dag info.", wfId, wfInstanceId, wfInstance.getStatus());
                return;
            }

            // 任务失败 && 不允许失败跳过，DAG流程被打断，整体失败
            if (status == InstanceStatus.FAILED && isNotAllowSkipWhenFailed(instanceNode)) {
                log.warn("[Workflow-{}|{}] workflow instance process failed because middle task(instanceId={}) failed", wfId, wfInstanceId, instanceId);
                onWorkflowInstanceFailed(SystemInstanceResult.MIDDLE_JOB_FAILED, wfInstance);
                return;
            }

            // 子任务被手动停止
            if (status == InstanceStatus.STOPPED) {
                wfInstance.setStatus(WorkflowInstanceStatus.STOPPED.getV());
                wfInstance.setResult(SystemInstanceResult.MIDDLE_JOB_STOPPED);
                wfInstance.setFinishedTime(System.currentTimeMillis());
                workflowInstanceInfoMapper.updateById(wfInstance);

                log.warn("[Workflow-{}|{}] workflow instance stopped because middle task(instanceId={}) stopped by user", wfId, wfInstanceId, instanceId);
                return;
            }
            // 注意：这里会直接跳过 disable 的节点
            List<PEWorkflowDAG.Node> readyNodes = WorkflowDAGUtils.listReadyNodes(dag);
            // 这里得重新更新一下，因为 WorkflowDAGUtils#listReadyNodes 可能会更新节点状态
            wfInstance.setDag(JSON.toJSONString(dag));
            // 如果没有就绪的节点，需要再次判断是否已经全部完成
            if (readyNodes.isEmpty() && isFinish(dag)) {
                allFinished = true;
            }
            // 工作流执行完毕（能执行到这里代表该工作流内所有子任务都执行成功了）
            if (allFinished) {
                wfInstance.setStatus(WorkflowInstanceStatus.SUCCEED.getV());
                // 最终任务的结果作为整个 workflow 的结果
                wfInstance.setResult(result);
                wfInstance.setFinishedTime(System.currentTimeMillis());
                workflowInstanceInfoMapper.updateById(wfInstance);

                log.info("[Workflow-{}|{}] process successfully.", wfId, wfInstanceId);
                return;
            }

            for (PEWorkflowDAG.Node readyNode : readyNodes) {
                // 同理：这里必须保证任务实例全部创建成功，避免部分失败导致已经生成的实例节点在工作流日志中没法展示
                // instanceParam 传递的是工作流实例的 wfContext
                Long newInstanceId = instanceService.create(readyNode.getJobId(), wfInstance.getAppId(), readyNode.getNodeParams(), wfInstance.getWfContext(), wfInstanceId, System.currentTimeMillis());
                readyNode.setInstanceId(newInstanceId);
                readyNode.setStatus(InstanceStatus.RUNNING.getV());
                log.debug("[Workflow-{}|{}] workflowInstance start to process new node(nodeId={},jobId={},instanceId={})", wfId, wfInstanceId, readyNode.getNodeId(), readyNode.getJobId(), newInstanceId);
            }
            // 这里也得更新 DAG 信息
            wfInstance.setDag(JSON.toJSONString(dag));
            workflowInstanceInfoMapper.updateById(wfInstance);
            // 持久化结束后，开始调度执行所有的任务
            readyNodes.forEach(this::runInstance);

        } catch (Exception e) {
            onWorkflowInstanceFailed("MOVE NEXT STEP FAILED: " + e.getMessage(), wfInstance);
            log.error("[Workflow-{}|{}] update failed.", wfId, wfInstanceId, e);
        }
    }

    private boolean isFinish(PEWorkflowDAG dag) {
        for (PEWorkflowDAG.Node node : dag.getNodes()) {
            if (InstanceStatus.GENERALIZED_RUNNING_STATUS.contains(node.getStatus())) {
                return false;
            }
        }
        return true;
    }
}
