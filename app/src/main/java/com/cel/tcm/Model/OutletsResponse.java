package com.cel.tcm.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OutletsResponse {
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

        @SerializedName("outletId")
        @Expose
        public Integer outletId;
        @SerializedName("code")
        @Expose
        public String code;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("contactNo")
        @Expose
        public String contactNo;
        @SerializedName("mhNodeId")
        @Expose
        public Integer mhNodeId;

    }
}
