package com.eric.spring;

/**
 * 支付实体类
 */
public class PaymentInfo {
    private int id;
    private String status;

    public PaymentInfo(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
