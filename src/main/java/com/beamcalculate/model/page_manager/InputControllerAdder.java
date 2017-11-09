package com.beamcalculate.model.page_manager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ruolin on 28/10/2017 for Beamcalculate.
 */
public class InputControllerAdder {
    private Map<Integer, ChangeListener<? super Boolean>> mMaxValueListenerMap = new HashMap<>();


    public void addRealNumberControllerTo(boolean canBeZero, TextField... textFields){
        for (TextField textField : textFields) {

            textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue) { //when focus lost
                    addPatternMatchTo(textField, canBeZero);
                }
            });
            textField.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    addPatternMatchTo(textField, canBeZero);
                }
            });
        }
    }

    public void addRealNumberControllerTo(TextField... textFields){
        addRealNumberControllerTo(false, textFields);
    }

    private void addPatternMatchTo(TextField textField, boolean canBeZero) {
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
        if (!canBeZero && !textField.getText().isEmpty() &&
                Double.parseDouble(textField.getText()) == 0){
            textField.setText("");
        }
    }

    public void addRealNumberControllerTo(List<TextField> list){
        list.forEach(this::addRealNumberControllerTo);
    }

    public void addRealNumberControllerTo(boolean canBeZero ,List<TextField> list){
        list.forEach(textField -> addRealNumberControllerTo(canBeZero, textField));
    }

    public void addMaxValueValidation(TextField textField, int chosenSpanId, double maxValue) {

        mMaxValueListenerMap.put(
                chosenSpanId ,
                (ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) { //when focus lost

                        if (!textField.getText().isEmpty() &&                               //value is entered
                        Double.parseDouble(textField.getText()) > maxValue) {       //entered value > max limit
                            textField.setText("");                                          //remove the value
                        }
                    }
                }
        );

        textField.focusedProperty().addListener(mMaxValueListenerMap.get(chosenSpanId));

//        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
//            if (!newValue) { //when focus lost
//                if (!textField.getText().isEmpty() &&                               //value is entered
//                        Double.parseDouble(textField.getText()) > maxValue) {       //entered value > max limit
//                    textField.setText("");                                          //remove the value
//                }
//            }
//        });
    }

    public void removeMaxValueValidation(TextField textField, int chosenSpanId){
        textField.focusedProperty().removeListener(mMaxValueListenerMap.get(chosenSpanId));
    }
}
