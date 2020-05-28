package com.junior.autoshop.models;

import org.json.JSONObject;

public class Vehicle {
    private String id;
    private String brand;
    private String model;
    private String brandModel;

    public Vehicle(JSONObject object) {
        try {
            this.id = object.optString("VEHICLE_ID", "");
            this.brand = object.optString("BRAND", "");
            this.model = object.optString("MODEL", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vehicle(String id, String brand, String model, String brandModel) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.brandModel = brandModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrandModel() {
        return brandModel;
    }

    public void setBrandModel(String brandModel) {
        this.brandModel = brandModel;
    }
}
