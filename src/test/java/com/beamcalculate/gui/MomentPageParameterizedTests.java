package com.beamcalculate.gui;

import com.beamcalculate.TestFXBase;
import com.beamcalculate.pages.InputPage;
import com.beamcalculate.pages.MomentPage;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;
import java.util.Collection;

import static com.beamcalculate.JavaFXIds.*;
import static org.testfx.api.FxAssert.verifyThat;

@RunWith(Parameterized.class)

public class MomentPageParameterizedTests extends TestFXBase {
    private InputPage mInputPage;
    private MomentPage mMomentPage;

    private final boolean mTSection;
    private final int mTotalSpanNum;
    private final Double[] mSpansLengths;
    private final Double[] mSupportsWidths;
    private final double mSectionHeight;
    private final double mSectionWidth;
    private final double mSlabThickness;
    private final double mPerpendicularSpacing;
    private final double mDeadLoad;
    private final double mLiveLoad;
    private final double mConcreteStrength;
    private final double mSteelStrength;
    private final Character mDuctibilityClass;
    private final int mNumOfContinue;
    private final int mNumOfMethods;

    public MomentPageParameterizedTests(
            boolean tSection,
            int totalSpanNum, Double[] spansLengths, Double[] supportsWidths,
            double sectionHeight, double sectionWidth, double slabThickness, double perpendicularSpacing,
            double deadLoad, double liveLoad,
            double concreteStrength, double steelStrength, Character ductibilityClass,
            int numOfContinue, int numOfMethods
    ) {
        mTSection = tSection;
        mTotalSpanNum = totalSpanNum;
        mSpansLengths = spansLengths;
        mSupportsWidths = supportsWidths;
        mSectionHeight = sectionHeight;
        mSectionWidth = sectionWidth;
        mSlabThickness = slabThickness;
        mPerpendicularSpacing = perpendicularSpacing;
        mDeadLoad = deadLoad;
        mLiveLoad = liveLoad;
        mConcreteStrength = concreteStrength;
        mSteelStrength = steelStrength;
        mDuctibilityClass = ductibilityClass;

        mNumOfContinue = numOfContinue;
        mNumOfMethods = numOfMethods;
    }

    @Parameters
    public static Collection<Object[]> params() {
        return Arrays.asList(new Object[][] {
                {false, 3, new Double[]{5.1, 5.6, 6.2}, new Double[]{0.2, 0.3, 0.2, 0.2},
                        0.4, 0.6, 0, 0,
                        3.8, 5.9,
                        25, 500, 'B',
                        0, 3
                },
                {false, 3, new Double[]{5.1, 5.6, 6.2}, new Double[]{0.2, 0.3, 0.2, 0.2},
                        0, 0, 0, 0,
                        3.8, 5.9,
                        25, 500, 'B',
                        1, 3
                },                                      //missing parameter
                {false, 4, new Double[]{5.1, 5.0, 6.2, 7.1}, new Double[]{0.2, 0.3, 0.2, 0.2, 0.3},
                        0.5, 0.7, 0, 0,
                        3.0, 6.9,
                        25, 500, 'B',
                        1, 2
                }                                       // forfaitaire condition is not satisfied
        });
    }


    @Before
    public void beforeEachTest(){
        mInputPage = new InputPage(this);
        mMomentPage = new MomentPage(this);
    }

    @Test
    public void numOfMethodsShouldBeCorrect(){
        allInputs();
        clickOn(ENVELOP_CURVE_BUTTON_ID);
        mInputPage.clickOnContinue(mNumOfContinue);     //continue when missing inputs


        verifyThat(METHODS_DISPLAY_CHECK_ID, (HBox hbox) -> hbox.getChildren().size() == mNumOfMethods);
        verifyThat(MOMENT_PAGE_BORDER_PANE_ID, (BorderPane container) -> {
             LineChart lineChart = (LineChart)container.getCenter();
             return lineChart.getData().size() == 2 * mNumOfMethods;
        });
    }

    @Test
    public void uncheckMethodShouldRemoveCorrespondingLineOnLineChart(){
        allInputs();
        clickOn(ENVELOP_CURVE_BUTTON_ID);
        mInputPage.clickOnContinue(mNumOfContinue);       //continue when missing inputs
        mMomentPage.uncheckMethodOfNum(1);

        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(METHODS_DISPLAY_CHECK_ID, (HBox hbox) -> hbox.getChildren().size() == mNumOfMethods);
        verifyThat(MOMENT_PAGE_BORDER_PANE_ID, (BorderPane container) -> {
            LineChart lineChart = (LineChart)container.getCenter();
            return lineChart.getData().size() == 2 * (mNumOfMethods-1);
        });
    }

    private void allInputs(){
        mInputPage.setAllInputs(
                mTSection, mTotalSpanNum, mSpansLengths, mSupportsWidths,
                mSectionWidth, mSectionHeight, mSlabThickness, mPerpendicularSpacing,
                mDeadLoad, mLiveLoad,
                mConcreteStrength, mSteelStrength, mDuctibilityClass
        );
    }
}
