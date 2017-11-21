package com.beamcalculate.model;

import com.beamcalculate.enums.RebarType;

public final class RebarType_Amount {
    private final RebarType mRebarType;
    private final int mNumberOfRebar;

    public RebarType_Amount(RebarType rebarType, int numberOfRebar) {
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
