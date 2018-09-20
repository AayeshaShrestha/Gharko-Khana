package com.kthdvs.gharkokhana;

import android.app.LocalActivityManager;

/**
 * Created by aayeshs on 1/18/18.
 */

public class foodPosts {

    private String DishName;
    private String Description;
    private String Price;
    private String OrdersNo;
    private String Delivery;
    private String image;
    private String username;
    private String Location;


    public foodPosts() {
    }

    public foodPosts(String nameOfDish, String description, String price, String orders, String delivery, String image, String username, String Location) {
        this.DishName = nameOfDish;
        this.Description = description;
        this.Price = price;
        this.OrdersNo = orders;
        this.Delivery = delivery;
        this.image = image;
        this.username = username;
        this.Location = Location;

    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDishName() {
        return DishName;
    }

    public void setDishName(String dishName) {
        DishName = dishName;
    }

    public String getOrdersNo() {
        return OrdersNo;
    }

    public void setOrdersNo(String ordersNo) {
        OrdersNo = ordersNo;
    }

    public String getDelivery() {
        return Delivery;
    }

    public void setDelivery(String delivery) {
        Delivery = delivery;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        this.Price = price;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

