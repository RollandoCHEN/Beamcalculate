package com.beamcalculate.enums;

import static com.beamcalculate.enums.OrdinalNumber.*;

public class MyMethods {
    public static double round(double number, int decimalPlace){
        double result;
        result = Math.round(number * Math.pow(10, decimalPlace));
        result = result / Math.pow(10, decimalPlace);
        return result;
    }

    public static String getOrdinalNumber(int number){
        switch (number){
            case 1: return FIRST.getOrdinalNumber();
            case 2: return SECOND.getOrdinalNumber();
            case 3: return THIRD.getOrdinalNumber();
            case 4: return FOURTH.getOrdinalNumber();
            case 5: return FIFTH.getOrdinalNumber();
            case 6: return SIXTH.getOrdinalNumber();
            case 7: return SEVENTH.getOrdinalNumber();
            case 8: return EIGHTH.getOrdinalNumber();
            case 9: return NINTH.getOrdinalNumber();
            case 10: return TENTH.getOrdinalNumber();
            default: return "";
        }
    }
}
