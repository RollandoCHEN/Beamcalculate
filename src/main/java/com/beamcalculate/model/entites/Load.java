package com.beamcalculate.model.entites;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Load {
    private final DoubleProperty gMNm = new SimpleDoubleProperty();
    private final DoubleProperty qMNm = new SimpleDoubleProperty();
    private final DoubleProperty gTm = new SimpleDoubleProperty();
    private final DoubleProperty qTm = new SimpleDoubleProperty();

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
