package com.eric.starter.roperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-8-27 16:18
 */
@ConfigurationProperties("calculate")
public class CalculateProperties {
    private int scale;

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

}
