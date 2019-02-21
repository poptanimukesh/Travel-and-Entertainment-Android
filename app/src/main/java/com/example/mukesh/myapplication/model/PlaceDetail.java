package com.example.mukesh.myapplication.model;

/**
 * Created by mukesh on 4/13/18.
 */

public class PlaceDetail {
    private String placeId;
    private String name;
    private String address;
    private String icon;

    public PlaceDetail(String placeId, String name, String address, String icon) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.icon = icon;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "PlaceDetail{" +
                "placeId='" + placeId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
