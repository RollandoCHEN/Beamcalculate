package com.beamcalculate.gui;

import com.beamcalculate.TestFXBase;
import com.beamcalculate.pages.InputPage;
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
        clickOn(DIAGRAM_BUTTON);

        verifyThat(DIAGRAM_BUTTON, NodeMatchers.isDisabled());
    }

    @Test
    public void alertMessageShouldShownWhenFillInOnlyGeometryAndLoad(){
        mInputPage.getNSpansBeam(3, new Double[]{5.1, 5.6, 6.2}, new Double[]{0.2, 0.3, 0.2, 0.2});
        mInputPage.setLoad(3.8, 5.9);
        clickOn(DIAGRAM_BUTTON);

    }

}
