package com.beamcalculate.enums;

import com.beamcalculate.model.page_manager.LanguageManager;

public enum ReinforcementParam {
    a_M("result.moment.paraName.maxMoment", "M\u2098\u2090\u2093", "unit.moment"),
    b_MU("result.moment.paraName.reducedMoment", "M\u1D64", ""),
    c_ALPHA("result.moment.paraName.relativeXPosition", "\u03b1", ""),
    d_X("result.moment.paraName.xPosition", "x", "unit.length.m"),
    e_BETA("result.moment.paraName.relativeLeverArm", "\u03b2", ""),
    f_Z("result.moment.paraName.leverArm", "z", "unit.length.m"),
    g_EPSILON_S("result.moment.paraName.steelStrain", "\u03b5\u209B", "unit.strain.perMille"),
    h_EPSILON_UK("result.moment.paraName.steelUltimateStrain", "\u03b5\u1D64\u2096", "unit.strain.perMille"),
    i_SIGMA_S("result.moment.paraName.steelStress", "\u03c3\u209B", "unit.stress"),
    j_A_S("result.moment.paraName.rebarSectionAea", "A\u209B", "unit.area.cm2"),
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
        return LanguageManager.getBundleText(mParaNameBundleKey);
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        this.mSymbol = symbol;
    }

    public String getUnit() {
        if (!mUnitBundleKey.equals("")) {
            return LanguageManager.getBundleText(mUnitBundleKey);
        } else {
            return "";
        }
    }

    public void setUnitBundleKey(String unitBundleKey) {
        mUnitBundleKey = unitBundleKey;
    }
}
