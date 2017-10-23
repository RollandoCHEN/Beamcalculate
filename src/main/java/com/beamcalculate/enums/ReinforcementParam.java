package com.beamcalculate.enums;

import com.beamcalculate.Main;

public enum ReinforcementParam {
    a_M("result.moment.paraName.maxMoment", "M_max", "unit.moment"),
    b_MU("result.moment.paraName.reducedMoment", "Mu", ""),
    c_ALPHA("result.moment.paraName.relativeXPosition", "Alpha", ""),
    d_X("result.moment.paraName.xPosition", "x", "unit.length.m"),
    e_BETA("result.moment.paraName.relativeLeverArm", "Beta", ""),
    f_Z("result.moment.paraName.leverArm", "z", "unit.length.m"),
    g_EPSILON_S("result.moment.paraName.steelStrain", "Epsilon_s", "unit.strain.perMille"),
    h_EPSILON_UK("result.moment.paraName.steelUltimateStrain", "Epsilon_uk", "unit.strain.perMille"),
    i_SIGMA_S("result.moment.paraName.steelStress", "Sigma_s", "unit.stress"),
    j_A_S("result.moment.paraName.rebarSectionAea", "As", "unit.area.cm2"),
    k_PIVOT("result.moment.paraName.pivot", "Pivot", "");


    private String mParaNameBundleKey;
    private String mSymbol;
    private String mUnitBundleKey;

    ReinforcementParam(String paraNameBundleKey, String symbol, String unitBundleKey) {
        setParaNameBundleKey(paraNameBundleKey);
        setSymbol(symbol);
        setUnitBundleKey(unitBundleKey);
    }

    public void setParaNameBundleKey(String paraNameBundleKey) {
        this.mParaNameBundleKey = paraNameBundleKey;
    }

    public String getParaName() {
        return Main.getBundleText(mParaNameBundleKey);
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        this.mSymbol = symbol;
    }

    public String getUnit() {
        if (!mUnitBundleKey.equals("")) {
            return Main.getBundleText(mUnitBundleKey);
        } else {
            return "";
        }
    }

    public void setUnitBundleKey(String unitBundleKey) {
        mUnitBundleKey = unitBundleKey;
    }
}
