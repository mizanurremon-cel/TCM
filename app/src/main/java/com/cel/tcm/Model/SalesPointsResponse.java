package com.cel.tcm.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SalesPointsResponse {
    @SerializedName("value")
    @Expose
    public List<Value> value = null;
    @SerializedName("returnStatus")
    @Expose
    public Integer returnStatus;
    @SerializedName("returnMessage")
    @Expose
    public List<Object> returnMessage = null;

    public class Value {

        @SerializedName("salesPointId")
        @Expose
        public Integer salesPointId;
        @SerializedName("salesPointName")
        @Expose
        public String salesPointName;
        @SerializedName("salesPointCode")
        @Expose
        public String salesPointCode;
        @SerializedName("status")
        @Expose
        public Integer status;

    }
}
