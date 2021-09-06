package com.example.smallbusiness.models.restaurant;

public class Restaurant {
    private int ID;
    private String Name, Address, Phone, Image, PaymentUrl;
    private float Lat, Lng;
    private int UserOwner;

    public int getId() {
        return ID;
    }

    public void setId(int id) {
        this.ID = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        this.Phone = phone;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public String getPaymentUrl() {
        return PaymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.PaymentUrl = paymentUrl;
    }

    public float getLat() {
        return Lat;
    }

    public void setLat(float lat) {
        this.Lat = lat;
    }

    public float getLng() {
        return Lng;
    }

    public void setLng(float lng) {
        this.Lng = lng;
    }

    public int getUserOwner() {
        return UserOwner;
    }

    public void setUserOwner(int userOwner) {
        this.UserOwner = userOwner;
    }


    @Override
    public String toString() {
        return "Restaurant{" +
                "Id=" + ID +
                ", Name='" + Name + '\'' +
                ", Address='" + Address + '\'' +
                ", Phone='" + Phone + '\'' +
                ", Image='" + Image + '\'' +
                ", PaymentUrl='" + PaymentUrl + '\'' +
                ", Lat=" + Lat +
                ", Lng=" + Lng +
                '}';
    }
}
