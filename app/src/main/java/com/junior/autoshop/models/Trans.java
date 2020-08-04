package com.junior.autoshop.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Trans implements Parcelable {
    private String id;
    private String startDate;
    private String finishDate;
    private String movementOption;
    private String latlong;
    private String autoshopId;
    private String pickupOption;
    private String progress;
    private String spaceNumber;
    private String type;
    private String totalPrice;
    private String autoshopName;
    private String autoshopEmail;
    private String status;
    private String autoshopAddress;
    private String autoshopLatlong;
    private String autoshopBank;
    private String autoshopAccountNumber;
    private String vehicleName;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleId;
    private String vhId;
    private String adminContact;
    private String pickupContact;
    private String customerName;
    private String customerEmail;
    private String customerId;
    private String customerContact;
    private String deliveryFee;
    private String overnightFee;
    private String latlongDelivery;
    private String paymentProof;
    private String pickupDate;
    private String pickupTime;

    public Trans() {
    }

    public Trans(JSONObject object) {
        try {
            this.id = object.optString("TRANSACTION_ID", "");
            this.startDate = object.optString("START_DATE", "");
            this.finishDate = object.optString("FINISH_DATE", "");
            this.movementOption = object.optString("MOVEMENT_OPTION", "");
            this.latlong = object.optString("LATLONG", "");
            this.autoshopId = object.optString("AUTOSHOP_ID", "");
            this.pickupOption = object.optString("PICKUP_OPTION", "");
            this.progress = object.optString("PROGRESS", "");
            this.spaceNumber = object.optString("SPACE_NUMBER", "");
            this.type = object.optString("TYPE", "");
            this.totalPrice = object.optString("TOTAL_PRICE", "");
            this.autoshopName = object.optString("NAME", "");
            this.autoshopBank = object.optString("BANK", "");
            this.autoshopAccountNumber = object.optString("ACCOUNT_NUMBER", "");
            this.status = object.optString("STATUS", "");
            this.autoshopAddress = object.optString("ADDRESS", "");
            this.autoshopLatlong = object.optString("AUTOSHOP_LATLONG", "");
            this.vehicleName = object.optString("VEHICLE_NAME", "");
            this.vehicleBrand = object.optString("BRAND", "");
            this.vehicleModel = object.optString("MODEL", "");
            this.adminContact = object.optString("ADMIN_CONTACT", "");
            this.pickupContact = object.optString("PICKUP_CONTACT", "");
            this.customerName = object.optString("FULLNAME", "");
            this.customerContact = object.optString("PHONE", "");
            this.deliveryFee = object.optString("DELIVERY_FEE", "");
            this.overnightFee = object.optString("OVERNIGHT_FEE", "");
            this.latlongDelivery = object.optString("LATLONG_DELIVERY", "");
            this.paymentProof = object.optString("PAYMENT_PROOF", "");
            this.pickupDate = object.optString("PICKUP_TIME", "");
            this.pickupTime = object.optString("PICKUP_DATE", "");
            this.vehicleId = object.optString("VEHICLE_ID", "");
            this.vhId = object.optString("VH_ID", "");
            this.customerId = object.optString("CUSTOMER_ID", "");
            this.autoshopEmail = object.optString("AUTOSHOP_EMAIL", "");
            this.customerEmail = object.optString("CUSTOMER_EMAIL", "");
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

    public String getAutoshopEmail() {
        return autoshopEmail;
    }

    public void setAutoshopEmail(String autoshopEmail) {
        this.autoshopEmail = autoshopEmail;
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

    public String getAutoshopBank() {
        return autoshopBank;
    }

    public void setAutoshopBank(String autoshopBank) {
        this.autoshopBank = autoshopBank;
    }

    public String getAutoshopAccountNumber() {
        return autoshopAccountNumber;
    }

    public void setAutoshopAccountNumber(String autoshopAccountNumber) {
        this.autoshopAccountNumber = autoshopAccountNumber;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVhId() {
        return vhId;
    }

    public void setVhId(String vhId) {
        this.vhId = vhId;
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

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getOvernightFee() {
        return overnightFee;
    }

    public void setOvernightFee(String overnightFee) {
        this.overnightFee = overnightFee;
    }

    public String getLatlongDelivery() {
        return latlongDelivery;
    }

    public void setLatlongDelivery(String latlongDelivery) {
        this.latlongDelivery = latlongDelivery;
    }

    public String getPaymentProof() {
        return paymentProof;
    }

    public void setPaymentProof(String paymentProof) {
        this.paymentProof = paymentProof;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.startDate);
        dest.writeString(this.finishDate);
        dest.writeString(this.movementOption);
        dest.writeString(this.latlong);
        dest.writeString(this.autoshopId);
        dest.writeString(this.pickupOption);
        dest.writeString(this.progress);
        dest.writeString(this.spaceNumber);
        dest.writeString(this.type);
        dest.writeString(this.totalPrice);
        dest.writeString(this.autoshopName);
        dest.writeString(this.autoshopEmail);
        dest.writeString(this.status);
        dest.writeString(this.autoshopAddress);
        dest.writeString(this.autoshopLatlong);
        dest.writeString(this.autoshopBank);
        dest.writeString(this.autoshopAccountNumber);
        dest.writeString(this.vehicleName);
        dest.writeString(this.vehicleBrand);
        dest.writeString(this.vehicleModel);
        dest.writeString(this.vehicleId);
        dest.writeString(this.vhId);
        dest.writeString(this.adminContact);
        dest.writeString(this.pickupContact);
        dest.writeString(this.customerName);
        dest.writeString(this.customerEmail);
        dest.writeString(this.customerId);
        dest.writeString(this.customerContact);
        dest.writeString(this.deliveryFee);
        dest.writeString(this.overnightFee);
        dest.writeString(this.latlongDelivery);
        dest.writeString(this.paymentProof);
        dest.writeString(this.pickupDate);
        dest.writeString(this.pickupTime);
    }

    protected Trans(Parcel in) {
        this.id = in.readString();
        this.startDate = in.readString();
        this.finishDate = in.readString();
        this.movementOption = in.readString();
        this.latlong = in.readString();
        this.autoshopId = in.readString();
        this.pickupOption = in.readString();
        this.progress = in.readString();
        this.spaceNumber = in.readString();
        this.type = in.readString();
        this.totalPrice = in.readString();
        this.autoshopName = in.readString();
        this.autoshopEmail = in.readString();
        this.status = in.readString();
        this.autoshopAddress = in.readString();
        this.autoshopLatlong = in.readString();
        this.autoshopBank = in.readString();
        this.autoshopAccountNumber = in.readString();
        this.vehicleName = in.readString();
        this.vehicleBrand = in.readString();
        this.vehicleModel = in.readString();
        this.vehicleId = in.readString();
        this.vhId = in.readString();
        this.adminContact = in.readString();
        this.pickupContact = in.readString();
        this.customerName = in.readString();
        this.customerEmail = in.readString();
        this.customerId = in.readString();
        this.customerContact = in.readString();
        this.deliveryFee = in.readString();
        this.overnightFee = in.readString();
        this.latlongDelivery = in.readString();
        this.paymentProof = in.readString();
        this.pickupDate = in.readString();
        this.pickupTime = in.readString();
    }

    public static final Creator<Trans> CREATOR = new Creator<Trans>() {
        @Override
        public Trans createFromParcel(Parcel source) {
            return new Trans(source);
        }

        @Override
        public Trans[] newArray(int size) {
            return new Trans[size];
        }
    };
}