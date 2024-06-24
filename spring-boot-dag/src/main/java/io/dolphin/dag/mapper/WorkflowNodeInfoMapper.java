package io.dolphin.dag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dolphin.dag.entity.WorkflowNodeInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年03月14日 15:08
 * @description WorkflowNodeInfo 数据访问层
 */
public interface WorkflowNodeInfoMapper extends BaseMapper<WorkflowNodeInfoDO> {
    int deleteByWorkflowIdAndIdNotIn(@Param("wfId") Long wfId, @Param("nodeIdList") List<Long> nodeIdList);
}
