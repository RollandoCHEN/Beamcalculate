package com.beamcalculate.pages;

import com.beamcalculate.TestFXBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import static com.beamcalculate.JavaFXIds.*;

public class MomentPage {

    private final TestFXBase driver;

    public MomentPage(TestFXBase driver) {
        this.driver = driver;
    }

    public MomentPage changeSpannerValue(int num) {
        driver.clickOn(TOTAL_NUM_ON_SPAN_SPINNER_ID).write(String.valueOf(num)).type(KeyCode.ENTER);
        return this;
    }

    public MomentPage uncheckMethodOfNum(int num) {
        HBox hbox = driver.find(METHODS_DISPLAY_CHECK_ID);
        CheckBox methodCheckBox;
        if(num > hbox.getChildren().size()){
            throw new IllegalArgumentException("Have only " + hbox.getChildren().size() + " methods on the line chart!!");
        } else {
            methodCheckBox = (CheckBox) hbox.getChildren().get(num - 1);
            if (methodCheckBox.isSelected()){
                driver.clickOn(methodCheckBox);
            }
        }
        return this;
    }

    public MomentPage chooseValue(Object value, String targetChoiceBox){
        ChoiceBox choiceBox = driver.find(targetChoiceBox);
        if (choiceBox.getItems().contains(String.valueOf(value))||choiceBox.getItems().contains(value)){
            driver.clickOn(targetChoiceBox).clickOn(String.valueOf(value));
        } else {
            throw new IllegalArgumentException("Can't find the value in the choice box!");
        }
        return this;
    }

    public MomentPage chooseIndex(int index, String targetChoiceBox){
        ChoiceBox choiceBox = driver.find(targetChoiceBox);
        if (index <= choiceBox.getItems().size() && index > 0){
            driver.clickOn(targetChoiceBox);
            for (int i=1; i<=index; i++){
                driver.type(KeyCode.DOWN);
            }
            driver.type(KeyCode.ENTER);
        } else {
            throw new IllegalArgumentException("Can't find the value in the choice box!");
        }
        return this;
    }
}
