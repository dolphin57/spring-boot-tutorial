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
 * @date 2024年03月14日 15:09
 * @description 工作流节点信息
 *  记录了工作流中的任务节点个性化的配置信息
 */
@Data
@TableName("workflow_node_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WorkflowNodeInfoDO extends Model<WorkflowNodeInfoDO> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long appId;

    private Long workflowId;

    /**
     * 节点类型 {@link WorkflowNodeType}
     */
    private Integer type;
    /**
     * 任务 ID
     */
    private Long jobId;
    /**
     * 节点名称，默认为对应的任务名称
     */
    private String nodeName;
    /**
     * 节点参数
     */
    private String nodeParams;
    /**
     * 是否启用
     */
    private Boolean enable;
    /**
     * 是否允许失败跳过
     */
    private Boolean skipWhenFailed;

    private String extra;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
