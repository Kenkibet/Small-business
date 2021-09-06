package com.example.smallbusiness.models.food;

import java.util.List;

public class FoodModel {
    private boolean success;
    private String message;
    private List<Food> result;
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

    public List<Food> getResult() {
        return result;
    }

    public void setResult(List<Food> result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
