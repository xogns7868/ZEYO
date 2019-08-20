package com.zeyo.android.model.consumer.response;

import com.google.gson.annotations.SerializedName;

public class FindIdResponse {

    @SerializedName("consumerId")
    private String consumerId;

    public String getConsumerId(){
        return consumerId;
    }
}
