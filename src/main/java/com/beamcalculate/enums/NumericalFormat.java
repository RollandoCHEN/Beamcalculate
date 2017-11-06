package com.beamcalculate.enums;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Formatter;
import java.util.Locale;

public enum NumericalFormat {
    ZERODECIMAL("##0"),
    ONEDECIMAL("##0.0"),
    TWODECIMALS("##0.00"),
    THREEDECIMALS("##0.000"),
    FOURDECIMALS("##0.0000");

    private DecimalFormat mFormat;
    //TODO Generate the numerical format according to the selected local language
    private Locale mCurrentLocale = new Locale("en", "US");

    NumericalFormat(String pattern){
        setDecimalFormat(pattern);
    }

    private void setDecimalFormat(String pattern) {
        mFormat = new DecimalFormat(pattern, new DecimalFormatSymbols(mCurrentLocale));
    }

    public String format(double number){
        return mFormat.format(number);
    }
}
