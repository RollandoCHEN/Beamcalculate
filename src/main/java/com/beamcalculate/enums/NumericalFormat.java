package com.beamcalculate.enums;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public enum NumericalFormat {
    ZERODECIMAL("##0"),
    ONEDECIMAL("##0.0"),
    TWODECIMALS("##0.00"),
    THREEDECIMALS("##0.000"),
    FOURDECIMALS("##0.0000");

    private DecimalFormat mPattern;
    //TODO Generate the numerical format according to the selected local language
    private Locale mCurrentLocale = new Locale("en", "US");

    NumericalFormat(String pattern){setPattern(pattern);}

    public DecimalFormat getDecimalFormat() {
        return mPattern;
    }

    public void setPattern(String pattern) {
        mPattern = new DecimalFormat(pattern, new DecimalFormatSymbols(mCurrentLocale));;
    }
}
