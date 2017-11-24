package com.vany.tulin.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by van元 on 2017/2/14.
 */

public class CookMenu implements Parcelable{
    private String cookName;
    private String cookInfo;
    private String cookUrl;

    public CookMenu() {
    }
    public CookMenu(String cookName, String cookInfo, String cookUrl) {
        this.cookName = cookName;
        this.cookInfo = cookInfo;
        this.cookUrl = cookUrl;
    }

    protected CookMenu(Parcel in) {
        cookName = in.readString();
        cookInfo = in.readString();
        cookUrl = in.readString();
    }
    public static final Creator<CookMenu> CREATOR = new Creator<CookMenu>() {
        @Override
        public CookMenu createFromParcel(Parcel in) {
            return new CookMenu(in);
        }

        @Override
        public CookMenu[] newArray(int size) {
            return new CookMenu[size];
        }
    };

    public String getCookName() {
        return cookName;
    }

    public void setCookName(String cookName) {
        this.cookName = cookName;
    }

    public String getCookInfo() {
        return cookInfo;
    }

    public void setCookInfo(String cookInfo) {
        this.cookInfo = cookInfo;
    }

    public String getCookUrl() {
        return cookUrl;
    }

    public void setCookUrl(String cookUrl) {
        this.cookUrl = cookUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cookName);
        dest.writeString(cookInfo);
        dest.writeString(cookUrl);
    }
}
