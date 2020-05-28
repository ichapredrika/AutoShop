package com.junior.autoshop.models;

import org.json.JSONObject;

public class VehicleCustomer {
    private String id;
    private String vehicleId;
    private String name;
    private String brand;
    private String model;

    public VehicleCustomer(){}
    public VehicleCustomer(JSONObject object) {
        try {
            this.id = object.optString("VH_ID", "");
            this.vehicleId = object.optString("VEHICLE_ID", "");
            this.name = object.optString("VEHICLE_NAME", "");
            this.brand = object.optString("BRAND", "");
            this.model = object.optString("MODEL", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VehicleCustomer(String id, String vehicleId, String name, String brand, String model) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.name = name;
        this.brand = brand;
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
