package io.dolphin.uid.segment.dao;

import io.dolphin.uid.segment.model.LeafAlloc;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年05月15日 9:59
 * @description
 */
public interface IDAllocDao {
    List<LeafAlloc> getAllLeafAllocs();
    LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);
    LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);
    List<String> getAllTags();
}
