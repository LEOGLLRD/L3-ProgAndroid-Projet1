package com.example.inventairelol.Util;



public class Item {

    String name;
    String urlImg;

    public Item(String name, String urlImg) {
        this.name = name;
        this.urlImg = urlImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }


    @Override
    public String toString() {
        return this.name + " " + this.urlImg;
    }
}
