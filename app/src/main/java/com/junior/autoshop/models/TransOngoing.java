package com.junior.autoshop.models;

import org.json.JSONObject;

public class TransOngoing {
    private String id;
    private String autoshopName;
    private String status;
    private String address;
    private String latlong;
    private String vehicleName;

    public TransOngoing(JSONObject object) {
        try {
            this.id = object.optString("TRANSACTION_ID", "");
            this.autoshopName = object.optString("NAME", "");
            this.status = object.optString("STATUS", "");
            this.address = object.optString("ADDRESS", "");
            this.latlong = object.optString("LATLONG", "");
            this.vehicleName = object.optString("VEHICLE_NAME", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TransOngoing(String id, String name, String status, String address, String latlong, String vehicleName) {
        this.id = id;
        this.autoshopName = name;
        this.status = status;
        this.address = address;
        this.latlong = latlong;
        this.vehicleName = vehicleName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutoshopName() {
        return autoshopName;
    }

    public void setAutoshopName(String autoshopName) {
        this.autoshopName = autoshopName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
}
