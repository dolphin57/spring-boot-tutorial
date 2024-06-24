package io.dolphin.libsvm.controller;

import io.dolphin.libsvm.entity.CreditScoreData;
import io.dolphin.libsvm.service.CreditScoreService;
import libsvm.svm_model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年03月20日 9:58
 * @description
 */
@RestController
public class CreditScoreController {
    @Autowired
    private CreditScoreService creditScoreService;

    @PostMapping("/train")
    public ResponseEntity<svm_model> trainModel(@RequestBody List<CreditScoreData> trainingData, double C, double gamma) throws Exception {
        svm_model model = creditScoreService.trainModel(trainingData, C, gamma);
        return ResponseEntity.ok(model);
    }

    @PostMapping("/predict")
    public ResponseEntity<Double> predictCreditScore(@RequestBody List<Double> features, @RequestBody svm_model model) throws Exception {
        double score = creditScoreService.predict(features, model);
        return ResponseEntity.ok(score);
    }
}
