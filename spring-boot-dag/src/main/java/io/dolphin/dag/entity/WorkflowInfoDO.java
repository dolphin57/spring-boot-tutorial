package io.dolphin.dag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author dolphin
 * @date 2024年03月14日 13:45
 * @description DAG 工作流信息表
 */
@Data
@TableName("workflow_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WorkflowInfoDO extends Model<WorkflowInfoDO> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String wfName;

    private String wfDescription;

    /**
     * 所属应用ID
     */
    private Long appId;
    /**
     * 工作流的DAG图信息（点线式DAG的json）
     */
    private String peDAG;
    /* ************************** 定时参数 ************************** */
    /**
     * 时间表达式类型（CRON/API/FIX_RATE/FIX_DELAY）
     */
    private Integer timeExpressionType;
    /**
     * 时间表达式，CRON/NULL/LONG/LONG
     */
    private String timeExpression;

    /**
     * 最大同时运行的工作流个数，默认 1
     */
    private Integer maxWfInstanceNum;

    /**
     * 1 正常运行，2 停止（不再调度）
     */
    private Integer status;
    /**
     * 下一次调度时间
     */
    private Long nextTriggerTime;
    /**
     * 工作流整体失败的报警
     */
    private String notifyUserIds;

    private Date gmtCreate;

    private Date gmtModified;

    private String extra;

    private String lifecycle;
}
