package com.beamcalculate.controllers;

import com.beamcalculate.Main;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.beamcalculate.enums.NumericalFormat.ONEDECIMAL;
import static com.beamcalculate.enums.NumericalFormat.TWODEDECIMALS;
import static com.beamcalculate.enums.NumericalFormat.ZERODECIMAL;

public class TSectionController implements Initializable {
    @FXML private GridPane bottomGridPane = new GridPane();
    @FXML private Label titleLabel = new Label();

    private DoubleProperty totalHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedTotalHeight = new SimpleDoubleProperty();
    private DoubleProperty webWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedWebWidth = new SimpleDoubleProperty();
    private DoubleProperty flangeWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedFlangeWidth = new SimpleDoubleProperty();
    private DoubleProperty flangeHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedFlangeHeight = new SimpleDoubleProperty();
    private DoubleProperty flangeCompHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedFlangeCompHeight = new SimpleDoubleProperty();
    private DoubleProperty flangeCompWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedFlangeCompWidth = new SimpleDoubleProperty();
    private DoubleProperty webCompHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedWebCompHeight = new SimpleDoubleProperty();
    private DoubleProperty webCompWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedWebCompWidth = new SimpleDoubleProperty();

    private DoubleProperty compRegionHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedCompRegionHeight = new SimpleDoubleProperty();

    private StringProperty compRegionHeightString = new SimpleStringProperty();
    private StringProperty webCompWidthString = new SimpleStringProperty();
    private StringProperty flangeWidthString = new SimpleStringProperty();
    private StringProperty flangeHeightString = new SimpleStringProperty();
    private StringProperty totalHeightString = new SimpleStringProperty();

    private static DoubleProperty sceneWidth = new SimpleDoubleProperty();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // height of cross section diagram is fixed at 300px

        double fixHeight = 300;
        double ratio = fixHeight / (Geometry.getSectionHeight() * 100);

        displayedTotalHeight.bind(Bindings.multiply(totalHeight, ratio));
        displayedWebWidth.bind(Bindings.multiply(webWidth, ratio));
        displayedWebCompHeight.bind(Bindings.multiply(webCompHeight, ratio));
        displayedWebCompWidth.bind(Bindings.multiply(webCompWidth, ratio));
        displayedCompRegionHeight.bind(Bindings.multiply(compRegionHeight, ratio));
        displayedFlangeWidth.bind(Bindings.multiply(flangeWidth, ratio));
        displayedFlangeHeight.bind(Bindings.multiply(flangeHeight, ratio));
        displayedFlangeCompHeight.bind(Bindings.multiply(flangeCompHeight, ratio));
        displayedFlangeCompWidth.bind(Bindings.multiply(flangeCompWidth, ratio));

        totalHeight.bind(Bindings.multiply(Geometry.sectionHeightProperty(),100));
        webWidth.bind(Bindings.multiply(Geometry.sectionWidthProperty(),100));
        flangeHeight.bind(totalHeight);
        flangeWidth.bind(webWidth);
        webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(1),100));
        webCompWidth.bind(webWidth);

        setTitleLabel(1);


        if (MainController.isOnTSection()) {
            flangeHeight.bind(Bindings.multiply(Geometry.slabThicknessProperty(),100));
            flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(1),100));
            flangeCompHeight.bind(Bindings.multiply(Reinforcement.getFlangeCompressionsHeightMap().get(1),100));
            flangeCompWidth.bind(flangeWidth);
        }

        if(webCompHeight.get()!=0) {
            compRegionHeight.bind(webCompHeight);
        } else {
            compRegionHeight.bind(flangeCompHeight);
        }

        Bindings.bindBidirectional(webCompWidthString, webCompWidth, ZERODECIMAL.getDecimalFormat());
        Bindings.bindBidirectional(compRegionHeightString, compRegionHeight, TWODEDECIMALS.getDecimalFormat());
        Bindings.bindBidirectional(flangeWidthString, flangeWidth, ZERODECIMAL.getDecimalFormat());
        Bindings.bindBidirectional(flangeHeightString, flangeHeight, ZERODECIMAL.getDecimalFormat());
        Bindings.bindBidirectional(totalHeightString, totalHeight, ZERODECIMAL.getDecimalFormat());

        sceneWidth.bind(Bindings.add(displayedFlangeWidth, 200));

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

                setTitleLabel(spanId);

                bottomGridPane.getChildren().forEach(node -> {
                    if (Integer.parseInt(node.getId()) == spanId) {
                        node.setDisable(true);
                    } else {
                        node.setDisable(false);
                    }
                });

                if (MainController.isOnTSection()) {
                    flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(spanId),100));
                    flangeCompHeight.bind(Bindings.multiply(Reinforcement.getFlangeCompressionsHeightMap().get(spanId),100));
                }
                webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(spanId),100));

                double actualWindowWidth = button.getScene().getWindow().getWidth();
                if (actualWindowWidth < getSceneWidth()){
                    button.getScene().getWindow().setWidth(getSceneWidth());
                }
            });
        }
    }

    private void setTitleLabel(int spanId) {
        titleLabel.textProperty().setValue(
                Main.getBundleText("title.crossSection") +
                        " (" +Main.getBundleText("unit.length.cm") + ")" +
                        " - " + Main.getBundleText("label.span") + " " + spanId
        );
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

    public String getCompRegionHeightString() {
        return compRegionHeightString.get();
    }

    public StringProperty compRegionHeightStringProperty() {
        return compRegionHeightString;
    }

    public String getWebCompWidthString() {
        return webCompWidthString.get();
    }

    public StringProperty webCompWidthStringProperty() {
        return webCompWidthString;
    }

    public double getCompRegionHeight() {
        return compRegionHeight.get();
    }

    public DoubleProperty compRegionHeightProperty() {
        return compRegionHeight;
    }

    public String getFlangeWidthString() {
        return flangeWidthString.get();
    }

    public StringProperty flangeWidthStringProperty() {
        return flangeWidthString;
    }

    public String getFlangeHeightString() {
        return flangeHeightString.get();
    }

    public StringProperty flangeHeightStringProperty() {
        return flangeHeightString;
    }

    public String getTotalHeightString() {
        return totalHeightString.get();
    }

    public StringProperty totalHeightStringProperty() {
        return totalHeightString;
    }

    public double getDisplayedTotalHeight() {
        return displayedTotalHeight.get();
    }

    public DoubleProperty displayedTotalHeightProperty() {
        return displayedTotalHeight;
    }

    public double getDisplayedWebWidth() {
        return displayedWebWidth.get();
    }

    public DoubleProperty displayedWebWidthProperty() {
        return displayedWebWidth;
    }

    public double getDisplayedFlangeWidth() {
        return displayedFlangeWidth.get();
    }

    public DoubleProperty displayedFlangeWidthProperty() {
        return displayedFlangeWidth;
    }

    public double getDisplayedFlangeHeight() {
        return displayedFlangeHeight.get();
    }

    public DoubleProperty displayedFlangeHeightProperty() {
        return displayedFlangeHeight;
    }

    public double getDisplayedFlangeCompHeight() {
        return displayedFlangeCompHeight.get();
    }

    public DoubleProperty displayedFlangeCompHeightProperty() {
        return displayedFlangeCompHeight;
    }

    public double getDisplayedFlangeCompWidth() {
        return displayedFlangeCompWidth.get();
    }

    public DoubleProperty displayedFlangeCompWidthProperty() {
        return displayedFlangeCompWidth;
    }

    public double getDisplayedWebCompHeight() {
        return displayedWebCompHeight.get();
    }

    public DoubleProperty displayedWebCompHeightProperty() {
        return displayedWebCompHeight;
    }

    public double getDisplayedWebCompWidth() {
        return displayedWebCompWidth.get();
    }

    public DoubleProperty displayedWebCompWidthProperty() {
        return displayedWebCompWidth;
    }

    public double getDisplayedCompRegionHeight() {
        return displayedCompRegionHeight.get();
    }

    public DoubleProperty displayedCompRegionHeightProperty() {
        return displayedCompRegionHeight;
    }

    public static double getSceneWidth() {
        return sceneWidth.get();
    }

    public DoubleProperty sceneWidthProperty() {
        return sceneWidth;
    }
}
