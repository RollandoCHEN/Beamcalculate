package com.beamcalculate.enums;

public enum PartialCoef {

    Q_ELS_QUASI_PERMANENT_COEF(0.3);

    private double mValue;

    PartialCoef(double v) {
        mValue = v;
    }

    public double getValue() {
        return mValue;
    }

}
