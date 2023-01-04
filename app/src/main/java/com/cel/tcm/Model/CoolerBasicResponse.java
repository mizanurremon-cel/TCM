package com.cel.tcm.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoolerBasicResponse {

    @SerializedName("value")
    @Expose
    public List<Value> value = null;
    @SerializedName("returnStatus")
    @Expose
    public Integer returnStatus;
    @SerializedName("returnMessage")
    @Expose
    public List<Object> returnMessage = null;

    public static class Value {

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

        public Value(Integer posmAssetId, String assetCode, String assetProperty1, String assetProperty2, String assetProperty3) {
            this.posmAssetId = posmAssetId;
            this.assetCode = assetCode;
            this.assetProperty1 = assetProperty1;
            this.assetProperty2 = assetProperty2;
            this.assetProperty3 = assetProperty3;
        }
    }
}
