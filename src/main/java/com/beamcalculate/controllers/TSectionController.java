package com.beamcalculate.controllers;

import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class TSectionController implements Initializable {
    @FXML private DoubleProperty totalHeight = new SimpleDoubleProperty();
    @FXML private DoubleProperty webWidth = new SimpleDoubleProperty();
    @FXML private DoubleProperty flangeWidth = new SimpleDoubleProperty();
    @FXML private DoubleProperty flangeHeight = new SimpleDoubleProperty();
    @FXML private DoubleProperty flangeCompHeight = new SimpleDoubleProperty();
    @FXML private DoubleProperty flangeCompWidth = new SimpleDoubleProperty();
    @FXML private DoubleProperty webCompHeight = new SimpleDoubleProperty();
    @FXML private DoubleProperty webCompWidth = new SimpleDoubleProperty();


    public double getTotalHeight() {
        return totalHeight.get();
    }

    public DoubleProperty totalHeightProperty() {
        return totalHeight;
    }

    public double getFlangeWidth() {
        return flangeWidth.get();
    }

    public DoubleProperty flangeWidthProperty() {
        return flangeWidth;
    }

    public double getWebWidth() {
        return webWidth.get();
    }

    public DoubleProperty webWidthProperty() {
        return webWidth;
    }

    public double getFlangeHeight() {
        return flangeHeight.get();
    }

    public DoubleProperty flangeHeightProperty() {
        return flangeHeight;
    }

    public double getFlangeCompHeight() {
        return flangeCompHeight.get();
    }

    public DoubleProperty flangeCompHeightProperty() {
        return flangeCompHeight;
    }

    public double getFlangeCompWidth() {
        return flangeCompWidth.get();
    }

    public DoubleProperty flangeCompWidthProperty() {
        return flangeCompWidth;
    }

    public double getWebCompHeight() {
        return webCompHeight.get();
    }

    public DoubleProperty webCompHeightProperty() {
        return webCompHeight;
    }

    public double getWebCompWidth() {
        return webCompWidth.get();
    }

    public DoubleProperty webCompWidthProperty() {
        return webCompWidth;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        double fixHeight = 200;
        double ratio = fixHeight / Geometry.getSectionHeight();
        totalHeight.setValue(fixHeight);
        webWidth.bind(Bindings.multiply(Geometry.sectionWidthProperty(), ratio));

        if(MainController.isOnTSection()) {
            flangeHeight.bind(Bindings.multiply(Geometry.slabThicknessProperty(), ratio));
            flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(1), ratio));
            flangeCompHeight.bind(Bindings.multiply(Reinforcement.flangeCompressionHightProperty(), ratio));
            flangeCompWidth.bind(flangeWidth);
        }

        webCompHeight.bind(Bindings.multiply(Reinforcement.webCompressionHeightProperty(), ratio));
        webCompWidth.bind(webWidth);

    }
}
