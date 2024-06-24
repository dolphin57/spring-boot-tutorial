package io.dolphin.libsvm.core;

import libsvm.*;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年03月20日 10:07
 * @description
 */
public class CreditScoreModelTrainer {
    public svm_model trainModel(List<double[]> features, List<Double> labels) throws Exception {
        // 检查数据集是否为空或大小不匹配
        if (features == null || labels == null || features.size() != labels.size()) {
            throw new IllegalArgumentException("Features and labels lists must not be null and have the same size.");
        }

        // 创建SVM问题结构
        svm_problem prob = new svm_problem();
        prob.l = features.size(); // 样本数量
        prob.y = new double[prob.l]; // 标签数组
        prob.x = new svm_node[prob.l][]; // 特征节点数组

        // 将数据填充到svm_node数组
        for (int i = 0; i < prob.l; i++) {
            prob.y[i] = labels.get(i); // 设置标签
            svm_node[] nodes = new svm_node[features.get(i).length];
            for (int j = 0; j < features.get(i).length; j++) {
                nodes[j] = new svm_node();
                nodes[j].index = j + 1; // 索引从1开始
                nodes[j].value = features.get(i)[j]; // 特征值
            }
            prob.x[i] = nodes;
        }

        // 设置SVM参数
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC; // 或其他类型，如Nu_SVC, EPSILON_SVR等
        param.kernel_type = svm_parameter.RBF; // 或其他核函数类型
        // 其他参数设置，比如C、gamma、epsilon等

        // 训练模型
        svm_model model = svm.svm_train(prob, param);

        return model;
    }

    public void saveModel(svm_model model, String modelName) throws Exception {
        svm.svm_save_model(modelName, model);
    }

    public svm_model loadModel(String modelName) throws Exception {
        return svm.svm_load_model(modelName);
    }
}
