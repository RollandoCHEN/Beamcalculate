package com.beamcalculate.model;

import com.beamcalculate.enums.RebarType;

public final class RebarType_Number {
    private final RebarType mRebarType;
    private final int mNumberOfRebar;

    public RebarType_Number(RebarType rebarType, int numberOfRebar) {
        mRebarType = rebarType;
        mNumberOfRebar = numberOfRebar;
    }

    public RebarType getRebarType() {
        return mRebarType;
    }

    public int getNumberOfRebar() {
        return mNumberOfRebar;
    }
}
