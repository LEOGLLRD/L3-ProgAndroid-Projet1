package com.example.inventairelol.Util;

import androidx.annotation.NonNull;

public class Champion {
    String name;
    String urlImg;

    public Champion(String name, String urlImg) {
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

    @NonNull
    @Override
    public String toString() {
        return this.name + " " + this.urlImg;
    }
}