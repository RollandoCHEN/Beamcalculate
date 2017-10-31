package com.beamcalculate.custom.node;

import com.beamcalculate.BeamCalculatorApp;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static com.beamcalculate.enums.NumericalFormat.*;

public class HoveredThresholdNode extends StackPane {

    public HoveredThresholdNode(double globelX, double relativeX, double y) {

        setPrefSize(10, 10);
        Label labelX;
        if (globelX == relativeX){
            labelX = new Label(
                    BeamCalculatorApp.getBundleText("label.abscissa")
                            + " : "
                            + TWODECIMALS.getDecimalFormat().format(globelX)
                            + BeamCalculatorApp.getBundleText("unit.length.m")
            );
        }else {
            labelX = new Label(
                    BeamCalculatorApp.getBundleText("label.abscissa")
                            + " G (R) : "
                            + TWODECIMALS.getDecimalFormat().format(globelX) +
                            " (" + TWODECIMALS.getDecimalFormat().format(relativeX) + ") "
                            + BeamCalculatorApp.getBundleText("unit.length.m")
            );
        }

        Label labelY = new Label(
                BeamCalculatorApp.getBundleText("label.moment")
                        + " : "
                        + FOURDECIMALS.getDecimalFormat().format(y)
                        + " "
                        + BeamCalculatorApp.getBundleText("unit.moment")
        );

        VBox vbox = new VBox(labelX, labelY);
        vbox.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
        vbox.setStyle("-fx-alignment: center");

        labelX.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        labelY.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        labelX.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        labelY.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                getChildren().setAll(vbox);
                setCursor(Cursor.NONE);
                toFront();
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                getChildren().clear();
                setCursor(Cursor.CROSSHAIR);
            }
        });
    }

}
