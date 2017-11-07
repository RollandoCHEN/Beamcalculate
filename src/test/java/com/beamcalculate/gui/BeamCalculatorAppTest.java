package com.beamcalculate.gui;

import com.beamcalculate.TestFXBase;
import com.beamcalculate.pages.InputPage;
import javafx.scene.control.TextField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.matcher.base.NodeMatchers;

import static com.beamcalculate.JavaFXIds.*;
import static org.testfx.api.FxAssert.verifyThat;


public class BeamCalculatorAppTest extends TestFXBase {
    private InputPage mInputPage;

    @Before
    public void beforeEachTest(){
        mInputPage = new InputPage(this);
    }

    @Test
    public void buttonShouldDisabled(){
        sleep(500);

        verifyThat(DIAGRAM_BUTTON_ID, NodeMatchers.isDisabled());
    }

    @Test
    public void enterSpecialValueShouldBeAllowed(){
        mInputPage.enterValue(".4", SECTION_WIDTH_FIELD_ID).enterValue(",6", SECTION_HEIGHT_FIELD_ID)
                .enterValue("4.", CONCRETE_STRENGTH_FIELD_ID).enterValue("5,", STEEL_STRENGTH_FIELD_ID);

        verifyThat(SECTION_WIDTH_FIELD_ID, (TextField textField) -> textField.getText().contains("0.4"));
        verifyThat(SECTION_HEIGHT_FIELD_ID, (TextField textField) -> textField.getText().contains("0.4"));
        verifyThat(CONCRETE_STRENGTH_FIELD_ID, (TextField textField) -> textField.getText().contains("0.4"));
        verifyThat(STEEL_STRENGTH_FIELD_ID, (TextField textField) -> textField.getText().contains("0.4"));
    }

    @Test
    public void alertMessageShouldShownWhenFillInOnlyGeometryAndLoad(){
        mInputPage.getNSpansBeam(
                3,
                new Double[]{5.1, 5.6, 6.2},
                new Double[]{0.2, 0.3, 0.2, 0.2}
        ).setLoad(3.8, 5.9);
        clickOn(DIAGRAM_BUTTON_ID);

    }

}
