package com.beamcalculate.pages;

import com.beamcalculate.TestFXBase;
import javafx.scene.control.CheckBox;
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
        HBox hbox = (HBox)driver.find(METHODS_DISPLAY_CHECK_ID);
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


}
