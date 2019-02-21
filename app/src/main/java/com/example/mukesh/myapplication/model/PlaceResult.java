package com.example.mukesh.myapplication.model;

/**
 * Created by mukesh on 4/12/18.
 */

public class PlaceResult {
    private String placeId;
    private String icon;
    private String name;
    private String vicinity;

    public PlaceResult() {

    }

    public PlaceResult(String placeId, String icon, String name, String vicinity) {
        this.placeId = placeId;
        this.icon = icon;
        this.name = name;
        this.vicinity = vicinity;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return "PlaceResult{" +
                "placeId='" + placeId + '\'' +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", vicinity='" + vicinity + '\'' +
                '}';
    }
}
