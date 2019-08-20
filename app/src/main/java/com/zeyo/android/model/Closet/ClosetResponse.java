package com.zeyo.android.model.Closet;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ClosetResponse {
    public List<wardrobeResponses> wardrobeResponses = new ArrayList<>();

    public List<wardrobeResponses> getWardrobeResponses(){
        return wardrobeResponses;
    }
    @SerializedName("totalElements")
    @Expose
    private int totalElements;
    @SerializedName("size")
    @Expose
    private int size;
    public int getTotalElements(){
        return totalElements;
    }
    public int getSize(){
        return size;
    }


    public class wardrobeResponses{
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
            return createDt;
        }
    }
}