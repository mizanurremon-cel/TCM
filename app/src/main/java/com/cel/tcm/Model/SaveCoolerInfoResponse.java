package com.cel.tcm.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SaveCoolerInfoResponse {
    @SerializedName("value")
    @Expose
    public Boolean value;
    @SerializedName("returnStatus")
    @Expose
    public Integer returnStatus;

    @SerializedName("returnMessage")
    @Expose
    public List<String> returnMessage = null;

}
