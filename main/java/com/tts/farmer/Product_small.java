package com.tts.farmer;

import com.google.firebase.Timestamp;

/**
 * Created by THAMS on 18-Mar-18.
 */

public class Product_small extends pushid {

    int price,quantity;
    String quantity_type;
    String product;
    String image;

    public Product_small() {
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getQuantity_type() {
        return quantity_type;
    }

    public String getProduct() {
        return product;
    }

    public String getImage() {
        return image;
    }

    public Product_small(int price, int quantity, String quantity_type, String product, String image) {
        this.price = price;
        this.quantity = quantity;
        this.quantity_type = quantity_type;
        this.product = product;
        this.image = image;
    }
}
