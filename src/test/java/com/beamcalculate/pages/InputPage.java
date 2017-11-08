package com.beamcalculate.pages;

import com.beamcalculate.TestFXBase;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.testfx.util.WaitForAsyncUtils;

import static com.beamcalculate.JavaFXIds.*;

/**
 * Created by Ruolin on 30/10/2017 for Beamcalculate.
 */
public class InputPage {

    private final TestFXBase driver;

    public InputPage(TestFXBase driver) {
        this.driver = driver;
    }

    public InputPage enterValue(double value, String targetField){
        driver.clickOn(targetField).write(String.valueOf(value)).type(KeyCode.ENTER);
        return this;
    }

    public InputPage enterValue(String value, String targetField){
        driver.clickOn(targetField).write(value).type(KeyCode.ENTER);
        return this;
    }

    public InputPage chooseValue(Object value, String targetChoiceBox){
        driver.clickOn(targetChoiceBox).clickOn(String.valueOf(value));
        return this;
    }
//    public void assertPopupIsNotVisible(Node ownedBy) {
//        WaitForAsyncUtils.waitForFxEvents();
//        for (Window w : driver.listWindows() ) {
//            if (w instanceof Popup) {
//                Popup lPopup = (Popup)w;
//                if (ownedBy.equals(lPopup.getOwnerNode())) {
//                    throw new IllegalStateException("Popup is visible (and should not be), owner = " + lPopup.getOwnerNode());
//                }
//            }
//        }
//    }
//
//    public void assertPopupIsVisible(Node ownedBy) {
//        WaitForAsyncUtils.waitForFxEvents();
//        for (Window w : driver.listWindows() ) {
//            if (w instanceof Popup) {
//                Popup lPopup = (Popup)w;
//                if (ownedBy.equals(lPopup.getOwnerNode())) {
//                    return;
//                }
//            }
//        }
//        throw new IllegalStateException("Popup is not visible (and should be)");
//    }

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

    public InputPage setCrossSection(double height, double width){
        enterValue(height, SECTION_HEIGHT_FIELD_ID).enterValue(width, SECTION_WIDTH_FIELD_ID);
        return this;
    }

    public InputPage setLoad(double deadLoad, double liveLoad){
        enterValue(deadLoad, DEAD_LOAD_FIELD_ID).enterValue(liveLoad, LIVE_LOAD_FIELD_ID);
        return this;
    }

    public InputPage setMeterial(double fck, double fyk, Character ductibility){
        enterValue(fck, CONCRETE_STRENGTH_FIELD_ID).
                enterValue(fyk, STEEL_STRENGTH_FIELD_ID).
                chooseValue(ductibility, DUCTIBILITY_CLASS_CHOICE_ID);
        return this;
    }
}
