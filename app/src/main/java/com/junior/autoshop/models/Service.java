package com.junior.autoshop.models;

import org.json.JSONObject;

public class Service {
    private String id;
    private String type;
    private String detail;

    public Service(JSONObject object) {
        try {
            this.id = object.optString("SA_ID", "");
            this.type = object.optString("TYPE", "");
            this.detail = object.optString("DETAIL", "");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Service(String id, String type, String detail) {
        this.id = id;
        this.type = type;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
