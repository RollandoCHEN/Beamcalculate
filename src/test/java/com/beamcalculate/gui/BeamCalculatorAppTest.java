package com.beamcalculate.gui;

import com.beamcalculate.TestFXBase;
import com.beamcalculate.pages.InputPage;
import com.beamcalculate.pages.MomentPage;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;
import java.util.Collection;

import static com.beamcalculate.JavaFXIds.*;
import static org.testfx.api.FxAssert.verifyThat;

@RunWith(Parameterized.class)

public class BeamCalculatorAppTest extends TestFXBase {
    private InputPage mInputPage;
    private MomentPage mMomentPage;

    private final int mTotalSpanNum;
    private final Double[] mSpansLengths;
    private final Double[] mSupportsWidths;
    private final double mSectionHeight;
    private final double mSectionWidth;
    private final double mSlabThickness;
    private final double mPerpenticularSpacing;
    private final double mDeadLoad;
    private final double mLiveLoad;
    private final double mConcreteStrength;
    private final double mStellStrength;
    private final String mDuctibilityClass;

    public BeamCalculatorAppTest(
            int totalSpanNum,
            Double[] spansLengths, Double[] supportsWidths,
            double sectionHeight, double sectionWidth,
            double slabThickness, double perpendicularSpacing,
            double deadLoad, double liveLoad,
            double concreteStrength, double steelStrength, String ductibilityClass
    ) {
        mTotalSpanNum = totalSpanNum;
        mSpansLengths = spansLengths;
        mSupportsWidths = supportsWidths;
        mSectionHeight = sectionHeight;
        mSectionWidth = sectionWidth;
        mSlabThickness = slabThickness;
        mPerpenticularSpacing = perpendicularSpacing;
        mDeadLoad = deadLoad;
        mLiveLoad = liveLoad;
        mConcreteStrength = concreteStrength;
        mStellStrength = steelStrength;
        mDuctibilityClass = ductibilityClass;
    }

    @Parameters
    public static Collection<Object[]> params() {
        return Arrays.asList(new Object[][] {
                {3, new Double[]{5.1, 5.6, 6.2}, new Double[]{0.2, 0.3, 0.2, 0.2},
                        0.4, 0.6, 0.18, 6, 3.8, 5.9, 25, 500, "B"},

                {4, new Double[]{5.1, 5.6, 6.2, 7.1}, new Double[]{0.2, 0.3, 0.2, 0.2, 0.3},
                        0.5, 0.7, 0.18, 6, 7.8, 6.9, 25, 500, "B"}
        });
    }


    @Before
    public void beforeEachTest(){
        mInputPage = new InputPage(this);
        mMomentPage = new MomentPage(this);
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
        verifyThat(SECTION_HEIGHT_FIELD_ID, (TextField textField) -> textField.getText().contains("0.6"));
        verifyThat(CONCRETE_STRENGTH_FIELD_ID, (TextField textField) -> textField.getText().contains("4.0"));
        verifyThat(STEEL_STRENGTH_FIELD_ID, (TextField textField) -> textField.getText().contains("5.0"));
    }

    @Test
    public void redistributionAndRebarCalculatingShouldBeInvisibleWhenMissingInputs(){
        mInputPage.getNSpansBeam(mTotalSpanNum, mSpansLengths, mSupportsWidths).
                setLoad(mDeadLoad, mLiveLoad);
        clickOn(DIAGRAM_BUTTON_ID);
        clickOn("Continuer");       //continue when missing inputs

        verifyThat(REDISTRIBUTION_CHECK_ID, (CheckBox checkBox) -> !checkBox.isVisible());
        verifyThat(CONFIGURATION_BUTTON_ID, (Button button) -> !button.isVisible());
        verifyThat(REBAR_CALCULATE_BUTTON_ID, (Button button) -> !button.isVisible());
    }

    @Test
    public void numOfMethodsShouldBeThree(){
        mInputPage.getNSpansBeam(mTotalSpanNum, mSpansLengths, mSupportsWidths).
                setLoad(mDeadLoad, mLiveLoad);
        clickOn(DIAGRAM_BUTTON_ID);
        clickOn("Continuer");

        verifyThat(METHODS_DISPLAY_CHECK_ID, (HBox hbox) -> hbox.getChildren().size() == 3);
        verifyThat(MOMENT_PAGE_BORDER_PANE_ID, (BorderPane container) -> {
             LineChart lineChart = (LineChart)container.getCenter();
             return lineChart.getData().size() == 6;
        });
    }

    @Test
    public void uncheckMethodShouldRemoveCorrespondingLineOnLineChart(){
        mInputPage.getNSpansBeam(mTotalSpanNum, mSpansLengths, mSupportsWidths).
                setLoad(mDeadLoad, mLiveLoad);
        clickOn(DIAGRAM_BUTTON_ID);
        clickOn("Continuer");
        mMomentPage.uncheckMethodOfNum(3);

        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(METHODS_DISPLAY_CHECK_ID, (HBox hbox) -> hbox.getChildren().size() == 3);
        verifyThat(MOMENT_PAGE_BORDER_PANE_ID, (BorderPane container) -> {
            LineChart lineChart = (LineChart)container.getCenter();
            return lineChart.getData().size() == 4;
        });
    }

}
