package com.beamcalculate.model.entites;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.HashMap;
import java.util.Map;

public class Load {
    private final DoubleProperty gMNm = new SimpleDoubleProperty();
    private final DoubleProperty qMNm = new SimpleDoubleProperty();
    private final DoubleProperty gTm = new SimpleDoubleProperty();
    private final DoubleProperty qTm = new SimpleDoubleProperty();

    public enum COEF_PSI {
        PSI2(0.3, 0.3, 0.6, 0.6, 0.8, 0.6, 0.3, 0);

        Map<String, Double> mValueMap = new HashMap<>();
        COEF_PSI(double... values) {
            for (int i=0; i<8; i++){
                mValueMap.put("A"+i, values[i]);
            }
        }
    }

    public Load() {
        gMNm.bind(Bindings.divide(gTm, 100));
        qMNm.bind(Bindings.divide(qTm, 100));
    }

    public double getGMNm() {
        return gMNm.get();
    }

    public DoubleProperty gMNmProperty() {
        return gMNm;
    }

    public double getQMNm() {
        return qMNm.get();
    }

    public DoubleProperty qMNmProperty() {
        return qMNm;
    }

    public double getGTm() {
        return gTm.get();
    }

    public DoubleProperty gTmProperty() {
        return gTm;
    }

    public double getQTm() {
        return qTm.get();
    }

    public DoubleProperty qTmProperty() {
        return qTm;
    }
}
