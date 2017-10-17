package com.beamcalculate.enums;

public class MyMath {
    public static double round(double number, int decimalPlace){
        double result;
        result = Math.round(number * Math.pow(10, decimalPlace));
        result = result / Math.pow(10, decimalPlace);
        return result;
    }
}
