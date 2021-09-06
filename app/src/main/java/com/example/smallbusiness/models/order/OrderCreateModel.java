package com.example.smallbusiness.models.order;

import java.util.List;

public class OrderCreateModel {
    private boolean success;
    private String message;
    private List<OrderCreate> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OrderCreate> getResult() {
        return result;
    }

    public void setResult(List<OrderCreate> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "OrderCreateModel{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
