package io.dolphin.dag.controller.vo;

import com.google.common.collect.Lists;
import io.dolphin.dag.core.PEWorkflowDAG;
import io.dolphin.dag.enums.TimeExpressionType;
import io.dolphin.dag.utils.CommonUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author dolphin
 * @date 2024年03月14日 11:12
 * @description 创建/修改 Workflow 请求
 */
@Data
public class SaveWorkflowRequest implements Serializable {
    private Long id;
    /**
     * 工作流名称
     */
    private String wfName;
    /**
     * 工作流描述
     */
    private String wfDescription;
    /**
     * 所属应用ID(OpenClient不需要用户填写，自动填充)
     */
    private Long appId;

    /* ************************** 定时参数 ************************** */
    /**
     * 时间表达式类型,仅支持CRON 和 API
     */
    private TimeExpressionType timeExpressionType;
    /**
     * 时间表达式，CRON/NULL/LONG/LONG
     */
    private String timeExpression;
    /**
     * 最大同时运行的工作流个数，默认 1
     */
    private Integer maxWfInstanceNum = 1;
    /**
     * 是否启用
     */
    private boolean enable = true;
    /**
     * 工作流整体失败的报警通知人
     */
    private List<Long> notifyUserIds = Lists.newArrayList();
    /**
     * 点线表示法
     */
    private PEWorkflowDAG dag;

    public void valid() {
        CommonUtils.requireNonNull(wfName, "workflow name can't be empty");
        CommonUtils.requireNonNull(appId, "appId can't be empty");
        CommonUtils.requireNonNull(timeExpressionType, "timeExpressionType can't be empty");
    }
}
