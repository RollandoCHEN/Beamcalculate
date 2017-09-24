package com.beamcalculate.enums;

import com.beamcalculate.Main;

public enum MethodName {
    CAQUOT(Main.getBundleText("enum.method.caquot")),
    CAQUOT_MINOREE(Main.getBundleText("enum.method.caquot.reduced")),
    FORFAITAIRE(Main.getBundleText("enum.method.forfaitaire")),
    TROIS_MOMENT(Main.getBundleText("enum.method.threeMoment")),
    TROIS_MOMENT_R(Main.getBundleText("enum.method.threeMoment.redistribution"));

    private String mMethodName;

    MethodName(String methodName) {
        mMethodName = methodName;
    }

    public String getMethodName() {
        return mMethodName;
    }
}
