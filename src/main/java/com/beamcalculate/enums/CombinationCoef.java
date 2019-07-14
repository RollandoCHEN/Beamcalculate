package com.beamcalculate.enums;

public enum CombinationCoef {

        G_ELU_UNFAVORABLE_COEF(1.35),
        G_ELU_FAVORABLE_COEF(1.00),
        Q_ELU_UNFAVORABLE_COEF(1.50),
        Q_ELU_FAVORABLE_COEF(0.00),
        G_ELS_COEF(1.00),
        Q_ELS_QUASI_PERMANENT_COEF(0.3);

        private double mValue;

        CombinationCoef(double v) {
            mValue = v;
        }

        public double getValue() {
            return mValue;
        }

}
