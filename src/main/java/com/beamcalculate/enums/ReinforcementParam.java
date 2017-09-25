package com.beamcalculate.enums;

import com.beamcalculate.Main;

public enum ReinforcementParam {
    a_M("result.moment.paraName.maxMoment", "M_max", "(MN*m)"),
    b_MU("result.moment.paraName.reducedMoment", "Mu", ""),
    c_ALPHA("result.moment.paraName.relativeXPosition", "Alpha", ""),
    d_X("result.moment.paraName.xPosition", "x", "(m)"),
    e_BETA("result.moment.paraName.relativeLeverArm", "Beta", ""),
    f_Z("result.moment.paraName.leverArm", "z", "(m)"),
    g_EPSILON_S("result.moment.paraName.steelStrain", "Epsilon_s", "(%)"),
    h_EPSILON_UK("result.moment.paraName.steelUltimateStrain", "Epsilon_uk", ""),
    i_SIGMA_S("result.moment.paraName.steelStress", "Sigma_s", "(MPa)"),
    j_A_S("result.moment.paraName.rebarSection", "As", "(cm2)"),
    k_PIVOT("result.moment.paraName.pivot", "Pivot", "");


    private String mParaNameBundleKey;
    private String mSymbol;
    private String mUnit;

    ReinforcementParam(String paraNameBundleKey, String symbol, String unit) {
        setParaNameBundleKey(paraNameBundleKey);
        setSymbol(symbol);
        setUnit(unit);
    }

    public void setParaNameBundleKey(String paraNameBundleKey) {
        this.mParaNameBundleKey = paraNameBundleKey;
    }

    public String getParaNameBundleKey() {
        return Main.getBundleText(mParaNameBundleKey);
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        this.mSymbol = symbol;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }
}
