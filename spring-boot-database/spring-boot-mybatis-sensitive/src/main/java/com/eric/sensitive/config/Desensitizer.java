package com.eric.sensitive.config;

import java.util.function.Function;

/**
 * @Description: 具体策略函数
 * @Author: qianliang
 * @Since: 2020-9-9 20:26
 */
public interface Desensitizer extends Function<String, String> {
}
