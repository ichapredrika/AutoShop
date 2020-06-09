package com.junior.autoshop.models;

import org.json.JSONObject;

public class Trans {
    private String id;
    private String startDate;
    private String finishDate;
    private String movementOption;
    private String location;
    private String latlong;
    private String autoshopId;
    private String pickupOption;
    private String progress;
    private String spaceNumber;
    private String type;
    private String totalPrice;
    private String autoshopName;
    private String status;
    private String autoshopAddress;
    private String autoshopLatlong;
    private String vehicleName;
    private String adminContact;
    private String pickupContact;
    private String customerName;
    private String customerContact;

    public Trans(){}

    public Trans(JSONObject object) {
        try {
            this.id = object.optString("TRANSACTION_ID", "");
            this.startDate = object.optString("START_DATE", "");
            this.finishDate = object.optString("FINISH_DATE", "");
            this.movementOption = object.optString("MOVEMENT_OPTION", "");
            this.location = object.optString("LOCATION", "");
            this.latlong = object.optString("LATLONG", "");
            this.autoshopId = object.optString("AUTOSHOP_ID", "");
            this.pickupOption = object.optString("PICKUP_OPTION", "");
            this.progress = object.optString("PROGRESS", "");
            this.spaceNumber = object.optString("SPACE_NUMBER", "");
            this.type = object.optString("TYPE", "");
            this.totalPrice = object.optString("TOTAL_PRICE", "");
            this.autoshopName = object.optString("NAME", "");
            this.status = object.optString("STATUS", "");
            this.autoshopAddress = object.optString("ADDRESS", "");
            this.autoshopLatlong = object.optString("AUTOSHOP_LATLONG", "");
            this.vehicleName = object.optString("VEHICLE_NAME", "");
            this.adminContact = object.optString("ADMIN_CONTACT", "");
            this.pickupContact = object.optString("PICKUP_CONTACT", "");
            this.customerName = object.optString("FULLNAME", "");
            this.customerContact = object.optString("PHONE", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getMovementOption() {
        return movementOption;
    }

    public void setMovementOption(String movementOption) {
        this.movementOption = movementOption;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getAutoshopId() {
        return autoshopId;
    }

    public void setAutoshopId(String autoshopId) {
        this.autoshopId = autoshopId;
    }

    public String getPickupOption() {
        return pickupOption;
    }

    public void setPickupOption(String pickupOption) {
        this.pickupOption = pickupOption;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getSpaceNumber() {
        return spaceNumber;
    }

    public void setSpaceNumber(String spaceNumber) {
        this.spaceNumber = spaceNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
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

    public String getAutoshopAddress() {
        return autoshopAddress;
    }

    public void setAutoshopAddress(String autoshopAddress) {
        this.autoshopAddress = autoshopAddress;
    }

    public String getAutoshopLatlong() {
        return autoshopLatlong;
    }

    public void setAutoshopLatlong(String autoshopLatlong) {
        this.autoshopLatlong = autoshopLatlong;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getAdminContact() {
        return adminContact;
    }

    public void setAdminContact(String adminContact) {
        this.adminContact = adminContact;
    }

    public String getPickupContact() {
        return pickupContact;
    }

    public void setPickupContact(String pickupContact) {
        this.pickupContact = pickupContact;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }
}
