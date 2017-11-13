package com.beamcalculate.gui;

import com.beamcalculate.TestFXBase;
import com.beamcalculate.pages.InputPage;
import com.beamcalculate.pages.MomentPage;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.junit.Before;
import org.junit.Test;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

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
        setNecessaryInputsToGetToMomentPage();

        verifyThat(REDISTRIBUTION_CHECK_ID, (CheckBox checkBox) -> !checkBox.isVisible());
        verifyThat(CONFIGURATION_BUTTON_ID, (Button button) -> !button.isVisible());
        verifyThat(REBAR_CALCULATE_BUTTON_ID, (Button button) -> !button.isVisible());
    }

    @Test
    public void totalPointsSpinnerShouldBeAvailableByDefault(){
        setNecessaryInputsToGetToMomentPage();

        verifyThat(TOTAL_POINTS_NUM_SPINNER_ID, ((Spinner spinner) -> !spinner.isDisabled()));
    }

    @Test
    public void totalPointsSpinnerShouldBeDisabledWhenNothingOnLineChart(){
        setNecessaryInputsToGetToMomentPage();
        mMomentPage.uncheckAllMethods();

        verifyThat(TOTAL_POINTS_NUM_SPINNER_ID, Node::isDisabled);
    }

    @Test
    public void redistributionCheckBoxShouldBeAbleToAddAMomentLineToLineChart(){
        setSampleForAllInputs();
        clickOn(ENVELOP_CURVE_BUTTON_ID);
        clickOn(REDISTRIBUTION_CHECK_ID);

        verifyThat(MOMENT_PAGE_BORDER_PANE_ID, (BorderPane container) -> {
            LineChart lineChart = (LineChart)container.getCenter();
            return lineChart.getData().size() == 8;
        });
    }

    @Test
    public void availabilityOfRebarCalculateButtonShouldBeCorrect(){
        setSampleForAllInputs();
        clickOn(ENVELOP_CURVE_BUTTON_ID);
        verifyThat(REBAR_CALCULATE_BUTTON_ID, Node::isDisabled);

        mMomentPage.chooseIndex(1, METHOD_CHOICE_ID);
        verifyThat(REBAR_CALCULATE_BUTTON_ID, ((Button button) -> !button.isDisabled()));
    }

    @Test
    public void availabilityOfMomentCalculateButtonShouldBeCorrect(){
        setNecessaryInputsToGetToMomentPage();
        verifyThat(MOMENT_CALCULATE_BUTTON_ID, Node::isDisabled);

        mMomentPage.chooseIndex(1, METHOD_CHOICE_ID);
        verifyThat(MOMENT_CALCULATE_BUTTON_ID, Node::isDisabled);

        mMomentPage.chooseValue(1, SPAN_CHOICE_ID);
        verifyThat(MOMENT_CALCULATE_BUTTON_ID, Node::isDisabled);

        mMomentPage.enterAbscissa(2.0);
        verifyThat(MOMENT_CALCULATE_BUTTON_ID, ((Button button) -> !button.isDisabled()));
    }

    @Test
    public void abscissaLimitShouldBeCorrectForEachSpan(){
        setNecessaryInputsToGetToMomentPage();
        mMomentPage.chooseIndex(1, METHOD_CHOICE_ID);
        mMomentPage.chooseValue(1, SPAN_CHOICE_ID);
        verifyThat(ABSCISSA_LIMIT_LABEL_ID, NodeMatchers.hasText("(0 ~ " +TWO_DECIMALS.format(3.2) + ")"));
        mMomentPage.chooseValue(2, SPAN_CHOICE_ID);
        verifyThat(ABSCISSA_LIMIT_LABEL_ID, NodeMatchers.hasText("(0 ~ " +TWO_DECIMALS.format(3.4) + ")"));
        mMomentPage.chooseValue(3, SPAN_CHOICE_ID);
        verifyThat(ABSCISSA_LIMIT_LABEL_ID, NodeMatchers.hasText("(0 ~ " +TWO_DECIMALS.format(4.0) + ")"));

        mMomentPage.chooseIndex(2, METHOD_CHOICE_ID);
        mMomentPage.chooseValue(1, SPAN_CHOICE_ID);
        verifyThat(ABSCISSA_LIMIT_LABEL_ID, NodeMatchers.hasText("(0 ~ " +TWO_DECIMALS.format(3.45) + ")"));
        mMomentPage.chooseValue(2, SPAN_CHOICE_ID);
        verifyThat(ABSCISSA_LIMIT_LABEL_ID, NodeMatchers.hasText("(0 ~ " +TWO_DECIMALS.format(3.65) + ")"));
        mMomentPage.chooseValue(3, SPAN_CHOICE_ID);
        verifyThat(ABSCISSA_LIMIT_LABEL_ID, NodeMatchers.hasText("(0 ~ " +TWO_DECIMALS.format(4.2) + ")"));
    }

    @Test
    public void enterAbscissaValueInTheLimitShouldBeAllowed(){
        setNecessaryInputsToGetToMomentPage();
        mMomentPage.chooseIndex(1, METHOD_CHOICE_ID)
                .chooseValue(1, SPAN_CHOICE_ID)
                .enterAbscissa(2.1);
        verifyThat(mMomentPage.getAbscissaField(), (TextField textField) -> textField.getText().contains("2.1"));

        mMomentPage.enterAbscissa(3.201);
        verifyThat(mMomentPage.getAbscissaField(), (TextField textField) -> textField.getText().isEmpty());

        mMomentPage.chooseValue(3, SPAN_CHOICE_ID)
                .enterAbscissa(3.99);
        verifyThat(mMomentPage.getAbscissaField(),(TextField textField) -> textField.getText().contains("3.99") );

        mMomentPage.enterAbscissa(4.01);
        verifyThat(mMomentPage.getAbscissaField(), (TextField textField) -> textField.getText().isEmpty());
    }


    ////////////////////////////////////////////////  PRIVATE METHODS  /////////////////////////////////////////////////////

    private void setNecessaryInputsToGetToMomentPage() {
        mInputPage.getNSpansBeam(3, new Double[]{3.2, 3.4, 4.0}, new Double[]{0.2, 0.3, 0.2, 0.2}).
                setLoad(5.2, 6.2);
        clickOn(ENVELOP_CURVE_BUTTON_ID);
        mInputPage.clickOnContinue(1);          //continue when missing inputs
    }
}
