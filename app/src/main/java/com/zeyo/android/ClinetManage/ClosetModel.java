package com.zeyo.android.ClinetManage;

public class ClosetModel {
    public String drawableId;
    public String title;
    public String date;
    public String signup;
    public String id;

    public ClosetModel(String drawableId,
                       String title, String date, String signup, String id){
        this.drawableId = drawableId;
        this.title = title;
        this.date = date;
        this.signup = signup;
        this.id = id;
    }
}