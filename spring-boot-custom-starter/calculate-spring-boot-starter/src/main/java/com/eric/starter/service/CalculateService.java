package com.eric.starter.service;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-8-27 16:10
 */
public class CalculateService {
    /**
     * 这个从配置文件获取,就是默认的scale
     */
    private int scale;

    public CalculateService(int scale) {
        this.scale = scale;
    }

    /**
     * 加法
     * @param v1
     * @param v2
     * @return
     */
    public double add(double v1,double v2) {
        return v1+v2;
    }

    /**
     * 减法
     * @param v1
     * @param v2
     * @return
     */
    public double sub(double v1,double v2) {
        return v1-v2;
    }

    /**
     * 乘法
     * @param v1
     * @param v2
     * @return
     */
    public double mul(double v1,double v2) {
        return v1*v2;
    }

    /**
     * 精确到小数点后scale位,以后的数字四舍五入
     * @param v 数
     * @param scale 保留精度
     * @return 两个参数的商
     */
    public double setScale(double v, int scale) {
        return new BigDecimal(v).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 精确到小数点后scale位,以后的数字四舍五入
     * @param v 数
     * @return 两个参数的商
     */
    public double setScale(double v) {
        return new BigDecimal(v).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
