package com.beamcalculate.enums;

import com.beamcalculate.BeamCalculatorApp;
import com.beamcalculate.model.LanguageManager;

public enum CalculateMethod {
    CAQUOT("enum.method.caquot"),
    CAQUOT_MINOREE("enum.method.caquot.reduced"),
    FORFAITAIRE("enum.method.forfaitaire"),
    TROIS_MOMENT("enum.method.threeMoment"),
    TROIS_MOMENT_R("enum.method.threeMoment.redistribution");

    private String mBundleTextKey;

    CalculateMethod(String bundleTextKey) {
        mBundleTextKey = bundleTextKey;
    }

    public String getMethodName() {
        return LanguageManager.getBundleText(mBundleTextKey);
    }
}