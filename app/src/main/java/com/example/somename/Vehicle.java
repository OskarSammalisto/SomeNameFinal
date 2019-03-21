package com.example.somename;

import java.util.ArrayList;

public class Vehicle {
//ss
    private String name;
    private int logo;
    private ArrayList images;


    public Vehicle(String name, int logo) {
        this.name = name;
        this.logo = logo;
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
}
