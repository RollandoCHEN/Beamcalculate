package com.beamcalculate.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Ruolin on 01/11/2017 for Beamcalculate.
 */
public class MomentPageController {
    @FXML AnchorPane anchorPane;
    @FXML Spinner<Integer> spanNumSpinner;
    @FXML HBox methodsCheckHBox;
    @FXML Label conditionInfoLabel;
    @FXML CheckBox redistributionCheck;
    @FXML Button configurationButton;
    @FXML ChoiceBox<String> methodsChoiceBox;
    @FXML Button rebarCalculateButton;
    @FXML ChoiceBox<Integer> spanChoiceBox;
    @FXML TextField abscissaField;
    @FXML Button momentCalculateButton;
    @FXML Label maxCaseMomentValue;
    @FXML Label minCaseMomentValue;
    @FXML BorderPane borderPaneContainer;

    public Spinner<Integer> getSpanNumSpinner() {
        return spanNumSpinner;
    }

    public HBox getMethodsCheckHBox() {
        return methodsCheckHBox;
    }

    public Label getConditionInfoLabel() {
        return conditionInfoLabel;
    }

    public CheckBox getRedistributionCheck() {
        return redistributionCheck;
    }

    public Button getConfigurationButton() {
        return configurationButton;
    }

    public ChoiceBox<String> getMethodsChoiceBox() {
        return methodsChoiceBox;
    }

    public Button getRebarCalculateButton() {
        return rebarCalculateButton;
    }

    public ChoiceBox<Integer> getSpanChoiceBox() {
        return spanChoiceBox;
    }

    public TextField getAbscissaField() {
        return abscissaField;
    }

    public Button getMomentCalculateButton() {
        return momentCalculateButton;
    }

    public Label getMaxCaseMomentValue() {
        return maxCaseMomentValue;
    }

    public Label getMinCaseMomentValue() {
        return minCaseMomentValue;
    }

    public BorderPane getBorderPaneContainer() {
        return borderPaneContainer;
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }
}
