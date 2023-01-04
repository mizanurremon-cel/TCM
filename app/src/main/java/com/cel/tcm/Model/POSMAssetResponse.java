package com.cel.tcm.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class POSMAssetResponse {

    @SerializedName("posmAssetId")
    @Expose
    public Integer posmAssetId;
    @SerializedName("assetCode")
    @Expose
    public String assetCode;
    @SerializedName("assetProperty1")
    @Expose
    public String assetProperty1;
    @SerializedName("assetProperty2")
    @Expose
    public String assetProperty2;
    @SerializedName("assetProperty3")
    @Expose
    public String assetProperty3;
    @SerializedName("placementDate")
    @Expose
    public String placementDate;
    @SerializedName("picture")
    @Expose
    public String picture;
    @SerializedName("remarks")
    @Expose
    public String remarks;
    @SerializedName("latitude")
    @Expose
    public Double latitude;
    @SerializedName("longitude")
    @Expose
    public Double longitude;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("assetStatus")
    @Expose
    public Integer assetStatus;
    @SerializedName("returnStatus")
    @Expose
    public Integer returnStatus;
    @SerializedName("returnMessage")
    @Expose
    public List<Object> returnMessage = null;
}
