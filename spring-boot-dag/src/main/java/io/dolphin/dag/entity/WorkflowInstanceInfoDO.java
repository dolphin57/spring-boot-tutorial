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
 * @date 2024年03月15日 9:56
 * @description 工作流运行实例表
 */
@Data
@TableName("workflow_instance_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WorkflowInstanceInfoDO extends Model<WorkflowInstanceInfoDO> {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务所属应用的ID，冗余提高查询效率
     */
    private Long appId;
    /**
     * workflowInstanceId（任务实例表都使用单独的ID作为主键以支持潜在的分表需求）
     */
    private Long wfInstanceId;

    private Long workflowId;
    /**
     * workflow 状态（WorkflowInstanceStatus）
     */
    private Integer status;
    /**
     * 工作流启动参数
     */
    private String wfInitParams;
    /**
     * 工作流上下文
     */
    private String wfContext;

    private String dag;

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

    private Date gmtCreate;

    private Date gmtModified;
}
