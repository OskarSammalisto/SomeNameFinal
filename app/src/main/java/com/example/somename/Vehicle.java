package com.example.somename;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable {
//ss
    private String name;
    private ArrayList images;
    private String description;
    private String uri;
    private LatLng latLng;
    private String[] stringLatLng;

    public String getVehiclesRef() {
        return vehiclesRef;
    }

    public void setVehiclesRef(String vehiclesRef) {
        this.vehiclesRef = vehiclesRef;
    }

    private String vehiclesRef;



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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String[] getStringLatLng() {
        return stringLatLng;
    }

    public LatLng getStringLatLngAsLatLng() {
        double latitude = Double.parseDouble(stringLatLng[0]);
        double longitude = Double.parseDouble(stringLatLng[1]);
        LatLng latLngTemp = new LatLng(latitude, longitude);
        return latLngTemp;
    }

    public void setStringLatLng(String[] stringLatLng) {
        this.stringLatLng = stringLatLng;
    }
}
