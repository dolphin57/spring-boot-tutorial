package io.dolphin.dag.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.dolphin.dag.common.DesignateServer;
import io.dolphin.dag.common.SJ;
import io.dolphin.dag.controller.vo.SaveWorkflowRequest;
import io.dolphin.dag.core.PEWorkflowDAG;
import io.dolphin.dag.core.algorithm.WorkflowDAGUtils;
import io.dolphin.dag.core.workflow.WorkflowInstanceManager;
import io.dolphin.dag.entity.WorkflowInfoDO;
import io.dolphin.dag.entity.WorkflowNodeInfoDO;
import io.dolphin.dag.enums.SwitchableStatus;
import io.dolphin.dag.enums.TimeExpressionType;
import io.dolphin.dag.exception.DolphinException;
import io.dolphin.dag.mapper.WorkflowInfoMapper;
import io.dolphin.dag.mapper.WorkflowNodeInfoMapper;
import io.dolphin.dag.utils.CronExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dolphin
 * @date 2024年03月14日 13:34
 * @description Workflow 服务
 */
@Slf4j
@Service
public class WorkflowService {
    @Resource
    private WorkflowInstanceManager workflowInstanceManager;
    @Resource
    private WorkflowInfoMapper workflowInfoMapper;
    @Resource
    private WorkflowNodeInfoMapper workflowNodeInfoMapper;

    /**
     * 保存/修改工作流信息
     *
     * 注意这里不会保存 DAG 信息
     *
     * @param req 请求
     * @return 工作流ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveWorkflow(SaveWorkflowRequest req) throws ParseException {
        req.valid();

        Long wfId = req.getId();
        WorkflowInfoDO wf;
        if (wfId == null) {
            wf = new WorkflowInfoDO();
            wf.setGmtCreate(new Date());
        } else {
            Long finalWfId = wfId;
            wf = workflowInfoMapper.selectById(finalWfId);
        }

        BeanUtils.copyProperties(req, wf);
        wf.setGmtModified(new Date());
        wf.setStatus(req.isEnable() ? SwitchableStatus.ENABLE.getV() : SwitchableStatus.DISABLE.getV());
        wf.setTimeExpressionType(req.getTimeExpressionType().getV());

        if (req.getNotifyUserIds() != null) {
            wf.setNotifyUserIds(SJ.COMMA_JOINER.join(req.getNotifyUserIds()));
        }

        // 计算 NextTriggerTime
        if (req.getTimeExpressionType() == TimeExpressionType.CRON) {
            CronExpression cronExpression = new CronExpression(req.getTimeExpression());
            Date nextValidTime = cronExpression.getNextValidTimeAfter(new Date());
            wf.setNextTriggerTime(nextValidTime.getTime());
        } else {
            wf.setTimeExpression(null);
        }
        // 新增工作流，需要先 save 一下获取 ID
        if (wfId == null) {
            workflowInfoMapper.insert(wf);
            wfId = wf.getId();
        }

        wf.setPeDAG(validateAndConvert2String(wfId, req.getDag()));
        workflowInfoMapper.updateById(wf);
        return wfId;
    }

    /**
     * 保存 DAG 信息
     * 这里会物理删除游离的节点信息
     */
    private String validateAndConvert2String(Long wfId, PEWorkflowDAG dag) {
        if (dag == null || CollectionUtils.isEmpty(dag.getNodes())) {
            return "{}";
        }
        if (!WorkflowDAGUtils.valid(dag)) {
            throw new DolphinException("illegal DAG");
        }

        // 注意：这里只会保存图相关的基础信息，nodeId,jobId,jobName(nodeAlias)
        // 其中 jobId，jobName 均以节点中的信息为准
        List<Long> nodeIdList = Lists.newArrayList();
        List<PEWorkflowDAG.Node> newNodes = Lists.newArrayList();
        for (PEWorkflowDAG.Node node : dag.getNodes()) {
            WorkflowNodeInfoDO nodeInfo = workflowNodeInfoMapper.selectById(node.getNodeId());
            // 更新工作流 ID
            if (nodeInfo.getWorkflowId() == null) {
                nodeInfo.setWorkflowId(wfId);
                nodeInfo.setGmtModified(new Date());
                workflowNodeInfoMapper.updateById(nodeInfo);
            }
            if (!wfId.equals(nodeInfo.getWorkflowId())) {
                throw new DolphinException("can't use another workflow's node");
            }

            // 只保存节点的 ID 信息，清空其他信息
            newNodes.add(new PEWorkflowDAG.Node(node.getNodeId()));
            nodeIdList.add(node.getNodeId());
        }

        dag.setNodes(newNodes);
        int deleteCount = workflowNodeInfoMapper.deleteByWorkflowIdAndIdNotIn(wfId, nodeIdList);
        log.warn("[WorkflowService-{}] delete {} dissociative nodes of workflow", wfId, deleteCount);
        return JSON.toJSONString(dag);
    }

    /**
     * 深度复制工作流
     *
     * @param wfId  工作流 ID
     * @param appId APP ID
     * @return 生成的工作流 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long copyWorkflow(Long wfId, Long appId) {
        WorkflowInfoDO originWorkflow = permissionCheck(wfId, appId);
        if (originWorkflow.getStatus() == SwitchableStatus.DELETED.getV()) {
            throw new IllegalStateException("can't copy the workflow which has been deleted!");
        }

        // 拷贝基础信息
        WorkflowInfoDO copyWorkflow = new WorkflowInfoDO();
        BeanUtils.copyProperties(originWorkflow, copyWorkflow);
        copyWorkflow.setId(null);
        copyWorkflow.setGmtCreate(new Date());
        copyWorkflow.setGmtModified(new Date());
        copyWorkflow.setWfName(copyWorkflow.getWfName() + "_COPY");

        PEWorkflowDAG peWorkflowDAG = null;
        if (StringUtils.isNotEmpty(copyWorkflow.getPeDAG())) {
            peWorkflowDAG = JSON.parseObject(copyWorkflow.getPeDAG(), PEWorkflowDAG.class);
            // 拷贝节点信息，并且更新 DAG 中的节点信息
            if (!CollectionUtils.isEmpty(peWorkflowDAG.getNodes())) {
                // originNodeId => copyNodeId
                HashMap<Long, Long> nodeIdMap = new HashMap<>(peWorkflowDAG.getNodes().size(), 1);
                // 校正 节点信息
                for (PEWorkflowDAG.Node node : peWorkflowDAG.getNodes()) {
                    WorkflowNodeInfoDO originNode = workflowNodeInfoMapper.selectById(node.getNodeId());
                    WorkflowNodeInfoDO copyNode = new WorkflowNodeInfoDO();
                    BeanUtils.copyProperties(originNode, copyNode);
                    copyNode.setId(null);
                    copyNode.setWorkflowId(copyWorkflow.getId());
                    copyNode.setGmtCreate(new Date());
                    copyNode.setGmtModified(new Date());

                    nodeIdMap.put(originNode.getId(), copyNode.getId());
                    node.setNodeId(copyNode.getId());
                }

                // 校正 边信息
                for (PEWorkflowDAG.Edge edge : peWorkflowDAG.getEdges()) {
                    edge.setFrom(nodeIdMap.get(edge.getFrom()));
                    edge.setTo(nodeIdMap.get(edge.getTo()));
                }
            }
        }
        copyWorkflow.setPeDAG(JSON.toJSONString(peWorkflowDAG));
        workflowInfoMapper.insert(copyWorkflow);
        return copyWorkflow.getId();
    }

    private WorkflowInfoDO permissionCheck(Long wfId, Long appId) {
        WorkflowInfoDO wfInfo = workflowInfoMapper.selectById(wfId);
        if (!wfInfo.getAppId().equals(appId)) {
            throw new DolphinException("Permission Denied! can't operate other app's workflow!");
        }
        return wfInfo;
    }

    /**
     * 获取工作流元信息，这里获取到的 DAG 包含节点的完整信息（是否启用、是否允许失败跳过）
     *
     * @param wfId  工作流ID
     * @param appId 应用ID
     * @return 对外输出对象
     */
    public WorkflowInfoDO obtainWorkflow(Long wfId, Long appId) {
        WorkflowInfoDO wfInfo = permissionCheck(wfId, appId);
        fillWorkflow(wfInfo);
        return wfInfo;
    }

    private void fillWorkflow(WorkflowInfoDO wfInfo) {
        PEWorkflowDAG dagInfo = null;
        try {
            dagInfo = JSON.parseObject(wfInfo.getPeDAG(), PEWorkflowDAG.class);
        } catch (Exception e) {
            log.error("[WorkflowService-{}]illegal DAG : {}", wfInfo.getId(), wfInfo.getPeDAG());
        }

        if (dagInfo == null) {
            return;
        }

        Map<Long, WorkflowNodeInfoDO> nodeIdNodInfoMap = Maps.newHashMap();
        workflowNodeInfoMapper.selectList(new QueryWrapper<WorkflowNodeInfoDO>().eq("workflow_id", wfInfo.getId()))
                .forEach(nodeInfo -> nodeIdNodInfoMap.put(nodeInfo.getId(), nodeInfo));

        // 填充节点信息
        if (!CollectionUtils.isEmpty(dagInfo.getNodes())) {
            for (PEWorkflowDAG.Node node : dagInfo.getNodes()) {
                WorkflowNodeInfoDO nodeInfo = nodeIdNodInfoMap.get(node.getNodeId());
                if (nodeInfo != null) {
                    node.setNodeType(nodeInfo.getType())
                            .setJobId(nodeInfo.getJobId())
                            .setEnable(nodeInfo.getEnable())
                            .setSkipWhenFailed(nodeInfo.getSkipWhenFailed())
                            .setNodeName(nodeInfo.getNodeName())
                            .setNodeParams(nodeInfo.getNodeParams());
                }
            }
        }
        wfInfo.setPeDAG(JSON.toJSONString(dagInfo));
    }

    /**
     * 删除工作流（软删除）
     *
     * @param wfId  工作流ID
     * @param appId 所属应用ID
     */
    public void deleteWorkflow(Long wfId, Long appId) {
        WorkflowInfoDO wfInfo = permissionCheck(wfId, appId);
        wfInfo.setStatus(SwitchableStatus.DELETED.getV());
        wfInfo.setGmtModified(new Date());
        workflowInfoMapper.updateById(wfInfo);
    }

    /**
     * 禁用工作流
     *
     * @param wfId  工作流ID
     * @param appId 所属应用ID
     */
    public void disableWorkflow(Long wfId, Long appId) {
        WorkflowInfoDO wfInfo = permissionCheck(wfId, appId);
        wfInfo.setStatus(SwitchableStatus.DISABLE.getV());
        wfInfo.setGmtModified(new Date());
        workflowInfoMapper.updateById(wfInfo);
    }

    /**
     * 启用工作流
     *
     * @param wfId  工作流ID
     * @param appId 所属应用ID
     */
    public void enableWorkflow(Long wfId, Long appId) {
        WorkflowInfoDO wfInfo = permissionCheck(wfId, appId);
        wfInfo.setStatus(SwitchableStatus.ENABLE.getV());
        wfInfo.setGmtModified(new Date());
        workflowInfoMapper.updateById(wfInfo);
    }

    /**
     * 立即运行工作流
     *
     * @param wfId       工作流ID
     * @param appId      所属应用ID
     * @param initParams 启动参数
     * @param delay      延迟时间
     * @return 该 workflow 实例的 instanceId（wfInstanceId）
     */
    @DesignateServer
    public Long runWorkflow(Long wfId, Long appId, String initParams, Long delay) {
        delay = delay == null ? 0 : delay;
        WorkflowInfoDO wfInfo = permissionCheck(wfId, appId);

        log.info("[WorkflowService-{}] try to run workflow, initParams={},delay={} ms.", wfInfo.getId(), initParams, delay);
        Long wfInstanceId = workflowInstanceManager.create(wfInfo, initParams, System.currentTimeMillis() + delay);
        if (delay <= 0) {
            workflowInstanceManager.start(wfInfo, wfInstanceId);
        } else {
//            InstanceTimeWheelService.schedule(wfInstanceId, delay, () -> workflowInstanceManager.start(wfInfo, wfInstanceId));
        }
        return wfInstanceId;
    }
}
