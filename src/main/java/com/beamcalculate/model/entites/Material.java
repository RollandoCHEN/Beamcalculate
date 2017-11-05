package com.beamcalculate.model.entites;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Material {
    public static double CONCRETE_COEF = 1.5;
    public static double STEEL_COEF = 1.15;

    private final DoubleProperty fck = new SimpleDoubleProperty();
    private final DoubleProperty fcd = new SimpleDoubleProperty();
    private final DoubleProperty fyk = new SimpleDoubleProperty();
    private final DoubleProperty fyd = new SimpleDoubleProperty();
    private final StringProperty ductibilityClass = new SimpleStringProperty();
    private final DoubleProperty steelUltimateExtension = new SimpleDoubleProperty();

    public Material() {
        fcd.bind(Bindings.divide(fck, CONCRETE_COEF));
        fyd.bind(Bindings.divide(fyk, STEEL_COEF));
        steelUltimateExtension.bind(
                Bindings.when(ductibilityClass.isEqualTo("A")).then(0.025).otherwise(
                        Bindings.when(ductibilityClass.isEqualTo("B")).then(0.05).otherwise(0.075)
                )
        );
    }

    public double getSteelUltimateExtension() {
        return steelUltimateExtension.get();
    }

    public DoubleProperty steelUltimateExtensionProperty() {
        return steelUltimateExtension;
    }


    public double getFcd() {
        return fcd.get();
    }

    public DoubleProperty fcdProperty() {
        return fcd;
    }

    public double getFyd() {
        return fyd.get();
    }

    public DoubleProperty fydProperty() {
        return fyd;
    }

    public double getFck() {
        return fck.get();
    }

    public DoubleProperty fckProperty() {
        return fck;
    }

    public double getFyk() {
        return fyk.get();
    }

    public DoubleProperty fykProperty() {
        return fyk;
    }

    public String getDuctibilityClass() {
        return ductibilityClass.get();
    }

    public StringProperty ductibilityClassProperty() {
        return ductibilityClass;
    }

    public boolean isEmptyDuctibilityClass(){
        return ductibilityClass.isEmpty().get();
    }
}
