package com.beamcalculate.model.result;

import com.beamcalculate.Main;
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

        final Label labelX = new Label(
                Main.getBundleText("label.abscissa")
                        + " G (R) : "
                        + TWODEDECIMALS.getDecimalFormat().format(globelX) +
                        " (" + TWODEDECIMALS.getDecimalFormat().format(relativeX) + ") "
                        + Main.getBundleText("unit.length.m")
        );

        final Label labelY = new Label(
                Main.getBundleText("label.moment")
                        + " : "
                        + FOURDECIMALS.getDecimalFormat().format(y)
                        + " "
                        + Main.getBundleText("unit.moment")
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
