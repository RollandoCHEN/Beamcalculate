package com.beamcalculate.custom.input_manager;

import com.beamcalculate.custom.alert.WarningMessage;
import com.beamcalculate.custom.node.NamedChoiceBox;
import com.beamcalculate.custom.node.NamedTextField;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        } catch (NumberFormatException e) {
            mMissingParamWarningSet.add(sourceTextField.getParameterName());
        }
    }

    public void getInputValue(NamedChoiceBox sourceChoiceBox, StringProperty goalProperty){
        try {
            goalProperty.set((String)sourceChoiceBox.getValue());
            if(sourceChoiceBox.getValue() == null){
                mMissingParamWarningSet.add(sourceChoiceBox.getParameterName());
            }
        } catch (Exception e) {
            mMissingParamWarningSet.add(sourceChoiceBox.getParameterName());
        }
    }

    public void getInputValue(ChoiceBox sourceChoiceBox, IntegerProperty goalProperty){
        try {
            goalProperty.set((Integer)sourceChoiceBox.getValue());
        } catch (Exception e) {
            mMissingParamWarningSet.add(sourceChoiceBox.getId());
        }
    }


    public void showInputWarning(){
        new WarningMessage(mMissingParamWarningSet, "warning.content.inputWarning");
    }
}
