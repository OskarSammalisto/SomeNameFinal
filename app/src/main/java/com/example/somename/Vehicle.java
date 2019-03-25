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
    private Uri uriReal;
    private LatLng latLng;

    private ArrayList images;
    private String uriString;
    private String[] stringLatLng;

    public String getVehiclesRef() {
        return vehiclesRef;
    }

    public void setVehiclesRef(String vehiclesRef) {
        this.vehiclesRef = vehiclesRef;
    }

    private String vehiclesRef;



    public Vehicle(){}

    public Vehicle(String name, String description, Uri uri) {
        this.name = name;
        this.description = description;
        this.uriReal = uri;

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
        return uriString;
    }

    public void setUri(String uri) {
        this.uriString = uri;
    }

    public Uri getUriReal() {
        return uriReal;
    }

    public void setUriReal(Uri uriReal) {
        this.uriReal = uriReal;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeValue(uriReal);
        dest.writeValue(latLng);
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
      //  mData = in.readInt();
        name = in.readString();
        description = in.readString();
        uriReal = (Uri) in.readValue(Uri.class.getClassLoader());
        latLng = (LatLng) in.readValue(LatLng.class.getClassLoader());
    }

}
