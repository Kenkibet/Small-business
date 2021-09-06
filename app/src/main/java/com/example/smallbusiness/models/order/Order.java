package com.example.smallbusiness.models.order;

public class Order {
    private int OrderId;
    private String OrderEmail;
    private String OrderPhone;
    private String OrderName;
    private String OrderAddress;
    private int OrderStatus;
    private String OrderDate;
    private int RestaurantId;
    private String TransactionId;
    private boolean COD;
    private int NumOfItem;
    private double TotalPrice;

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getOrderEmail() {
        return OrderEmail;
    }

    public void setOrderEmail(String orderEmail) {
        OrderEmail = orderEmail;
    }

    public String getOrderPhone() {
        return OrderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        OrderPhone = orderPhone;
    }

    public String getOrderName() {
        return OrderName;
    }

    public void setOrderName(String orderName) {
        OrderName = orderName;
    }

    public String getOrderAddress() {
        return OrderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        OrderAddress = orderAddress;
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public int getRestaurantId() {
        return RestaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        RestaurantId = restaurantId;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public boolean isCOD() {
        return COD;
    }

    public void setCOD(boolean COD) {
        this.COD = COD;
    }

    public int getNumOfItem() {
        return NumOfItem;
    }

    public void setNumOfItem(int numOfItem) {
        NumOfItem = numOfItem;
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        TotalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "OrderId=" + OrderId +
                ", OrderEmail='" + OrderEmail + '\'' +
                ", OrderPhone='" + OrderPhone + '\'' +
                ", TotalPrice=" + TotalPrice +
                '}';
    }
}
