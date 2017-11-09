package com.beamcalculate.pages;

import com.beamcalculate.TestFXBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.testfx.util.WaitForAsyncUtils;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;
import static com.beamcalculate.JavaFXIds.*;

/**
 * Created by Ruolin on 30/10/2017 for Beamcalculate.
 */
public class InputPage {

    private final TestFXBase driver;

    public InputPage(TestFXBase driver) {
        this.driver = driver;
    }

    public InputPage writeValue(double value, String targetField){
        driver.clickOn(targetField).write(String.valueOf(value));
        return this;
    }

    public InputPage writeValue(String value, String targetField){
        driver.clickOn(targetField).write(value);
        return this;
    }

    public InputPage writeValueWithEnter(double value, String targetField){
        writeValue(value, targetField);
        driver.type(KeyCode.ENTER);
        return this;
    }

    public InputPage writeValueWithEnter(String value, String targetField){
        writeValue(value, targetField);
        driver.type(KeyCode.ENTER);
        return this;
    }

    public InputPage chooseValue(Object value, String targetChoiceBox){
        ChoiceBox choiceBox = driver.find(targetChoiceBox);
        if (choiceBox.getItems().contains(String.valueOf(value))){
            driver.clickOn(targetChoiceBox).clickOn(String.valueOf(value));
        } else {
            throw new IllegalArgumentException("Can't find the value in the choice box!");
        }
        return this;
    }

    public InputPage setTSection(boolean tSection){
        CheckBox checkBox = driver.find(T_SHAPED_SECTION_CHECK_ID);
        if (tSection && !checkBox.isSelected()){
            driver.clickOn(T_SHAPED_SECTION_CHECK_ID);
        } else if (!tSection && checkBox.isSelected()){
            driver.clickOn(T_SHAPED_SECTION_CHECK_ID);
        }
        return this;
    }

    public InputPage getNSpansBeam(int n, Double[] spanLengths, Double[] supportWidths){
        if (n < 1 || n > 7){
            throw new IllegalArgumentException("It's not an available value for the total number of spans!");
        } else if (spanLengths.length != n) {
            throw new IllegalArgumentException("The number of given length values should be the same as the number of spans!");
        } else if (supportWidths.length != n+1){
            throw new IllegalArgumentException("The number of given width values should be the number of spans plus 1!");
        } else {
            chooseValue(n, NUM_SPAN_CHOICE_ID);

            WaitForAsyncUtils.waitForFxEvents();

            //add spans lengths to the grid pane
            GridPane spansLengths = driver.find(SPANS_LENGTH_GRID_ID);
            for (int spanNum = 1; spanNum < n+1; spanNum++) {
                TextField spanLengthField = (TextField) spansLengths.getChildren().get(spanNum-1);
                spanLengthField.setText(spanLengths[spanNum-1].toString());
            }

            //add supports widths to the grid pane
            GridPane supportsWidths = driver.find(SUPPORTS_WIDTH_GRID_ID);
            for (int supportNum = 1; supportNum < n+2; supportNum++) {
                TextField supportWidthField = (TextField) supportsWidths.getChildren().get(supportNum-1);
                supportWidthField.setText(supportWidths[supportNum-1].toString());
            }
        }

        return this;
    }

    public InputPage setCrossSection(double width, double height){
        writeValue(width, SECTION_WIDTH_FIELD_ID).writeValueWithEnter(height, SECTION_HEIGHT_FIELD_ID);
        return this;
    }

    public InputPage setTSectionParam(double slabThickness, double perpendicularSpacing){
        writeValue(slabThickness, SLAB_THICKNESS_FIELD_ID).writeValueWithEnter(perpendicularSpacing, PERPENDICULAR_SPACING_FIELD_ID);
        return this;
    }

    public InputPage setLoad(double deadLoad, double liveLoad){
        writeValue(deadLoad, DEAD_LOAD_FIELD_ID).writeValueWithEnter(liveLoad, LIVE_LOAD_FIELD_ID);
        return this;
    }

    public InputPage setMaterial(double fck, double fyk, Character ductibility){
        writeValue(fck, CONCRETE_STRENGTH_FIELD_ID).
                writeValue(fyk, STEEL_STRENGTH_FIELD_ID).
                chooseValue(ductibility, DUCTIBILITY_CLASS_CHOICE_ID);
        return this;
    }

    public InputPage clickToContinue(int numOfClick){
        for (int i=0; i < numOfClick; i++) {
            driver.clickOn(getBundleText("button.continue"));
        }
        return this;
    }
}
