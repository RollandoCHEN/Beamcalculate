package com.beamcalculate.model.entites;

import com.beamcalculate.model.NamedDoubleProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Load {
    private static final NamedDoubleProperty gMNm = new NamedDoubleProperty();
    private static final NamedDoubleProperty qMNm = new NamedDoubleProperty();
    private static final NamedDoubleProperty gTm = new NamedDoubleProperty();
    private static final NamedDoubleProperty qTm = new NamedDoubleProperty();

    public Load() {
        gMNm.bind(Bindings.divide(gTm, 100));
        qMNm.bind(Bindings.divide(qTm, 100));
    }

    public static double getGMNm() {
        return gMNm.get();
    }

    public static NamedDoubleProperty gMNmProperty() {
        return gMNm;
    }

    public static double getQMNm() {
        return qMNm.get();
    }

    public static NamedDoubleProperty qMNmProperty() {
        return qMNm;
    }

    public static double getGTm() {
        return gTm.get();
    }

    public static NamedDoubleProperty gTmProperty() {
        return gTm;
    }

    public static double getQTm() {
        return qTm.get();
    }

    public static NamedDoubleProperty qTmProperty() {
        return qTm;
    }
}
