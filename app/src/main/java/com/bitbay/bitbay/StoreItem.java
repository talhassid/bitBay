package com.bitbay.bitbay;

import java.util.ArrayList;



public class StoreItem {
    private String price ;
    private String description ;
    private String imagePath  ;
    private String categoryList ;
    private String sellerKey ;
    private String address;
    private String itemKey ;
    private ArrayList<String> cartWatchersList;

    StoreItem(String price,String description,String image,
              String sellerKey,String categoryList,String address){
        this.price=price ;
        this.description=description;
        this.imagePath = image;
        this.sellerKey = sellerKey;
        this.address = address;
        this.categoryList = categoryList;
        this.cartWatchersList  = new ArrayList<>();
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

    public String getAddress(){
        return this.address;
    }

    public String getItemKey(){
        return this.itemKey;
    }

    public String getCategoryList() {return this.categoryList;}

    public void setItemKey(String key){
        this.itemKey = key ;
    }

    public void add2CartWatchers(String userCart){
        this.cartWatchersList.add(userCart);
    }

    public void removeFromCartWatchers(String userCart){
        this.cartWatchersList.remove(userCart);
    }

    public ArrayList<String> getCartWatchersList(){
        return (ArrayList<String>) this.cartWatchersList.clone();
    }



    void addCategory(){

    }
}
