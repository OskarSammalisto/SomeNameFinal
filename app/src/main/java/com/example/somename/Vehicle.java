package com.example.somename;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable {
//ss
    private String name;
    private int logo;
    private ArrayList images;
    private String description;
    private Bitmap bitmapLogo;


    public Vehicle(String name, String description, int logo) {
        this.name = name;
        this.logo = logo;
        this.description = description;
//        this.bitmapLogo = bitmapLogo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public ArrayList getImages() {
        return images;
    }

    public void setImages(ArrayList images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getBitmapLogo() {
        return bitmapLogo;
    }

    public void setBitmapLogo(Bitmap bitmapLogo) {
        this.bitmapLogo = bitmapLogo;
    }
}
