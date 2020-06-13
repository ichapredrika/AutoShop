package com.junior.autoshop.models;

import org.json.JSONObject;

public class TransCost {
    private String id;
    private String serviceId;
    private String price;
    private String type;
    private String detail;
    private String transId;

    public TransCost(){}

    public TransCost(JSONObject object) {
        try {
            this.id = object.optString("PRICING_ID", "");
            this.serviceId = object.optString("SERVICE_ID", "");
            this.price = object.optString("PRICE", "");
            this.type = object.optString("TYPE", "");
            this.detail = object.optString("DETAIL", "");
            this.transId = object.optString("TRANSACTION_ID", "");

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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }
}