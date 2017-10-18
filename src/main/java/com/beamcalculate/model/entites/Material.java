package com.beamcalculate.model.entites;

import com.beamcalculate.model.NamedDoubleProperty;
import com.beamcalculate.model.NamedStringProperty;
import javafx.beans.binding.Bindings;


public class Material {
    public static double CONCRETE_COEF = 1.5;
    public static double STEEL_COEF = 1.15;

    private static final NamedDoubleProperty fck = new NamedDoubleProperty();
    private static final NamedDoubleProperty fcd = new NamedDoubleProperty();
    private static final NamedDoubleProperty fyk = new NamedDoubleProperty();
    private static final NamedDoubleProperty fyd = new NamedDoubleProperty();
    private static final NamedStringProperty ductibilityClass = new NamedStringProperty();
    private static final NamedDoubleProperty steelUltimateStrain = new NamedDoubleProperty();

    public Material() {
        fcd.bind(Bindings.divide(fck, CONCRETE_COEF));
        fyd.bind(Bindings.divide(fyk, STEEL_COEF));
        steelUltimateStrain.bind(
                Bindings.when(ductibilityClass.isEqualTo("A")).then(0.025).otherwise(
                        Bindings.when(ductibilityClass.isEqualTo("B")).then(0.05).otherwise(0.075)
                )
        );
    }

    public static double getSteelUltimateStrain() {
        return steelUltimateStrain.get();
    }

    public static NamedDoubleProperty steelUltimateStrainProperty() {
        return steelUltimateStrain;
    }

    public static double getFcd() {
        return fcd.get();
    }

    public static NamedDoubleProperty fcdProperty() {
        return fcd;
    }

    public static double getFyd() {
        return fyd.get();
    }

    public static NamedDoubleProperty fydProperty() {
        return fyd;
    }

    public double getFck() {
        return fck.get();
    }

    public static NamedDoubleProperty fckProperty() {
        return fck;
    }

    public double getFyk() {
        return fyk.get();
    }

    public static NamedDoubleProperty fykProperty() {
        return fyk;
    }

    public static String getDuctibilityClass() {
        return ductibilityClass.get();
    }

    public static NamedStringProperty ductibilityClassProperty() {
        return ductibilityClass;
    }

    public static boolean isEmptyDuctibilityClass(){
        return ductibilityClass.isEmpty().get();
    }
}
