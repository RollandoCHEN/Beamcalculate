package com.beamcalculate.enums;

public enum RebarType {
    HA6(6, 1),
    HA8(8, 2),
    HA10(10, 3),
    HA12(12, 4),
    HA14(14, 5),
    HA16(16, 6),
    HA20(20, 7),
    HA25(25, 8),
    HA32(32, 8),
    HA40(40, 9);

    private double mDiameter_mm;
    private int mInnerNumber;

    RebarType(double diameter, int innerNumber) {
        setDiameter_mm(diameter);
        setInnerNumber(innerNumber);
    }

    private void setDiameter_mm(double diameter_mm) {
        mDiameter_mm = diameter_mm;
    }

    public double getDiameter_mm() {
        return mDiameter_mm;
    }

    private void setInnerNumber(int innerNumber) {
        mInnerNumber = innerNumber;
    }

    public int getInnerNumber() {
        return mInnerNumber;
    }

    public double getSectionalArea_cm2(int numOfRebar){
        return Math.PI * Math.pow(getDiameter_mm() / 2, 2) * numOfRebar /100;
    }

    public double getMassPerMeter_kg(int numOfRebar){
        return 7840 * getSectionalArea_cm2(numOfRebar) / 10000;
    }

    public RebarType getRebarTypeOfDiameter(double diameter_mm){
        RebarType rebarType = null;
        for (RebarType type : RebarType.values()){
            if (diameter_mm == type.getDiameter_mm()){
                rebarType = type;
            }
        }
        return rebarType;
    }
}
