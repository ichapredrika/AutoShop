package com.junior.autoshop.models;

import org.json.JSONObject;

public class ServiceAutoshop {
    private String id;
    private String serviceId;
    private String type;
    private String detail;
    private boolean isChecked;

    public ServiceAutoshop(JSONObject object) {
        try {
            this.id = object.optString("SA_ID", "");
            this.serviceId = object.optString("SERVICE_ID", "");
            this.type = object.optString("TYPE", "");
            this.detail = object.optString("DETAIL", "");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServiceAutoshop(String id, String serviceId, String type, String detail, boolean isChecked) {
        this.id = id;
        this.serviceId = serviceId;
        this.type = type;
        this.detail = detail;
        this.isChecked = isChecked;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ServiceAutoshop(String id, String serviceId, String type, String detail) {
        this.id = id;
        this.serviceId = serviceId;
        this.type = type;
        this.detail = detail;
    }


}
