package com.zeyo.android.model.Closet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ClosetDetailResponse {
    @SerializedName("id")
    @Expose
    private String id="";
    @SerializedName("name")
    @Expose
    private String name="";
    @SerializedName("image")
    @Expose
    private String image="";
    @SerializedName("subCategoryId")
    @Expose
    private String subCategoryId="";
    @SerializedName("subCategoryName")
    @Expose
    private String subCategoryName="";
    @SerializedName("registerType")
    @Expose
    private String registerType="";
    @SerializedName("createDt")
    @Expose
    private String createDt="";
    public List<wardrobeScmmValueResponses> wardrobeScmmValueResponses = new ArrayList<>();

    public List<wardrobeScmmValueResponses> getwardrobeScmmValueResponses(){
        return wardrobeScmmValueResponses;
    }
    public class wardrobeScmmValueResponses{

        @SerializedName("wardrobeScmmValueId")
        @Expose
        private String wardrobeScmmValueId;
        @SerializedName("measureItemName")
        @Expose
        private String measureItemName;
        @SerializedName("measureItemId")
        @Expose
        private String measureItemId;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("value")
        @Expose
        private String value;

        public String getWardrobeScmmValueId(){
            return wardrobeScmmValueId;
        }
        public String getMeasureItemName(){
            return measureItemName;
        }
        public String getMeasureItemId(){
            return measureItemId;
        }
        public String getCode(){
            return code;
        }
        public String getValue(){
            return value;
        }
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public String getImage(){
        return image;
    }
    public String getSubCategoryId(){
        return subCategoryId;
    }
    public String getSubCategoryName(){
        return subCategoryName;
    }
    public String getRegisterType(){
        return registerType;
    }
    public String getCreateDt(){
        return registerType;
    }
}
