package com.beamcalculate.custom.input_manager;

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
                    if (!textField.getText().matches("(?=([.]|[,])?\\d)\\d*(?:(?:[.]|[,])(?:\\d*))?")) {
                        /* The pattern means :
                         * (?=[.]?\\d) -----> first match [.]|[,]? - "." or "," appears once or not at all with \d - one number, to avoid entering only one "." and one ","
                         * \\d*(?:(?:[.])(?:\\d*))? -------> then match \d* zero or more number, ([.] + \d*)? "." with zero or more number appears zero or once
                         */
                        //when it not matches the pattern
                        //set the textField empty
                        textField.setText("");
                    } else {
                        Pattern p = Pattern.compile("(\\d*)([.]|[,])(\\d*)");
                        //if we enter ".2" or "3."
                        //it will be replaced by "0.2" and "3.0"
                        Matcher m = p.matcher(textField.getText());
                        if(m.find()) {
                            if (m.group(2).equals(",")){
                                textField.setText(m.group(1) + "." + m.group(3));
                            }
                            if (m.group(1).equals("")){
                                textField.setText("0." + m.group(3));
                            }
                            if (m.group(3).equals("")){
                                textField.setText(m.group(1) + ".0");
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
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                if (!textField.getText().matches("\\d*(?:(?:[.])(?:\\d*))?")) {
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
