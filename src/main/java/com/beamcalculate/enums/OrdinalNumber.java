package com.beamcalculate.enums;

import com.beamcalculate.BeamCalculatorApp;

public enum OrdinalNumber {
    FIRST("ordinalNumber.1st"),
    SECOND("ordinalNumber.2nd"),
    THIRD("ordinalNumber.3rd"),
    FOURTH(""),
    FIFTH(""),
    SIXTH(""),
    SEVENTH(""),
    EIGHTH(""),
    NINTH(""),
    TENTH("");

    private String mBundleTextKey;

    OrdinalNumber(String bundleTextKey) {
        mBundleTextKey = bundleTextKey;
    }

    public String getOrdinalNumber() {
        return BeamCalculatorApp.getBundleText(mBundleTextKey);
    }
}
