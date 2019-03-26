package com.example.somename;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable, Parcelable {
//ss
    private String name;
    private String description;
    private String uriString;
    private double latitude;
    private double longitude;

    private ArrayList images;



    public Vehicle(){}

    public Vehicle(String name, String description, String uriString, Double latitude, Double longitude) {
        this.name = name;
        this.description = description;
        this.uriString = uriString;
        this.latitude = latitude;
        this.longitude = longitude;



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
        return uriString;
    }

    public void setUri(String uri) {
        this.uriString = uri;
    }

    public LatLng getLatLng() {

        LatLng latLng = new LatLng(this.latitude, this.longitude);

        return latLng;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
    }

    //parcelable section


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(uriString);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);

    }

    public static final Parcelable.Creator<Vehicle> CREATOR
            = new Parcelable.Creator<Vehicle>() {
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    private Vehicle(Parcel in) {
        name = in.readString();
        description = in.readString();
        uriString = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();

    }

}
