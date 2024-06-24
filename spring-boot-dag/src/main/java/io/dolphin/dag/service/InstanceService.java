package io.dolphin.dag.service;

import io.dolphin.dag.core.uid.IdGenerateService;
import io.dolphin.dag.entity.InstanceInfoDO;
import io.dolphin.dag.enums.InstanceStatus;
import io.dolphin.dag.enums.InstanceType;
import io.dolphin.dag.mapper.InstanceInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author dolphin
 * @date 2024年03月15日 11:10
 * @description 任务运行实例服务
 */
@Slf4j
@Service
public class InstanceService {
    @Resource
    private IdGenerateService idGenerateService;
    @Resource
    private InstanceInfoMapper instanceInfoMapper;

    /**
     * 创建任务实例（注意，该方法并不调用 saveAndFlush，如果有需要立即同步到DB的需求，请在方法结束后手动调用 flush）
     * ********************************************
     * 新增 jobParams ，每次均记录任务静态参数
     * ********************************************
     *
     * @param jobId             任务ID
     * @param appId             所属应用ID
     * @param jobParams         任务静态参数
     * @param instanceParams    任务实例参数，仅 OpenAPI 创建 或者 工作流任务 时存在
     * @param wfInstanceId      工作流任务实例ID，仅工作流下的任务实例存在
     * @param expectTriggerTime 预期执行时间
     * @return 任务实例ID
     */
    public Long create(Long jobId, Long appId, String jobParams, String instanceParams, Long wfInstanceId, Long expectTriggerTime) {
        Long instanceId = idGenerateService.allocate();
        Date now = new Date();

        InstanceInfoDO newInstanceInfo = new InstanceInfoDO();
        newInstanceInfo.setJobId(jobId);
        newInstanceInfo.setAppId(appId);
        newInstanceInfo.setInstanceId(instanceId);
        newInstanceInfo.setJobParams(jobParams);
        newInstanceInfo.setInstanceParams(instanceParams);
        newInstanceInfo.setType(wfInstanceId == null ? InstanceType.NORMAL.getV() : InstanceType.WORKFLOW.getV());
        newInstanceInfo.setWfInstanceId(wfInstanceId);

        newInstanceInfo.setStatus(InstanceStatus.WAITING_DISPATCH.getV());
        newInstanceInfo.setRunningTimes(0L);
        newInstanceInfo.setExpectedTriggerTime(expectTriggerTime);
        newInstanceInfo.setLastReportTime(-1L);
        newInstanceInfo.setGmtCreate(now);
        newInstanceInfo.setGmtModified(now);

        instanceInfoMapper.insert(newInstanceInfo);
        return instanceId;
    }
}
