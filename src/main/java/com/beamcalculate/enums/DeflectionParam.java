package com.beamcalculate.enums;

import com.beamcalculate.model.page_manager.LanguageManager;

public enum DeflectionParam {
    a_A_S("result.deflection.paraName.rebar_area", "A\u209B", "unit.area.cm2"),
    b_RHO("result.deflection.paraName.bottom_rebar_percentage", "\u03C1", "unit.ratio.percentage"),
    c_LIMIT_L_D("result.deflection.paraName.limit_l_d_ratio", "L/D", ""),
    d_L_D("result.deflection.paraName.l_d_ratio", "L/D", "");


    private String mParaNameBundleKey;
    private String mSymbol;
    private String mUnitBundleKey;

    DeflectionParam(String paraNameBundleKey, String symbol, String unitBundleKey) {
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

    public String getUnit(boolean withBracket){
        if (withBracket && !mUnitBundleKey.equals("")){
            return " (" + getUnit() + ")";
        } else {
            return getUnit();
        }
    }

    public void setUnitBundleKey(String unitBundleKey) {
        mUnitBundleKey = unitBundleKey;
    }
}
