package com.example.somename;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable {
//ss
    private String name;
    private ArrayList images;
    private String description;
    private String uri;


    public Vehicle(){}

    public Vehicle(String name, String description) {
        this.name = name;
        this.description = description;
       // this.uri = uri;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
