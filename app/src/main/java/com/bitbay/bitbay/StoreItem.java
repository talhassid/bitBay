package com.bitbay.bitbay;

import java.util.ArrayList;

/**
 * Created by roeis on 3/22/2018.
 */

public class StoreItem {
    private double price ;
    private String description ;
    private String imagePath  ;
    private ArrayList<String> categoryList = new ArrayList<>() ;
    private String sellerKey ;
    private String itemKey ;

    StoreItem(double price,String description,String image,
              String sellerKey){
        this.price=price ;
        this.description=description;
        this.imagePath = image;
        this.sellerKey = sellerKey;


    }
    public double getPrice(){
        return this.price;
    }

    public String getDescription(){
        return this.description;
    }

    public String getImagePath(){
        return this.imagePath;
    }

    public String getSellerKey(){
        return this.sellerKey;
    }

    public String getItemKey(){
        return this.itemKey;
    }

    public void setItemKey(String key){
        this.itemKey = key ;
    }
    void addCategory(){

    }
}
