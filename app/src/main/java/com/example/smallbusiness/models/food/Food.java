package com.example.smallbusiness.models.food;

public class Food {
    private int ID;
    private String Name, Description, Image;
    private Double Price;
    private boolean IsSize, IsAddon;
    private int Discount;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public boolean isSize() {
        return IsSize;
    }

    public void setSize(boolean size) {
        IsSize = size;
    }

    public boolean isAddon() {
        return IsAddon;
    }

    public void setAddon(boolean addon) {
        IsAddon = addon;
    }

    public int getDiscount() {
        return Discount;
    }

    public void setDiscount(int discount) {
        Discount = discount;
    }

    @Override
    public String toString() {
        return "Food{" +
                "ID=" + ID +
                ", Name='" + Name + '\'' +
                ", Description='" + Description + '\'' +
                ", Image='" + Image + '\'' +
                ", Price=" + Price +
                ", IsSize=" + IsSize +
                ", IsAddon=" + IsAddon +
                ", Discount=" + Discount +
                '}';
    }
}
