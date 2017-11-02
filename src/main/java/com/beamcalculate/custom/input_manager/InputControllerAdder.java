package com.beamcalculate.custom.input_manager;

import javafx.scene.control.TextField;

import java.util.List;

/**
 * Created by Ruolin on 28/10/2017 for Beamcalculate.
 */
public class InputControllerAdder {

    public void addRealNumberControllerTo(TextField... textFields){
        for (TextField textField : textFields) {
            textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue) { //when focus lost
                    if (!textField.getText().matches("\\d+\\.\\d+|\\d+")) {
                        //when it not matches the pattern
                        //set the textField empty
                        textField.setText("");
                    }
                }
            });
        }
    }

    public void addRealNumberControllerTo(List<TextField> list){
        list.forEach(this::addRealNumberControllerTo);
    }

    public void addMaxValueValidation(TextField textField, double maxValue) {
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                if (!textField.getText().matches("\\d+\\.\\d+|\\d+")) {
                    //when it not matches the pattern
                    //set the textField empty
                    textField.setText("");
                } else if (Double.parseDouble(textField.getText()) > maxValue) {
                    textField.setText("");
                }
            }
        });
    }
}
