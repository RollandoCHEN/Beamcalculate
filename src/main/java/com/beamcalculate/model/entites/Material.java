package com.beamcalculate.model.entites;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Material {
    public static double CONCRETE_COEF = 1.5;
    public static double STEEL_COEF = 1.15;

    private final DoubleProperty fck_MPa = new SimpleDoubleProperty();
    private final DoubleProperty fcd_MPa = new SimpleDoubleProperty();
    private final DoubleProperty fyk_MPa = new SimpleDoubleProperty();
    private final DoubleProperty fyd_MPa = new SimpleDoubleProperty();
    private final DoubleProperty fcm_MPa = new SimpleDoubleProperty();
    private final DoubleProperty fctm_MPa = new SimpleDoubleProperty();
    private final DoubleProperty Ecm_GPa = new SimpleDoubleProperty();
    private final DoubleProperty Es_GPa = new SimpleDoubleProperty();
    private final StringProperty ductibilityClass = new SimpleStringProperty();
    private final DoubleProperty steelUltimateExtension = new SimpleDoubleProperty();

    public Material() {
        fcd_MPa.bind(Bindings.divide(fck_MPa, CONCRETE_COEF));
        fyd_MPa.bind(Bindings.divide(fyk_MPa, STEEL_COEF));
        steelUltimateExtension.bind(
                Bindings.when(ductibilityClass.isEqualTo("A")).then(0.025).otherwise(
                        Bindings.when(ductibilityClass.isEqualTo("B")).then(0.05).otherwise(0.075)
                )
        );
        fcm_MPa.bind(Bindings.add(fck_MPa, 8));
        fctm_MPa.bind(Bindings.createDoubleBinding(
                () -> 3 * Math.pow(fcm_MPa.get(), 2/3),
                fcm_MPa
        ));
        Ecm_GPa.bind(Bindings.createDoubleBinding(
                () -> 22 * Math.pow(fcm_MPa.get() / 10, 0.3),
                fcm_MPa
        ));
        Es_GPa.set(200);
    }

    public double getSteelUltimateExtension() {
        return steelUltimateExtension.get();
    }

    public DoubleProperty steelUltimateExtensionProperty() {
        return steelUltimateExtension;
    }


    public double getFcd_MPa() {
        return fcd_MPa.get();
    }

    public DoubleProperty fcd_MPaProperty() {
        return fcd_MPa;
    }

    public double getFyd_MPa() {
        return fyd_MPa.get();
    }

    public DoubleProperty fyd_MPaProperty() {
        return fyd_MPa;
    }

    public double getFck_MPa() {
        return fck_MPa.get();
    }

    public DoubleProperty fck_MPaProperty() {
        return fck_MPa;
    }

    public double getFyk_MPa() {
        return fyk_MPa.get();
    }

    public DoubleProperty fyk_MPaProperty() {
        return fyk_MPa;
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

    public double getFcm_MPa() {
        return fcm_MPa.get();
    }

    public DoubleProperty fcm_MPaProperty() {
        return fcm_MPa;
    }

    public double getFctm_MPa() {
        return fctm_MPa.get();
    }

    public DoubleProperty fctm_MPaProperty() {
        return fctm_MPa;
    }

    public double getEcm_GPa() {
        return Ecm_GPa.get();
    }

    public DoubleProperty ecm_GPaProperty() {
        return Ecm_GPa;
    }

    public double getEs_GPa() {
        return Es_GPa.get();
    }

    public DoubleProperty es_GPaProperty() {
        return Es_GPa;
    }
}
