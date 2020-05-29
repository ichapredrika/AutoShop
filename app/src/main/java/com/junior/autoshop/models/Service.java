package com.junior.autoshop.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Service implements Parcelable {
    private String id;
    private String type;
    private String detail;
    private String note;

    public Service(JSONObject object) {
        try {
            this.id = object.optString("SERVICE_ID", "");
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.detail);
        dest.writeString(this.note);
    }

    protected Service(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        this.detail = in.readString();
        this.note = in.readString();
    }

    public static final Parcelable.Creator<Service> CREATOR = new Parcelable.Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel source) {
            return new Service(source);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };
}
