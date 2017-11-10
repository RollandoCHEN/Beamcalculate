package com.beamcalculate.gui;

import com.beamcalculate.TestFXBase;
import com.beamcalculate.pages.InputPage;
import com.beamcalculate.pages.MomentPage;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import org.junit.Before;
import org.junit.Test;
import org.testfx.matcher.base.NodeMatchers;

import static com.beamcalculate.JavaFXIds.*;
import static com.beamcalculate.enums.NumericalFormat.TWO_DECIMALS;
import static org.testfx.api.FxAssert.verifyThat;

public class MomentPageSingleTests extends TestFXBase {

    @Before
    public void beforeEachTest(){
        mInputPage = new InputPage(this);
        mMomentPage = new MomentPage(this);
    }

    @Test
    public void redistributionAndRebarCalculatingShouldBeInvisibleWhenMissingInputs(){
        mInputPage.getNSpansBeam(2, new Double[]{3.2, 3.4}, new Double[]{0.2, 0.3, 0.2}).
                setLoad(5.2, 6.2);
        clickOn(ENVELOP_CURVE_BUTTON_ID);
        mInputPage.clickOnContinue(1);       //continue when missing inputs

        verifyThat(REDISTRIBUTION_CHECK_ID, (CheckBox checkBox) -> !checkBox.isVisible());
        verifyThat(CONFIGURATION_BUTTON_ID, (Button button) -> !button.isVisible());
        verifyThat(REBAR_CALCULATE_BUTTON_ID, (Button button) -> !button.isVisible());
    }

    @Test
    public void abscissaLimitShouldBeCorrectForEachSpan(){
        mInputPage.getNSpansBeam(3, new Double[]{3.2, 3.4, 4.0}, new Double[]{0.2, 0.3, 0.2, 0.2}).
                setLoad(5.2, 6.2);
        clickOn(ENVELOP_CURVE_BUTTON_ID);
        mInputPage.clickOnContinue(1);       //continue when missing inputs
        mMomentPage.chooseIndex(1, METHOD_CHOICE_ID);
        mMomentPage.chooseValue(1, SPAN_CHOICE_ID);

        verifyThat(ABSCISSA_LIMIT_LABEL_ID, NodeMatchers.hasText("(0 ~ " +TWO_DECIMALS.format(3.2) + ")"));
    }
}
