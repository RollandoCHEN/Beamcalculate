package com.beamcalculate.model.page_manager;

import com.beamcalculate.model.custom_node.NamedComboBox;
import com.beamcalculate.model.custom_node.NamedTextField;
import com.beamcalculate.model.custom_alert.WarningMessage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.beamcalculate.model.custom_alert.WarningMessage.WarningMessageOption.WITH_CONFIRM;

/**
 * Created by Ruolin on 29/10/2017 for Beamcalculate.
 */
public class InputValueGetter {

    private Set<String> mMissingParamWarningSet = new HashSet<>();

    public void getInputValue(GridPane sourceGridPane, Map goalMap){
        sourceGridPane.getChildren().forEach(node -> {
            TextInputControl textField = (TextInputControl)node;
            try {
                goalMap.put(Integer.parseInt(textField.getId()),Double.parseDouble(textField.getText()));
            } catch (NumberFormatException e) {
            }
        });
    }

    public void getInputValue(NamedTextField sourceTextField, DoubleProperty goalProperty){
        try {
            goalProperty.set(Double.parseDouble(sourceTextField.getText()));
            if (Double.parseDouble(sourceTextField.getText()) == 0){
                mMissingParamWarningSet.add(sourceTextField.getParameterName());
            }
        } catch (NumberFormatException e) {
            mMissingParamWarningSet.add(sourceTextField.getParameterName());
        }
    }

    public void getInputValue(NamedComboBox<String> sourceComboBox, StringProperty goalProperty){
        try {
            goalProperty.set(sourceComboBox.getValue());
            if(sourceComboBox.getValue() == null){
                mMissingParamWarningSet.add(sourceComboBox.getParameterName());
            }
        } catch (Exception e) {
            mMissingParamWarningSet.add(sourceComboBox.getParameterName());
        }
    }

    public void getInputValue(ComboBox<Integer> sourceComboBox, IntegerProperty goalProperty){
        goalProperty.set(sourceComboBox.getValue());
    }

    public void getInputValue(ToggleButton sourceToggleButton, BooleanProperty goalProperty){
        goalProperty.set(sourceToggleButton.isSelected());
    }

    public boolean continueAfterShowingWarning(){
        if(!mMissingParamWarningSet.isEmpty()) {
            WarningMessage warningMessage = new WarningMessage(
                    mMissingParamWarningSet, "warning.content.inputWarning","", WITH_CONFIRM
            );
            return warningMessage.okChosen();
        } else {
            return true;
        }
    }
}
