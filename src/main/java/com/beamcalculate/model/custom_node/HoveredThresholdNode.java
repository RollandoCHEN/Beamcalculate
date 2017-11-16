package com.beamcalculate.model.custom_node;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static com.beamcalculate.enums.NumericalFormat.*;

public class HoveredThresholdNode extends StackPane {

    public HoveredThresholdNode(double globalX, double relativeX, double y) {

        setPrefSize(10, 10);
        Label labelX;
        if (globalX == relativeX){
            labelX = new Label(
                    getBundleText("label.abscissa")
                            + " : "
                            + TWO_DECIMALS.format(globalX)
                            + getBundleText("unit.length.m")
            );
        }else {
            labelX = new Label(
                    getBundleText("label.abscissa")
                            + " G (R) : "
                            + TWO_DECIMALS.format(globalX) +
                            " (" + TWO_DECIMALS.format(relativeX) + ") "
                            + getBundleText("unit.length.m")
            );
        }

        Label labelY = new Label(
                getBundleText("label.moment")
                        + " : "
                        // TODO When inverse the y axis properly, this negative sign should be removed
                        + FOUR_DECIMALS.format(-y)           // trick to display inverse value on line chart
                        + " "
                        + getBundleText("unit.moment")
        );

        VBox vbox = new VBox(labelX, labelY);
        vbox.getStyleClass().addAll("value_mark", "no_transparent");
        vbox.setStyle("-fx-alignment: center");

        labelX.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        labelY.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        setOnMouseEntered(event -> {
            getChildren().setAll(vbox);
            setCursor(Cursor.NONE);
            toFront();
        });
        setOnMouseExited(event -> {
            getChildren().clear();
            setCursor(Cursor.CROSSHAIR);
        });
    }

}
