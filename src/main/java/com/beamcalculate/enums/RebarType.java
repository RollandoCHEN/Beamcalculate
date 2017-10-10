package com.beamcalculate.enums;

public enum RebarType {
    HA6(6),
    HA8(8),
    HA10(10),
    HA12(12),
    HA14(14),
    HA16(16),
    HA20(20),
    HA25(25),
    HA32(32),
    HA40(40);

    private double mDiameter_mm;

    RebarType(double diameter) {
        setDiameter_mm(diameter);
    }

    public void setDiameter_mm(double diameter_mm) {
        mDiameter_mm = diameter_mm;
    }

    public double getDiameter_mm() {
        return mDiameter_mm;
    }

    public double getSectionalArea_cm2(int numOfRebar){
        return Math.PI * Math.pow(getDiameter_mm() / 2, 2) * numOfRebar /100;
    }

    public double getMassPerMeter_kg(int numOfRebar){
        return 7840 * getSectionalArea_cm2(numOfRebar) / 10000;
    }
}
