package com.beamcalculate.enums;

import com.beamcalculate.model.page_manager.LanguageManager;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public enum NumericalFormat {
    ZERO_DECIMAL("##0", "%.0f"),
    ONE_DECIMAL("##0.0", "%.1f"),
    TWO_DECIMALS("##0.00", "%.2f"),
    THREE_DECIMALS("##0.000", "%.3f"),
    FOUR_DECIMALS("##0.0000", "%.4f");

    private String mDecimalPattern;
    private String mFormatterPattern;

    NumericalFormat(String decimalPattern, String formatterPattern){
        setDecimalFormat(decimalPattern);
        setFormatter(formatterPattern);
    }

    private void setDecimalFormat(String pattern) {
        mDecimalPattern = pattern;
    }

    private void setFormatter(String formatter) {
        mFormatterPattern = formatter;
    }

    public String format(double number){
        DecimalFormat decimalFormat = new DecimalFormat(
                mDecimalPattern,
                new DecimalFormatSymbols(LanguageManager.AppSettings.getCurrentLocal())
        );
        return decimalFormat.format(number);
    }

    public StringBinding format(DoubleProperty doubleProperty) {
        return doubleProperty.asString(LanguageManager.AppSettings.getCurrentLocal(), mFormatterPattern);
    }
}
