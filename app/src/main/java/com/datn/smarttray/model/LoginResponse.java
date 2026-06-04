package com.datn.smarttray.model;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("role")
    private String role;
    @SerializedName("username")
    private String username;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRole() {
        return role;
    }
    public String getUserName(){
        return username;
    }
}

