package com.junior.autoshop.models;

import org.json.JSONObject;

public class TransCost {
    private String id;
    private String serviceAct;
    private String price;
    private String transId;

    public TransCost(){}

    public TransCost(JSONObject object) {
        try {
            this.id = object.optString("PRICING_ID", "");
            this.serviceAct = object.optString("SERVICE_ACT", "");
            this.price = object.optString("PRICE", "");
            this.transId = object.optString("TRANSACTION_ID", "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServiceAct() {
        return serviceAct;
    }

    public void setServiceAct(String serviceAct) {
        this.serviceAct = serviceAct;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }
}