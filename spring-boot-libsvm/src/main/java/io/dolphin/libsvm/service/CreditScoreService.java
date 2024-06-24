package io.dolphin.libsvm.service;

import io.dolphin.libsvm.entity.CreditScoreData;
import libsvm.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年03月20日 10:07
 * @description
 */
@Service
public class CreditScoreService {
    public svm_model trainModel(List<CreditScoreData> trainingData, double C, double gamma) throws Exception {
        // 将训练数据转换为libsvm的格式
        svm_problem problem = new svm_problem();
        problem.l = trainingData.size();
        problem.y = new double[problem.l];
        problem.x = new svm_node[problem.l][];

        for (int i = 0; i < problem.l; i++) {
            problem.y[i] = trainingData.get(i).getLabel();
            problem.x[i] = new svm_node[trainingData.get(i).getFeatures().size()];
            for (int j = 0; j < problem.x[i].length; j++) {
                problem.x[i][j] = new svm_node();
                problem.x[i][j].index = j + 1;
                problem.x[i][j].value = trainingData.get(i).getFeatures().get(j);
            }
        }

        // 设置SVM参数
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.C = C;
        param.gamma = gamma;

        // 训练模型
        svm_model model = svm.svm_train(problem, param);

        return model;
    }

    public double predict(List<Double> features, svm_model model) throws Exception {
        svm_node[] x = new svm_node[features.size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = new svm_node();
            x[i].index = i + 1;
            x[i].value = features.get(i);
        }

        return svm.svm_predict(model, x);
    }
}
