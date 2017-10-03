package com.beamcalculate.controllers;

import com.beamcalculate.Main;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class TSectionController implements Initializable {
    @FXML private GridPane bottomGridPane = new GridPane();
    @FXML private Label titleLabel = new Label();

    private DoubleProperty totalHeight = new SimpleDoubleProperty();
    private DoubleProperty webWidth = new SimpleDoubleProperty();
    private DoubleProperty flangeWidth = new SimpleDoubleProperty();
    private DoubleProperty flangeHeight = new SimpleDoubleProperty();
    private DoubleProperty flangeCompHeight = new SimpleDoubleProperty();
    private DoubleProperty flangeCompWidth = new SimpleDoubleProperty();
    private DoubleProperty webCompHeight = new SimpleDoubleProperty();
    private DoubleProperty webCompWidth = new SimpleDoubleProperty();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        double fixHeight = 300;
        double ratio = fixHeight / Geometry.getSectionHeight();
        totalHeight.setValue(fixHeight);
        webWidth.bind(Bindings.multiply(Geometry.sectionWidthProperty(), ratio));

        titleLabel.textProperty().setValue(
                Main.getBundleText("title.crossSection") +
                        " (" + Main.getBundleText("label.span") + " 1" + ")");

        if (MainController.isOnTSection()) {
            flangeHeight.bind(Bindings.multiply(Geometry.slabThicknessProperty(), ratio));
            flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(1), ratio));
            flangeCompHeight.bind(Bindings.multiply(Reinforcement.getFlangeCompressionsHeightMap().get(1), ratio));
            flangeCompWidth.bind(flangeWidth);
        }

        webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(1), ratio));
        webCompWidth.bind(webWidth);

        for (int span = 1; span < Geometry.getNumSpan() + 1; span++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(100);
            c.setHalignment(HPos.CENTER);
            bottomGridPane.getColumnConstraints().add(c);
            Button button = new Button(Main.getBundleText("label.span") + " " + span);
            button.setId(String.valueOf(span));
            if (span==1){button.setDisable(true);}
            bottomGridPane.add(button, span - 1, 0);

            button.setOnAction(event -> {
                int spanId = Integer.parseInt(button.getText().replaceAll("[^0-9]", ""));

                titleLabel.textProperty().setValue(Main.getBundleText("title.crossSection") +
                        " (" + Main.getBundleText("label.span") + " " + spanId + ")");

                bottomGridPane.getChildren().forEach(node -> {
                    if (Integer.parseInt(node.getId()) == spanId) {
                        node.setDisable(true);
                    } else {
                        node.setDisable(false);
                    }
                });

                if (MainController.isOnTSection()) {
                    flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(spanId), ratio));
                    flangeCompHeight.bind(Bindings.multiply(Reinforcement.getFlangeCompressionsHeightMap().get(spanId), ratio));
                }
                webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(spanId), ratio));
            });
        }
    }

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
}
