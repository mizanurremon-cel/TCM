package com.cel.tcm.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.http.Headers;

public class Login_response {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("loginStatus")
    @Expose
    public Integer loginStatus;
    @SerializedName("loginId")
    @Expose
    public String loginId;
    @SerializedName("userName")
    @Expose
    public String userName;
    @SerializedName("validUser")
    @Expose
    public Boolean validUser;
    @SerializedName("authenticationToken")
    @Expose
    public String authenticationToken;
    @SerializedName("authRequiredAtlogin")
    @Expose
    public Boolean authRequiredAtlogin;
    @SerializedName("authMethod")
    @Expose
    public Integer authMethod;
    @SerializedName("pwdChangeRequired")
    @Expose
    public Boolean pwdChangeRequired;
    @SerializedName("userType")
    @Expose
    public Integer userType;
    @SerializedName("logId")
    @Expose
    public Integer logId;
    @SerializedName("loginTime")
    @Expose
    public String loginTime;
    @SerializedName("salesPointID")
    @Expose
    public Integer salesPointID;
    @SerializedName("menuLayout")
    @Expose
    public String menuLayout;
    @SerializedName("themeName")
    @Expose
    public String themeName;
    @SerializedName("schemeName")
    @Expose
    public String schemeName;
    @SerializedName("moduleIds")
    @Expose
    public List<String> moduleIds = null;

}
