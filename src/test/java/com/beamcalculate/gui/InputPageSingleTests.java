package com.beamcalculate.gui;

import com.beamcalculate.TestFXBase;
import com.beamcalculate.pages.InputPage;
import com.beamcalculate.pages.MomentPage;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.junit.Before;
import org.junit.Test;

import static com.beamcalculate.JavaFXIds.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Created by Ruolin on 08/11/2017 for Beamcalculate.
 */
public class InputPageSingleTests extends TestFXBase {

    @Before
    public void beforeEachTest(){
        mInputPage = new InputPage(this);
        mMomentPage = new MomentPage(this);
    }

    @Test
    public void momentGenerationButtonShouldDisabled(){
        sleep(500);

        verifyThat(ENVELOP_CURVE_BUTTON_ID, Node::isDisabled);
    }

    @Test
    public void momentAndRebarPageButtonsShouldDisabled(){
        sleep(500);

        verifyThat(MOMENT_PAGE_BUTTON_ID, Node::isDisabled);
        verifyThat(REBAR_CASES_PAGE_BUTTON_ID, Node::isDisabled);
    }

    @Test
    public void zeroShouldNotBeAllowedForGeometryAndMaterialParam(){
        mInputPage.writeValueWithEnter(0, SECTION_HEIGHT_FIELD_ID);
        mInputPage.writeValueWithEnter(0, SECTION_HEIGHT_FIELD_ID);

        verifyThat(SECTION_HEIGHT_FIELD_ID, (TextField textField) -> textField.getText().isEmpty());
    }

    @Test
    public void zeroShouldBeAllowedForLoad(){
        mInputPage.writeValueWithEnter(0, DEAD_LOAD_FIELD_ID);
        mInputPage.writeValueWithEnter(0, LIVE_LOAD_FIELD_ID);

        verifyThat(DEAD_LOAD_FIELD_ID, (TextField textField) -> Double.parseDouble(textField.getText()) == 0);
        verifyThat(LIVE_LOAD_FIELD_ID, (TextField textField) -> Double.parseDouble(textField.getText()) == 0);
    }

    @Test
    public void enterSpecialValueShouldBeAllowed(){
        mInputPage.writeValueWithEnter(".4", SECTION_WIDTH_FIELD_ID).
                writeValueWithEnter(",6", SECTION_HEIGHT_FIELD_ID).
                writeValueWithEnter("4.", CONCRETE_STRENGTH_FIELD_ID).
                writeValueWithEnter("5,", STEEL_STRENGTH_FIELD_ID);

        verifyThat(SECTION_WIDTH_FIELD_ID, (TextField textField) -> textField.getText().contains("0.4"));
        verifyThat(SECTION_HEIGHT_FIELD_ID, (TextField textField) -> textField.getText().contains("0.6"));
        verifyThat(CONCRETE_STRENGTH_FIELD_ID, (TextField textField) -> textField.getText().contains("4.0"));
        verifyThat(STEEL_STRENGTH_FIELD_ID, (TextField textField) -> textField.getText().contains("5.0"));
    }

    @Test
    public void momentPageButtonShouldBeAvailableOnceGeneratingEnvelopCurve(){
        setSampleForAllInputs();
        clickOn(ENVELOP_CURVE_BUTTON_ID);

        verifyThat(MOMENT_PAGE_BUTTON_ID, ((ToggleButton button) -> !button.isDisabled()));
    }

    @Test
    public void momentPageButtonShouldBeDisabledWhenAnInputIsChanged(){
        setSampleForAllInputs();
        clickOn(ENVELOP_CURVE_BUTTON_ID);
        clickOn(INPUT_PAGE_BUTTON_ID);
        mInputPage.writeValueWithEnter(7.4, DEAD_LOAD_FIELD_ID);

        verifyThat(MOMENT_PAGE_BUTTON_ID, Node::isDisabled);
    }



    /////////////////////////////////////////////////////////  PRIVATE METHODS  ////////////////////////////////////////////////////////

}
