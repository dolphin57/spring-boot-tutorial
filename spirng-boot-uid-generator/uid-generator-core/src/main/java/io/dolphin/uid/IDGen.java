package io.dolphin.uid;

import io.dolphin.uid.common.Result;

/**
 * @author dolphin
 * @date 2024年05月14日 11:28
 * @description
 */
public interface IDGen {
    Result get(String key);

    boolean init();
}
