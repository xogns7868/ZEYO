package com.zeyo.android.model.consumer.response;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("id")
    private String id="";
    @SerializedName("name")
    private String name="";
    @SerializedName("email")
    private String email="";
    @SerializedName("gender")
    private String gender="";
    @SerializedName("birthYear")
    private String birthYear="";
    @SerializedName("birthMonth")
    private String birthMonth="";
    @SerializedName("birthDay")
    private String birthDay="";
    @SerializedName("ad")
    private String ad="";

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

}
