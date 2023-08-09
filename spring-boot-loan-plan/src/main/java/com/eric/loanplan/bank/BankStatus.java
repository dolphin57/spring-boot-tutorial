package com.eric.loanplan.bank;

public enum BankStatus {
    NEED_REQUEST, // 所需资源超过此进程宣布的最大值
    AVAILABLE_REQUEST, // 所需资源超过此系统当前资源
    SECURITY, // 安全状态
    UNSECURITY, // 不安全状态
    SUCCESS, // 分配成功
    FAIL // 分配失败
}
