package com.beamcalculate.model.result;

import static com.beamcalculate.model.LanguageManager.getBundleText;
import static com.beamcalculate.model.LanguageManager.getResourceBundle;
import com.beamcalculate.controllers.InputPageController;
import com.beamcalculate.controllers.RebarCasesPageController;
import com.beamcalculate.custom.alert.InfoMessage;
import com.beamcalculate.custom.node.HoveredThresholdNode;
import com.beamcalculate.model.calculate.ELUCombination;
import com.beamcalculate.model.calculate.MomentRedistribution;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.calculate.span_function.AbstractSpanMoment;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.enums.UltimateCase;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT;
import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.NumericalFormat.FOURDECIMALS;
import static com.beamcalculate.enums.NumericalFormat.THREEDECIMALS;
import static com.beamcalculate.enums.UltimateCase.MAX;
import static com.beamcalculate.enums.UltimateCase.MIN;

public class MomentLineChart {

    private BorderPane mBorderPaneContainer;
    private Spinner<Integer> mIntegerSpinner;
    private HBox mMethodsCheckHBox;
    private Label mConditionInfoLabel;
    private CheckBox mRedistributionCheck;
    private Button mConfigurationButton;
    private ChoiceBox<String> mMethodChoice;
    private Button mRebarCalculateButton;
    private ChoiceBox<Integer> mSpanChoiceBox;
    private TextField mAbscissaField;
    private Button mMomentCalculateButton;
    private Label mMaxMomentLabel;
    private Label mMinMomentLabel;

    private final LineChart<Number, Number> mLineChart;
    private BooleanBinding mDisableSpinnerBoolean;
    private StringProperty mMethodChoiceValue = new SimpleStringProperty();
    private NumberAxis mXAxis = new NumberAxis();
    private NumberAxis mYAxis = new NumberAxis();
    private Map<String, AbstractSpanMoment> mMethodChoiceMap = new HashMap<>();
    private GridPane mGridPaneTop;
    private Map<String, XYChart.Series<Number, Number>> mStringSeriesMap = new HashMap<>();
    private Map<Integer, StringProperty> mEnteredRdsCoef = new HashMap<>();

    private static Stage mCrossSectionStage = new Stage();

    private void addRealNumberValidation(TextField textField) {
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                if (!textField.getText().matches("\\d+\\.\\d+|\\d+")) {
                    //when it not matches the pattern
                    //set the textField empty
                    textField.setText("");
                }
            }
        });
    }

    public MomentLineChart(
            SpanMomentFunction spanMomentFunction,
            Spinner<Integer> numOfSpanSpinner, HBox methodsCheckHBox, Label conditionInfoLabel,
            CheckBox redistributionCheck, Button configurationButton, ChoiceBox<String> methodChoice,
            Button rebarCalculateButton, ChoiceBox<Integer> spanChoice, TextField abscissaField,
            Button momentCalculateButton, Label maxMomentValueLabel, Label minMomentValueLabel,
            BorderPane borderPaneContainer
    ) {
        mIntegerSpinner = numOfSpanSpinner;
        mMethodsCheckHBox = methodsCheckHBox;
        mConditionInfoLabel = conditionInfoLabel;
        mRedistributionCheck = redistributionCheck;
        mConfigurationButton = configurationButton;
        mMethodChoice = methodChoice;
        mRebarCalculateButton = rebarCalculateButton;
        mSpanChoiceBox = spanChoice;
        mAbscissaField = abscissaField;
        mMomentCalculateButton = momentCalculateButton;
        mMaxMomentLabel = maxMomentValueLabel;
        mMinMomentLabel = minMomentValueLabel;
        mBorderPaneContainer = borderPaneContainer;


        ELUCombination combination = new ELUCombination(spanMomentFunction);

        // match the calculate method name to the related spanMomentFunction
        mMethodChoiceMap.put(spanMomentFunction.getMethod(), spanMomentFunction);

        //defining the axes
        mXAxis = defineAxis(spanMomentFunction).get(0);
        mYAxis = defineAxis(spanMomentFunction).get(1);

        //creating the chart
        mLineChart = new LineChart<>(mXAxis, mYAxis);
        mLineChart.setTitle("");
        mLineChart.setCursor(Cursor.CROSSHAIR);

        //Define series
        XYChart.Series maxELUSeries = new XYChart.Series();
        createMomentSeries(mIntegerSpinner.getValue(), combination, MAX, maxELUSeries);
        maxELUSeries.setName(getBundleText("label.max") + " - " + spanMomentFunction.getMethod());

        XYChart.Series minELUSeries = new XYChart.Series();
        createMomentSeries(mIntegerSpinner.getValue(), combination, MIN, minELUSeries);
        minELUSeries.setName(getBundleText("label.min") + " - " + spanMomentFunction.getMethod());

        //through this mStringSeriesMap to store all the series
        //when add series to the line chart, use also mStringSeriesMap, so when remove series, we can identify the series ??

        mStringSeriesMap.put(spanMomentFunction.getMethod() + "_" + getBundleText("label.max"), maxELUSeries);
        mStringSeriesMap.put(spanMomentFunction.getMethod() + "_" + getBundleText("label.min"), minELUSeries);

        mLineChart.getData().addAll(
                mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.max")),
                mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.min"))
        );
        mBorderPaneContainer.setCenter(mLineChart);

        //Set action for the spinner to re-load the moment chart line
        mIntegerSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxELUSeries.getData().clear();
            minELUSeries.getData().clear();
            createMomentSeries(mIntegerSpinner.getValue(), combination, MAX, maxELUSeries);
            createMomentSeries(mIntegerSpinner.getValue(), combination, MIN, minELUSeries);
        });

        //Calculating Module : moment calculating and rebar calculating

        //Method choice
        mMethodChoice.setItems(FXCollections.observableArrayList(spanMomentFunction.getMethod()));
        mMethodChoiceValue.bind(mMethodChoice.valueProperty());

        mSpanChoiceBox.setItems(FXCollections.observableArrayList(Geometry.spansLengthMap().keySet()));
        mSpanChoiceBox.setOnAction(e -> {
            int selectedSpanId = mSpanChoiceBox.getValue();
            addMaxValueValidation(mAbscissaField, mMethodChoiceMap.get(mMethodChoiceValue.get()).getCalculateSpanLengthMap().get(selectedSpanId));
        });
        mSpanChoiceBox.disableProperty().bind(Bindings.isNull(mMethodChoice.valueProperty()));

        mAbscissaField.disableProperty().bind(Bindings.isNull(mSpanChoiceBox.valueProperty()));

        //Moment Calculating Button setting : disable value and on action
        mMomentCalculateButton.disableProperty().bind(
                Bindings.isNull(mMethodChoice.valueProperty())
                        .or(Bindings.isNull(mSpanChoiceBox.valueProperty()))
                        .or(Bindings.isEmpty(mAbscissaField.textProperty()))
        );
        mMomentCalculateButton.setOnAction(e -> {
            double maxY, minY;
            int chosenSpan = mSpanChoiceBox.getValue();
            double enteredXValue = Double.parseDouble(mAbscissaField.getText());
            if (mMethodChoiceValue.get().equals(TROIS_MOMENT_R.getMethodName())){
                SpanMomentFunction_SpecialLoadCase newSpanMoment = (SpanMomentFunction_SpecialLoadCase)mMethodChoiceMap.get(mMethodChoiceValue.get());
                maxY = newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                        enteredXValue, chosenSpan, MAX
                );
                minY = newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                        enteredXValue, chosenSpan, MIN
                );
            }else{
                ELUCombination eluCombination = new ELUCombination(mMethodChoiceMap.get(mMethodChoiceValue.get()));
                maxY = eluCombination.getCombinedUltimateMomentAtXOfSpan(
                        enteredXValue, chosenSpan, MAX
                );
                minY = eluCombination.getCombinedUltimateMomentAtXOfSpan(
                        enteredXValue, chosenSpan, MIN
                );
            }
            mMaxMomentLabel.setText(FOURDECIMALS.getDecimalFormat().format(maxY));
            mMinMomentLabel.setText(FOURDECIMALS.getDecimalFormat().format(minY));
        });

        //Rebar Calculating Button setting : disable value and on action
        mRebarCalculateButton.disableProperty().bind(
                Bindings.isNull(mMethodChoice.valueProperty())
                        .or(InputPageController.isDisabledRebarCalculateProperty())
        );
        mRebarCalculateButton.setOnAction(event -> {
            Reinforcement reinforcement = new Reinforcement(mMethodChoiceMap.get(mMethodChoiceValue.get()));
            Rebar rebar = new Rebar(reinforcement);

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RebarCasesPage.fxml"),
                        getResourceBundle());
                Parent root = fxmlLoader.load();

                RebarCasesPageController controller = fxmlLoader.getController();

                controller.setRebar(rebar);
                controller.generateRebarSelectionCasesTable();

                int maxNumOfCases = 1;
                for (int spanId=1; spanId < Geometry.getNumSpan()+1; spanId++) {
                    int rebarCases = rebar.getRebarCasesListOfSpan(spanId).size();
                    maxNumOfCases = Math.max(rebarCases, maxNumOfCases);
                }
                // 60 is the padding in the grid pane, around the left and right grid pane
                double sceneWidth = controller.getLeftGridPaneWidth() + controller.getRightGridPaneWidth() + 80;

                double sceneHeight = Math.max(maxNumOfCases * 110 + 100, 970);

                Scene rebarSelectionScene = new Scene(root, sceneWidth, sceneHeight);
                Stage rebarSelectionStage = new Stage();
                rebarSelectionStage.setTitle(getBundleText("window.title.rebarChoices"));
                rebarSelectionStage.getIcons().add(new Image("image/rebar.png"));
                rebarSelectionStage.setScene(rebarSelectionScene);
                rebarSelectionStage.setResizable(true);

                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                if (primaryScreenBounds.getHeight() < rebarSelectionStage.getScene().getHeight()){
                    rebarSelectionStage.setHeight(primaryScreenBounds.getHeight());
                }
                if (primaryScreenBounds.getWidth() < rebarSelectionStage.getScene().getWidth()){
                    rebarSelectionStage.setHeight(primaryScreenBounds.getWidth());
                }

                rebarSelectionStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //Set checkbox to show or hide line chart
        CheckBox methodCheck = new CheckBox(spanMomentFunction.getMethod());
        methodCheck.setSelected(true);
        methodCheck.selectedProperty().addListener((arg0, oldValue, newValue) -> {
            if (newValue) {
                mLineChart.getData().addAll(
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.max")),
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.min"))
                );
            } else {
                mLineChart.getData().removeAll(
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.max")),
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.min"))
                );
            }
        });
        mMethodsCheckHBox.getChildren().clear();                 //Clear the existed check boxes
        mMethodsCheckHBox.getChildren().add(methodCheck);

        mDisableSpinnerBoolean = Bindings.not(methodCheck.selectedProperty());
        mIntegerSpinner.disableProperty().bind(mDisableSpinnerBoolean);

        //Methods applying condition label
        setClickableStyle(mConditionInfoLabel);
        mConditionInfoLabel.setOnMouseClicked(e -> new InfoMessage(
                "info.title.methodConditions",
                "info.head.methodConditions",
                "info.content.methodConditions"
        ));


        //if the method of calculate is "3 moment", add redistribution for the method
        if (spanMomentFunction.getMethod().equals(TROIS_MOMENT.getMethodName())
                && !InputPageController.isDisabledRebarCalculate()
                ) {
            addRedistribution(spanMomentFunction);
        }


    }

    public MomentLineChart(
            Spinner<Integer> numOfSpanSpinner, HBox methodsCheckHBox, Label conditionInfoLabel,
            CheckBox redistributionCheck, Button configurationButton, ChoiceBox<String> methodChoice,
            Button rebarCalculateButton, ChoiceBox<Integer> spanChoice, TextField abscissaField,
            Button momentCalculateButton, Label maxMomentValueLabel, Label minMomentValueLabel,
            BorderPane borderPaneContainer, SpanMomentFunction... spanMomentFunctions
    ) {
        this(
                spanMomentFunctions[0],
                numOfSpanSpinner, methodsCheckHBox, conditionInfoLabel, redistributionCheck,
                configurationButton, methodChoice, rebarCalculateButton, spanChoice,
                abscissaField, momentCalculateButton, maxMomentValueLabel, minMomentValueLabel,
                borderPaneContainer
        );
        for (int i=1; i<spanMomentFunctions.length; i++) {
            addNewMomentChart(spanMomentFunctions[i]);
        }
    }

    private void addNewMomentChart(SpanMomentFunction spanMomentFunction) {
        ELUCombination combination = new ELUCombination(spanMomentFunction);

        // match the calculate method name to the related spanMomentFunction
        mMethodChoiceMap.put(spanMomentFunction.getMethod(), spanMomentFunction);

        //        add new series to line chart

        XYChart.Series newMaxELUSeries = new XYChart.Series();
        createMomentSeries(mIntegerSpinner.getValue(), combination, MAX, newMaxELUSeries);


        XYChart.Series newMinELUSeries = new XYChart.Series();
        createMomentSeries(mIntegerSpinner.getValue(), combination, MIN, newMinELUSeries);

        mStringSeriesMap.put(spanMomentFunction.getMethod() + "_" + getBundleText("label.max"), newMaxELUSeries);
        mStringSeriesMap.put(spanMomentFunction.getMethod() + "_" + getBundleText("label.min"), newMinELUSeries);
        mLineChart.getData().addAll(
                mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.max")),
                mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.min"))
        );

//        bind the spinner listener to the new series

        mIntegerSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            newMaxELUSeries.getData().clear();
            newMinELUSeries.getData().clear();
            createMomentSeries(mIntegerSpinner.getValue(), combination, MAX, newMaxELUSeries);
            createMomentSeries(mIntegerSpinner.getValue(), combination, MIN, newMinELUSeries);
        });

//        check box to show and hide new series

        CheckBox newMethodCheck = new CheckBox(spanMomentFunction.getMethod());
        newMethodCheck.setSelected(true);
        mMethodsCheckHBox.getChildren().addAll(newMethodCheck);

        newMethodCheck.selectedProperty().addListener((arg0, oldValue, newValue) -> {
            if (newValue) {
                mLineChart.getData().addAll(
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.max")),
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.min"))
                );
            } else {
                mLineChart.getData().removeAll(
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.max")),
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + getBundleText("label.min"))
                );
            }
        });

        mDisableSpinnerBoolean = mDisableSpinnerBoolean.and(Bindings.not(newMethodCheck.selectedProperty()));
        mIntegerSpinner.disableProperty().bind(mDisableSpinnerBoolean);

//        add margin to the y axis

        double maxSpanMomentValue = -Math.max(-mYAxis.getLowerBound(), 1.2 * combination.getUltimateMomentValue(MAX));
        double maxSupportMomentValue = -Math.min(-mYAxis.getUpperBound(), 1.2 * combination.getUltimateMomentValue(MIN));

        mYAxis.lowerBoundProperty().set(maxSpanMomentValue);
        mYAxis.upperBoundProperty().set(maxSupportMomentValue);

        mMethodChoice.getItems().add(spanMomentFunction.getMethod());

        //        if the method of calculate is "3 moment", add redistribution for the method

        if (spanMomentFunction.getMethod().equals(TROIS_MOMENT.getMethodName())
                && !InputPageController.isDisabledRebarCalculate()
                ) {
            addRedistribution(spanMomentFunction);
        }
    }

    private void setClickableStyle(Label label) {
        label.setStyle(
                "-fx-text-fill: black; -fx-font-style: italic;"
        );
        label.setOnMouseEntered(e -> label.setStyle(
                "-fx-text-fill: blue; -fx-underline : true; -fx-font-style: italic;"
        ));
        label.setOnMouseExited(e -> label.setStyle(
                "-fx-text-fill: black; -fx-font-style: italic;"
        ));
    }

    private void addMaxValueValidation(TextField textField, double maxValue) {
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                if (!textField.getText().matches("\\d+\\.\\d+|\\d+")) {
                    //when it not matches the pattern
                    //set the textField empty
                    textField.setText("");
                } else if (Double.parseDouble(textField.getText()) > maxValue) {
                    textField.setText("");
                }
            }
        });
    }

    public static List<NumberAxis> defineAxis(AbstractSpanMoment spanMomentFunction){
        double maxSpanMomentValue;
        double maxSupportMomentValue;

        if(spanMomentFunction.getMethod().equals(TROIS_MOMENT_R.getMethodName())) {
            SpanMomentFunction_SpecialLoadCase newSpanMomentFunction = (SpanMomentFunction_SpecialLoadCase) spanMomentFunction;
            maxSupportMomentValue = newSpanMomentFunction.getUltimateMomentValue(MIN);
            maxSpanMomentValue = newSpanMomentFunction.getUltimateMomentValue(MAX);
        }else {
            ELUCombination combination = new ELUCombination(spanMomentFunction);
            maxSupportMomentValue = combination.getUltimateMomentValue(MIN);
            maxSpanMomentValue = combination.getUltimateMomentValue(MAX);
        }

        NumberAxis xAxis = new NumberAxis(-1, Geometry.getTotalLength() + 1, 1);
        NumberAxis yAxis = new NumberAxis(- 1.2 * maxSpanMomentValue, - 1.2 * maxSupportMomentValue, 0.05);

        xAxis.setLabel(getBundleText("label.abscissa") + " (" + getBundleText("unit.length.m") + ")");
        yAxis.setLabel(getBundleText("label.ordinate") + " (" + getBundleText("unit.moment") + ")");

        List<NumberAxis> axisList = new ArrayList<>();
        axisList.add(xAxis);
        axisList.add(yAxis);

        return axisList;
    }

    public static void createMomentSeries(
            int numSection,
            ELUCombination eluCombination, UltimateCase ultimateCase,
            XYChart.Series<Number, Number> series
    ) {

        for (int spanId = 1; spanId < Geometry.getNumSpan() + 1; spanId++) {

            double spanLength = eluCombination.getSpanMomentFunction().getCalculateSpanLengthMap().get(spanId);
            double spanLocalX = 0;

            String calculateMethod = eluCombination.getSpanMomentFunction().getMethod();
            double globalX = getGlobalX(spanId, spanLocalX, calculateMethod);

            for (int i = 0; i < numSection + 1; i++) {             // Number of data (moment value) is numSection+1
                double moment = -eluCombination.getCombinedUltimateMomentAtXOfSpan(spanLocalX, spanId, ultimateCase);         // negative just because can't inverse the Y axis to show the span_function moment underside of 0 axis
                final XYChart.Data<Number, Number> data = new XYChart.Data<>(globalX, moment);
                data.setNode(new HoveredThresholdNode(globalX, spanLocalX, moment));
                series.getData().add(data);
                spanLocalX += spanLength / numSection;
                globalX += spanLength / numSection;
            }
        }
        series.setName(getBundleText("label." + ultimateCase.toString().toLowerCase()) + " - " + eluCombination.getSpanMomentFunction().getMethod());
    }

    public static void createRedistributionMomentSeries(
            int numSection,
            SpanMomentFunction_SpecialLoadCase spanMomentFunction, UltimateCase ultimateCase,
            XYChart.Series series
    ) {
        spanMomentFunction.getSpanMomentFunctionMap().forEach((spanId, loadCaseMomentFunctionMap) -> {
            double spanLength = Geometry.getEffectiveSpansLengthMap().get(spanId);
            double spanLocalX = 0;
            double globalX = getGlobalX(spanId, spanLocalX, TROIS_MOMENT.getMethodName());

            for (int i = 0; i < numSection + 1; i++) {             // Number of data (moment value) is numSection+1
                double moment = -spanMomentFunction.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                        spanLocalX, spanId, ultimateCase
                );         // negative just because can't inverse the Y axis to show the span_function moment underside of 0 axis
                final XYChart.Data<Double, Double> data = new XYChart.Data<>(globalX, moment);
                data.setNode(new HoveredThresholdNode(globalX, spanLocalX, moment));
                series.getData().add(data);
                spanLocalX += spanLength / numSection;
                globalX += spanLength / numSection;
            }
        });
        series.setName(getBundleText("label."
                + ultimateCase.toString().toLowerCase())
                + " - "
                + TROIS_MOMENT_R.getMethodName());
    }

    public static double getGlobalX(int spanId, double spanLocalX, String method) {
        double globalX = spanLocalX;
        if (TROIS_MOMENT.getMethodName().equals(method)
                || TROIS_MOMENT_R.getMethodName().equals(method)) {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preX;
                if (preSpanId == 0) {
                    preX = Geometry.supportWidthMap().get(1) / 2;
                } else {
                    preX = Geometry.getEffectiveSpansLengthMap().get(preSpanId);
                }
                globalX += preX;
            }
        } else {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preSpanLength = 0;
                double preSupportLength;
                if (preSpanId == 0) {
                    preSupportLength = Geometry.supportWidthMap().get(1);
                } else {
                    preSpanLength = Geometry.spansLengthMap().get(preSpanId);
                    preSupportLength = Geometry.supportWidthMap().get(preSpanId + 1);
                }
                globalX += (preSpanLength + preSupportLength);
            }
        }
        return globalX;
    }

    // TODO Simplify this method by removing spanId parameter
    public static double getSpanLocalX(int spanId, double globalX, String method) {
        double spanLocalX = globalX;
        if (TROIS_MOMENT.getMethodName().equals(method)) {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preX;
                if (preSpanId == 0) {
                    preX = Geometry.supportWidthMap().get(1) / 2;
                } else {
                    preX = Geometry.getEffectiveSpansLengthMap().get(preSpanId);
                }
                spanLocalX -= preX;
            }
        } else {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preSpanLength = 0;
                double preSupportLength;
                if (preSpanId == 0) {
                    preSupportLength = Geometry.supportWidthMap().get(1);
                } else {
                    preSpanLength = Geometry.spansLengthMap().get(preSpanId);
                    preSupportLength = Geometry.supportWidthMap().get(preSpanId + 1);
                }
                spanLocalX -= (preSpanLength + preSupportLength);
            }
        }
        return spanLocalX;
    }


    private void addRedistribution(SpanMomentFunction spanMomentFunction) {

        mDisableSpinnerBoolean = mDisableSpinnerBoolean.and(Bindings.not(mRedistributionCheck.selectedProperty()));
        mIntegerSpinner.disableProperty().bind(mDisableSpinnerBoolean);

        ELUCombination combination = new ELUCombination(spanMomentFunction);
        MomentRedistribution momentRedistribution = new MomentRedistribution(combination);

        Map<Integer, Double> calculatedFinalRedCoefMap = momentRedistribution.getFinalRedistributionCoefMap();
        Map<Integer, Double> calculatedRedCoefMap = momentRedistribution.getRedistributionCoefMap();

        Map<Integer, Double> usedRedCoefMap = new HashMap<>();
        calculatedFinalRedCoefMap.forEach(usedRedCoefMap::put);

        calculateRedistributionMoment(spanMomentFunction, usedRedCoefMap);

        //TODO This is not the correct way to add method to the method choice box
        mMethodChoice.getItems().add(TROIS_MOMENT_R.getMethodName());

        mRedistributionCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            XYChart.Series maxELUSeries = new XYChart.Series();
            XYChart.Series minELUSeries = new XYChart.Series();
            if (newValue) {
                for (int i = 1; i < Geometry.getNumSupport(); i++) {
                    try {
                        usedRedCoefMap.put(i, Double.parseDouble(mEnteredRdsCoef.get(i).get()));
                    } catch (Exception exp) {
                        usedRedCoefMap.put(i, calculatedFinalRedCoefMap.get(i));
                    }
                }

                SpanMomentFunction_SpecialLoadCase newSpanMomentFunction = calculateRedistributionMoment(spanMomentFunction, usedRedCoefMap);

                createRedistributionMomentSeries(mIntegerSpinner.getValue(), newSpanMomentFunction, MAX, maxELUSeries);

                createRedistributionMomentSeries(mIntegerSpinner.getValue(), newSpanMomentFunction, MIN, minELUSeries);

                mIntegerSpinner.valueProperty().addListener((observable1, oldValue1, newValue1) -> {
                    maxELUSeries.getData().clear();
                    minELUSeries.getData().clear();
                    createRedistributionMomentSeries(mIntegerSpinner.getValue(), newSpanMomentFunction, MAX, maxELUSeries);
                    createRedistributionMomentSeries(mIntegerSpinner.getValue(), newSpanMomentFunction, MIN, minELUSeries);
                });


                mStringSeriesMap.put(TROIS_MOMENT.getMethodName() + "_ReducedMAX", maxELUSeries);
                mStringSeriesMap.put(TROIS_MOMENT.getMethodName() + "_ReducedMIN", minELUSeries);

                mLineChart.getData().addAll(
                        mStringSeriesMap.get(TROIS_MOMENT.getMethodName() + "_ReducedMAX"),
                        mStringSeriesMap.get(TROIS_MOMENT.getMethodName() + "_ReducedMIN")
                );
            } else {
                mLineChart.getData().removeAll(
                        mStringSeriesMap.get(TROIS_MOMENT.getMethodName() + "_ReducedMAX"),
                        mStringSeriesMap.get(TROIS_MOMENT.getMethodName() + "_ReducedMIN")
                );

            }
        });

        VBox paramNameVBox = new VBox();
        paramNameVBox.setSpacing(15);
        Label blank = new Label("");
        Label rdsCoef = new Label(getBundleText("label.theoRdsCoef"));
        Label minRdsCoef = new Label(getBundleText("label.minRdsCoef"));
        Label finalRdsCoef = new Label(getBundleText("label.finalRdsCoef"));
        blank.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
        paramNameVBox.getChildren().addAll(blank, rdsCoef, minRdsCoef, finalRdsCoef);


        HBox paramValuesHBox = new HBox();
        paramValuesHBox.setSpacing(20);
        paramValuesHBox.setAlignment(Pos.CENTER);
        calculatedRedCoefMap.forEach((supportId, coef) -> {
            VBox supportParamValueVBox = new VBox();
            supportParamValueVBox.setSpacing(15);
            Label sectionLabel = new Label(getBundleText("label.support") + " " + supportId.toString());
            sectionLabel.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
            Label rdsCoefValue = new Label(
                    THREEDECIMALS.getDecimalFormat().format(coef)
            );
            Label finalCoefValue = new Label(
                    THREEDECIMALS.getDecimalFormat().format(calculatedFinalRedCoefMap.get(supportId))
            );

            TextField coefValue = new TextField();
            coefValue.setPrefWidth(65);
            coefValue.textProperty().setValue(
                    THREEDECIMALS.getDecimalFormat().format(calculatedFinalRedCoefMap.get(supportId))
            );
            if (calculatedFinalRedCoefMap.get(supportId) == 1) {
                coefValue.setDisable(true);
            }
            addRealNumberValidation(coefValue);
            coefValue.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue) { //when focus lost
                    try {
                        if (Double.parseDouble(coefValue.getText()) <
                                calculatedFinalRedCoefMap.get(supportId) - 0.001
                                || Double.parseDouble(coefValue.getText()) > 1.0
                                ) {
                            //set the textField empty
                            coefValue.setText("");
                        }
                    } catch (Exception exp) {
                        System.out.println(getBundleText("message.enterCoef"));
                    }
                }
            });
            StringProperty stringProperty = new SimpleStringProperty();
            stringProperty.bind(coefValue.textProperty());
            mEnteredRdsCoef.put(supportId, stringProperty);

            supportParamValueVBox.getChildren().addAll(sectionLabel, rdsCoefValue, finalCoefValue, coefValue);
            paramValuesHBox.getChildren().add(supportParamValueVBox);
        });

        HBox centerHBox = new HBox();
        centerHBox.setSpacing(20);
        centerHBox.setAlignment(Pos.CENTER);
        centerHBox.getChildren().addAll(paramNameVBox, paramValuesHBox);

        HBox bottomHBox = new HBox();
        bottomHBox.setSpacing(20);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        Button confirmButton = new Button(getBundleText("button.ok"));
        Button applyButton = new Button(getBundleText("button.apply"));
        bottomHBox.getChildren().addAll(applyButton, confirmButton);
        applyButton.setOnAction(event -> {
            mRedistributionCheck.setSelected(false);
            mRedistributionCheck.setSelected(true);
        });

        BorderPane container = new BorderPane();
        container.setPadding(new Insets(20, 20, 20, 20));
        container.setCenter(centerHBox);
        container.setBottom(bottomHBox);


        Stage configStage = new Stage();
        configStage.setTitle(getBundleText("window.title.redistribution"));
        configStage.getIcons().add(new Image("image/configuration.png"));

        Scene scene = new Scene(container, 1000, 300);
        configStage.setScene(scene);

        mConfigurationButton.setOnAction(event -> {
            configStage.show();
        });

        confirmButton.setOnAction(event -> {
            mRedistributionCheck.setSelected(false);
            mRedistributionCheck.setSelected(true);
            configStage.close();
        });
    }

    private SpanMomentFunction_SpecialLoadCase calculateRedistributionMoment(
            SpanMomentFunction spanMomentFunction,
            Map<Integer, Double> usedRedCoefMap
    ) {
        ELUCombination combination = new ELUCombination(spanMomentFunction);

        Map<Integer, Map<Integer, Double>> supportMomentMap = combination.getSpecialLoadCaseSupportMomentMap();

        Map<Integer, Map<Integer, Double>> supportMomentMap_AD = combination.getSpecialLoadCaseSupportMomentMap();

        for (Map.Entry<Integer, Map<Integer, Double>> entry : supportMomentMap.entrySet()) {
            Map<Integer, Double> newLoadCaseMap = new HashMap<>();
            int supportId = entry.getKey();
            for (Map.Entry<Integer, Double> entry1 : entry.getValue().entrySet()) {
                int loadCase = entry1.getKey();
                double moment = entry1.getValue();
                if (supportId == loadCase - 10) {
                    newLoadCaseMap.put(loadCase, usedRedCoefMap.get(loadCase - 10) * moment);
                } else {
                    newLoadCaseMap.put(loadCase, moment);
                }
            }
            supportMomentMap_AD.put(supportId, newLoadCaseMap);
        }

        SpanMomentFunction_SpecialLoadCase newSpanMomentFunction = new SpanMomentFunction_SpecialLoadCase(supportMomentMap_AD);
        // match the calculate method name to the related spanMomentFunction
        mMethodChoiceMap.put(newSpanMomentFunction.getMethod(), newSpanMomentFunction);
        return newSpanMomentFunction;
    }

    public static Stage getCrossSectionStage() {
        return mCrossSectionStage;
    }
}


