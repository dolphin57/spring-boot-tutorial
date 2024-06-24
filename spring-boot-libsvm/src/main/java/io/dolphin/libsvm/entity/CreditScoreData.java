package io.dolphin.libsvm.entity;

import lombok.Data;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年03月20日 10:48
 * @description
 */
@Data
public class CreditScoreData {
    private List<Double> features;
    private Double label;

    public CreditScoreData(List<Double> features, Double label) {
        this.features = features;
        this.label = label;
    }
}
