package com.bitbay.bitbay;

import java.util.ArrayList;

/**
 * Created by roeis on 3/22/2018.
 */

public class StoreItem {
    private String price ;
    private String description ;
    private String imagePath  ;
    private String categoryList ;
    private String sellerKey ;
    private String itemKey ;

    StoreItem(String price,String description,String image,
              String sellerKey,String categoryList){
        this.price=price ;
        this.description=description;
        this.imagePath = image;
        this.sellerKey = sellerKey;
        this.categoryList = categoryList;

    }
    public String getPrice(){
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

    public String getCategoryList() {return this.categoryList;}

    public void setItemKey(String key){
        this.itemKey = key ;
    }
    void addCategory(){

    }
}
