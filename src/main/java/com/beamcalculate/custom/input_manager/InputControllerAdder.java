package com.beamcalculate.custom.input_manager;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import sun.misc.Regexp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ruolin on 28/10/2017 for Beamcalculate.
 */
public class InputControllerAdder {

    public void addRealNumberControllerTo(TextField... textFields){
        for (TextField textField : textFields) {
            textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue) { //when focus lost
                    if (!textField.getText().matches("(?=(?:[.]|[,])?(?:\\d))(\\d*)(?:([.]|[,])(\\d*))?")) {
                        //when it not matches the pattern
                        //set the textField empty
                        textField.setText("");
                    } else {
                        Pattern pattern = Pattern.compile("(\\d*)([.]|[,])(\\d*)");
                        Matcher matcher = pattern.matcher(textField.getText());
                        if (matcher.find()) {
                            if (matcher.group(2).equals(",")) {
                                textField.setText(matcher.group(1) + "." + matcher.group(3));
                            }
                            if (matcher.group(1).equals("")) {
                                textField.setText("0." + matcher.group(3));
                            }
                            if (matcher.group(3).equals("")) {
                                textField.setText(matcher.group(1) + ".0");
                            }
                        }
                    }
                }
            });
        }
    }

    public void addRealNumberControllerTo(List<TextField> list){
        list.forEach(this::addRealNumberControllerTo);
    }

    public void addMaxValueValidation(TextField textField, double maxValue) {
        addRealNumberControllerTo(textField);
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                if (!textField.getText().isEmpty()&&Double.parseDouble(textField.getText()) > maxValue) {
                    textField.setText("");
                }
            }
        });
    }
}
