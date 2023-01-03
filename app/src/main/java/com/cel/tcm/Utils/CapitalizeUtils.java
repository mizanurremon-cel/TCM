package com.cel.tcm.Utils;

public class CapitalizeUtils {

    public static String doCapFirst(String value){
        StringBuilder sb = new StringBuilder(value);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}
