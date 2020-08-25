package com.example.campusgeoquiz.utils;

import android.app.Application;

public class Campus_Geo_Quiz_API extends Application {

    private String username;
    private String userId;
    private static Campus_Geo_Quiz_API instance;

    public static Campus_Geo_Quiz_API getInstance(){
        if ( instance == null)
            instance = new Campus_Geo_Quiz_API();
        return instance;

    }

    public Campus_Geo_Quiz_API(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
