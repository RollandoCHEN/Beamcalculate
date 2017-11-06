package com.beamcalculate.enums;

import static com.beamcalculate.enums.NumericalFormat.THREE_DECIMALS;

public enum Pivots {
    PIVOTA("Pivot A", 0, 0.056),
    PIVOTB("Pivot B", 0.056, 0.48),
    PIVOTC("Pivot C", 0, 0);

    private String mName;
    private double mLeftLimit;
    private double mRightLimit;

    Pivots(String name, double leftLimit, double rightLimit) {
        setName(name);
        setLeftLimit(leftLimit);
        setRightLimit(rightLimit);
    }

    public String getContent() {
        String content;
        ;
        if (mLeftLimit != mRightLimit) {
            content = mName + " (" +
                    (mLeftLimit!=0 ? THREE_DECIMALS.format(mLeftLimit)+ " < " : "")
                    + "Mu" +
                    (mRightLimit!=0 ? " < " + THREE_DECIMALS.format(mRightLimit) : "") +
                    ")";
        } else {
            content = mName;
        }
        return content;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setLeftLimit(double leftLimit) {
        mLeftLimit = leftLimit;
    }

    public void setRightLimit(double rightLimit) {
        mRightLimit = rightLimit;
    }
}
