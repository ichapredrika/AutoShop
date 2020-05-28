package com.junior.autoshop.models;

import org.json.JSONObject;

public class Service {
    private String id;
    private String type;
    private String detail;
    private String note;

    public Service(JSONObject object) {
        try {
            this.id = object.optString("SA_ID", "");
            this.type = object.optString("TYPE", "");
            this.detail = object.optString("DETAIL", "");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Service(String id, String type, String detail, String note) {
        this.id = id;
        this.type = type;
        this.detail = detail;
        this.note = note;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
