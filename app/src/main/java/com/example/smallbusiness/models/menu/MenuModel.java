package com.example.smallbusiness.models.menu;

import java.util.List;

public class MenuModel {
    private boolean success;
    private String message;
    private List<Category> result;
    private int total;

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

    public List<Category> getResult() {
        return result;
    }

    public void setResult(List<Category> result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "MenuModel{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", result=" + result +
                ", total=" + total +
                '}';
    }
}
