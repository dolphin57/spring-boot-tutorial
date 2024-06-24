package io.dolphin.dag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author dolphin
 * @date 2024年03月15日 11:13
 * @description 任务运行日志表
 */
@Data
@TableName("instance_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class InstanceInfoDO extends Model<InstanceInfoDO> {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long jobId;
    /**
     * 任务所属应用的ID，冗余提高查询效率
     */
    private Long appId;
    /**
     * 任务所属应用的ID，冗余提高查询效率
     */
    private Long instanceId;
    /**
     * 任务参数（静态）
     * @since 2021/2/01
     */
    private String jobParams;
    /**
     * 任务实例参数（动态）
     */
    private String instanceParams;
    /**
     * 该任务实例的类型，普通/工作流（InstanceType）
     */
    private Integer type;
    /**
     * 该任务实例所属的 workflow ID，仅 workflow 任务存在
     */
    private Long wfInstanceId;
    /**
     * 任务状态 {@link InstanceStatus}
     */
    private Integer status;
    /**
     * 执行结果（允许存储稍大的结果）
     */
    private String result;
    /**
     * 预计触发时间
     */
    private Long expectedTriggerTime;
    /**
     * 实际触发时间
     */
    private Long actualTriggerTime;
    /**
     * 结束时间
     */
    private Long finishedTime;
    /**
     * 最后上报时间
     */
    private Long lastReportTime;
    /**
     * TaskTracker 地址
     */
    private String taskTrackerAddress;
    /**
     * 总共执行的次数（用于重试判断）
     */
    private Long runningTimes;


    private Date gmtCreate;

    private Date gmtModified;
}
