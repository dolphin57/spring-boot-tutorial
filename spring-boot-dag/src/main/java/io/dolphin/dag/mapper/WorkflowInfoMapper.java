package io.dolphin.dag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dolphin.dag.entity.WorkflowInfoDO;

/**
 * @author dolphin
 * @date 2024年03月14日 14:18
 * @description
 */
public interface WorkflowInfoMapper extends BaseMapper<WorkflowInfoDO> {
    void insertOrUpdate(WorkflowInfoDO wf);
}
