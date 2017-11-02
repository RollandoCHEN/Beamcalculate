package com.beamcalculate.controllers;

import com.beamcalculate.custom.alert.InfoMessage;
import com.beamcalculate.custom.input_manager.InputControllerAdder;
import com.beamcalculate.custom.node.HoveredThresholdNode;
import com.beamcalculate.enums.UltimateCase;
import com.beamcalculate.model.calculate.ELUCombination;
import com.beamcalculate.model.calculate.MomentRedistribution;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.calculate.span_function.AbstractSpanMoment;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
import static com.beamcalculate.model.LanguageManager.getBundleText;
import static com.beamcalculate.model.LanguageManager.getResourceBundle;

/**
 * Created by Ruolin on 01/11/2017 for Beamcalculate.
 */
public class MomentPageController {
    @FXML AnchorPane anchorPane;
    @FXML BorderPane borderPaneContainer;
    @FXML Spinner<Integer> spanNumSpinner;
    @FXML HBox methodsCheckHBox;
    @FXML Label conditionInfoLabel;
    @FXML CheckBox redistributionCheck;
    @FXML Button configurationButton;
    @FXML ChoiceBox<AbstractSpanMoment> methodsChoiceBox;
    @FXML Button rebarCalculateButton;
    @FXML ChoiceBox<Integer> spanChoiceBox;
    @FXML TextField abscissaField;
    @FXML Button momentCalculateButton;
    @FXML Label maxCaseMomentValue;
    @FXML Label minCaseMomentValue;

    private Geometry mGeometry;

    private InputControllerAdder inputControllerAdder = new InputControllerAdder();

    public class MomentLineChart {

        private LineChart<Number, Number> mLineChart;
        private BooleanBinding mDisableSpinnerBoolean;
        private NumberAxis mXAxis = new NumberAxis();
        private NumberAxis mYAxis = new NumberAxis();
        private Map<String, XYChart.Series<Number, Number>> mStringSeriesMap = new HashMap<>();
        private Map<Integer, StringProperty> mEnteredRdsCoef = new HashMap<>();

        public MomentLineChart(SpanMomentFunction spanMomentFunction) {
            final String methodName = spanMomentFunction.getMethod();
            String maxSeriesId = methodName + "_" + getBundleText("label.max");
            String minSeriesId = methodName + "_" + getBundleText("label.min");
            mGeometry = spanMomentFunction.getGeometry();

            addChartsDisplayingCheckBox(methodName, maxSeriesId, minSeriesId);

            createLineChart(spanMomentFunction, maxSeriesId, minSeriesId);

            //Calculating Module : moment calculating and rebar calculating

            addMethodsChoicesForCalculating(spanMomentFunction);

            //Moment Calculating Button setting : disable value and on action
            momentCalculateButton.disableProperty().bind(
                    Bindings.isNull(methodsChoiceBox.valueProperty())
                            .or(Bindings.isNull(spanChoiceBox.valueProperty()))
                            .or(Bindings.isEmpty(abscissaField.textProperty()))
            );
            momentCalculateButton.setOnAction(e -> {
                double maxY, minY;
                int chosenSpan = spanChoiceBox.getValue();
                double enteredXValue = Double.parseDouble(abscissaField.getText());
                AbstractSpanMoment chosenMethod = methodsChoiceBox.getValue();

                if (chosenMethod.equals(TROIS_MOMENT_R.getMethodName())) {
                    SpanMomentFunction_SpecialLoadCase newSpanMoment = (SpanMomentFunction_SpecialLoadCase) chosenMethod;
                    maxY = newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                            enteredXValue, chosenSpan, MAX
                    );
                    minY = newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                            enteredXValue, chosenSpan, MIN
                    );
                } else {
                    ELUCombination eluCombination = new ELUCombination(chosenMethod);
                    maxY = eluCombination.getCombinedUltimateMomentAtXOfSpan(
                            enteredXValue, chosenSpan, MAX
                    );
                    minY = eluCombination.getCombinedUltimateMomentAtXOfSpan(
                            enteredXValue, chosenSpan, MIN
                    );
                }
                maxCaseMomentValue.setText(FOURDECIMALS.getDecimalFormat().format(maxY));
                minCaseMomentValue.setText(FOURDECIMALS.getDecimalFormat().format(minY));
            });

            //Rebar Calculating Button setting : disable value and on action
            rebarCalculateButton.disableProperty().bind(
                    Bindings.isNull(methodsChoiceBox.valueProperty())
                            .or(InputPageController.isDisabledRebarCalculateProperty())
            );
            rebarCalculateButton.setOnAction(event -> {
                AbstractSpanMoment chosenMethod = methodsChoiceBox.getValue();
                Reinforcement reinforcement = new Reinforcement(chosenMethod);
                Rebar rebar = new Rebar(reinforcement);

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RebarCasesPage.fxml"),
                            getResourceBundle());
                    Parent root = fxmlLoader.load();

                    RebarCasesPageController controller = fxmlLoader.getController();

                    controller.setRebar(rebar);
                    controller.generateRebarSelectionCasesTable();

                    int maxNumOfCases = 1;
                    for (int spanId = 1; spanId < Geometry.getNumSpan() + 1; spanId++) {
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
                    if (primaryScreenBounds.getHeight() < rebarSelectionStage.getScene().getHeight()) {
                        rebarSelectionStage.setHeight(primaryScreenBounds.getHeight());
                    }
                    if (primaryScreenBounds.getWidth() < rebarSelectionStage.getScene().getWidth()) {
                        rebarSelectionStage.setHeight(primaryScreenBounds.getWidth());
                    }

                    rebarSelectionStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });



            //if the methodName of calculate is "3 moment", add redistribution for the methodName
            if (methodName.equals(TROIS_MOMENT.getMethodName())
                    && !InputPageController.isDisabledRebarCalculate()
                    ) {
                addRedistribution(spanMomentFunction);
            }


        }

        private void addMethodsChoicesForCalculating(SpanMomentFunction spanMomentFunction) {
            //Method choice
            methodsChoiceBox.setItems(FXCollections.observableArrayList(spanMomentFunction));

            // match the calculate methodName name to the related spanMomentFunction
            spanChoiceBox.setItems(FXCollections.observableArrayList(mGeometry.spansLengthMap().keySet()));
            spanChoiceBox.setOnAction(e -> {
                int selectedSpanId = spanChoiceBox.getValue();
                AbstractSpanMoment chosenMethod = methodsChoiceBox.getValue();
                inputControllerAdder.addMaxValueValidation(abscissaField, chosenMethod.getCalculateSpanLengthMap().get(selectedSpanId));
            });
            spanChoiceBox.disableProperty().bind(Bindings.isNull(methodsChoiceBox.valueProperty()));

            abscissaField.disableProperty().bind(Bindings.isNull(spanChoiceBox.valueProperty()));
        }

        private void addChartsDisplayingCheckBox(String methodName, String maxSeriesId, String minSeriesId) {
            //Set checkbox to show or hide line chart
            CheckBox methodCheck = new CheckBox(methodName);
            methodCheck.setSelected(true);
            methodCheck.selectedProperty().addListener((arg0, oldValue, newValue) -> {
                if (newValue) {
                    mLineChart.getData().addAll(
                            mStringSeriesMap.get(maxSeriesId),
                            mStringSeriesMap.get(minSeriesId)
                    );
                } else {
                    mLineChart.getData().removeAll(
                            mStringSeriesMap.get(maxSeriesId),
                            mStringSeriesMap.get(minSeriesId)
                    );
                }
            });
            methodsCheckHBox.getChildren().clear();                 //Clear the existed check boxes
            methodsCheckHBox.getChildren().add(methodCheck);

            mDisableSpinnerBoolean = Bindings.not(methodCheck.selectedProperty());
            spanNumSpinner.disableProperty().bind(mDisableSpinnerBoolean);

            //Methods applying condition label
            setClickableStyle(conditionInfoLabel);
            conditionInfoLabel.setOnMouseClicked(e -> new InfoMessage(
                    "info.title.methodConditions",
                    "info.head.methodConditions",
                    "info.content.methodConditions"
            ));
        }

        private void createLineChart(SpanMomentFunction spanMomentFunction, String maxSeriesId, String minSeriesId) {
            //defining the axes
            mXAxis = defineAxis(spanMomentFunction).get(0);
            mYAxis = defineAxis(spanMomentFunction).get(1);

            //creating the chart
            mLineChart = new LineChart<>(mXAxis, mYAxis);
            mLineChart.setTitle("");
            mLineChart.setCursor(Cursor.CROSSHAIR);

            //Define series
            XYChart.Series maxELUSeries = new XYChart.Series();
            createMomentSeries(spanNumSpinner.getValue(), spanMomentFunction, MAX, maxELUSeries);
            maxELUSeries.setName(maxSeriesId);

            XYChart.Series minELUSeries = new XYChart.Series();
            createMomentSeries(spanNumSpinner.getValue(), spanMomentFunction, MIN, minELUSeries);
            minELUSeries.setName(minSeriesId);

            //through this mStringSeriesMap to store all the series
            //when add series to the line chart, use also mStringSeriesMap, so when remove series, we can identify the series ??

            mStringSeriesMap.put(maxSeriesId, maxELUSeries);
            mStringSeriesMap.put(minSeriesId, minELUSeries);

            mLineChart.getData().addAll(
                    mStringSeriesMap.get(maxSeriesId),
                    mStringSeriesMap.get(minSeriesId)
            );

            //Set action for the spinner to re-load the moment chart line
            spanNumSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                mStringSeriesMap.get(maxSeriesId).getData().clear();
                mStringSeriesMap.get(minSeriesId).getData().clear();
                createMomentSeries(spanNumSpinner.getValue(), spanMomentFunction, MAX, mStringSeriesMap.get(maxSeriesId));
                createMomentSeries(spanNumSpinner.getValue(), spanMomentFunction, MIN, mStringSeriesMap.get(minSeriesId));
            });

            borderPaneContainer.setCenter(mLineChart);
        }

        public MomentLineChart(SpanMomentFunction... spanMomentFunctions) {
            this(spanMomentFunctions[0]);
            for (int i = 1; i < spanMomentFunctions.length; i++) {
                addNewMomentChart(spanMomentFunctions[i]);
            }
        }

        private void addNewMomentChart(SpanMomentFunction spanMomentFunction) {
            final String methodName = spanMomentFunction.getMethod();
            String maxSeriesId = methodName + "_" + getBundleText("label.max");
            String minSeriesId = methodName + "_" + getBundleText("label.min");
            ELUCombination combination = new ELUCombination(spanMomentFunction);

            // match the calculate method name to the related spanMomentFunction

            //        add new series to line chart

            XYChart.Series newMaxELUSeries = new XYChart.Series();
            createMomentSeries(spanNumSpinner.getValue(), spanMomentFunction, MAX, newMaxELUSeries);
            XYChart.Series newMinELUSeries = new XYChart.Series();
            createMomentSeries(spanNumSpinner.getValue(), spanMomentFunction, MIN, newMinELUSeries);

            mStringSeriesMap.put(maxSeriesId, newMaxELUSeries);
            mStringSeriesMap.put(minSeriesId, newMinELUSeries);
            mLineChart.getData().addAll(
                    mStringSeriesMap.get(maxSeriesId),
                    mStringSeriesMap.get(minSeriesId)
            );

//        bind the spinner listener to the new series

            spanNumSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                newMaxELUSeries.getData().clear();
                newMinELUSeries.getData().clear();
                createMomentSeries(spanNumSpinner.getValue(), spanMomentFunction, MAX, newMaxELUSeries);
                createMomentSeries(spanNumSpinner.getValue(), spanMomentFunction, MIN, newMinELUSeries);
            });

//        check box to show and hide new series

            CheckBox newMethodCheck = new CheckBox(methodName);
            newMethodCheck.setSelected(true);
            methodsCheckHBox.getChildren().addAll(newMethodCheck);

            newMethodCheck.selectedProperty().addListener((arg0, oldValue, newValue) -> {
                if (newValue) {
                    mLineChart.getData().addAll(
                            mStringSeriesMap.get(maxSeriesId),
                            mStringSeriesMap.get(minSeriesId)
                    );
                } else {
                    mLineChart.getData().removeAll(
                            mStringSeriesMap.get(maxSeriesId),
                            mStringSeriesMap.get(minSeriesId)
                    );
                }
            });

            mDisableSpinnerBoolean = mDisableSpinnerBoolean.and(Bindings.not(newMethodCheck.selectedProperty()));
            spanNumSpinner.disableProperty().bind(mDisableSpinnerBoolean);

//        add margin to the y axis

            double maxSpanMomentValue = -Math.max(-mYAxis.getLowerBound(), 1.2 * combination.getUltimateMomentValue(MAX));
            double maxSupportMomentValue = -Math.min(-mYAxis.getUpperBound(), 1.2 * combination.getUltimateMomentValue(MIN));

            mYAxis.lowerBoundProperty().set(maxSpanMomentValue);
            mYAxis.upperBoundProperty().set(maxSupportMomentValue);

            methodsChoiceBox.getItems().add(spanMomentFunction);

            //        if the method of calculate is "3 moment", add redistribution for the method

            if (methodName.equals(TROIS_MOMENT.getMethodName())
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

        public List<NumberAxis> defineAxis(AbstractSpanMoment spanMomentFunction) {
            double maxSpanMomentValue;
            double maxSupportMomentValue;

            if (spanMomentFunction.getMethod().equals(TROIS_MOMENT_R.getMethodName())) {
                SpanMomentFunction_SpecialLoadCase newSpanMomentFunction = (SpanMomentFunction_SpecialLoadCase) spanMomentFunction;
                maxSupportMomentValue = newSpanMomentFunction.getUltimateMomentValue(MIN);
                maxSpanMomentValue = newSpanMomentFunction.getUltimateMomentValue(MAX);
            } else {
                ELUCombination combination = new ELUCombination(spanMomentFunction);
                maxSupportMomentValue = combination.getUltimateMomentValue(MIN);
                maxSpanMomentValue = combination.getUltimateMomentValue(MAX);
            }

            NumberAxis xAxis = new NumberAxis(-1, mGeometry.getTotalLength() + 1, 1);
            NumberAxis yAxis = new NumberAxis(-1.2 * maxSpanMomentValue, -1.2 * maxSupportMomentValue, 0.05);

            xAxis.setLabel(getBundleText("label.abscissa") + " (" + getBundleText("unit.length.m") + ")");
            yAxis.setLabel(getBundleText("label.ordinate") + " (" + getBundleText("unit.moment") + ")");

            List<NumberAxis> axisList = new ArrayList<>();
            axisList.add(xAxis);
            axisList.add(yAxis);

            return axisList;
        }

        public void createMomentSeries(
                int numSection,
                SpanMomentFunction spanMomentFunction, UltimateCase ultimateCase,
                XYChart.Series<Number, Number> series
        ) {
            ELUCombination eluCombination = new ELUCombination(spanMomentFunction);
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

        public void createRedistributionMomentSeries(
                int numSection,
                SpanMomentFunction_SpecialLoadCase spanMomentFunction, UltimateCase ultimateCase,
                XYChart.Series series
        ) {
            spanMomentFunction.getSpanMomentFunctionMap().forEach((spanId, loadCaseMomentFunctionMap) -> {
                double spanLength = mGeometry.getEffectiveSpansLengthMap().get(spanId);
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

        public double getGlobalX(int spanId, double spanLocalX, String method) {
            double globalX = spanLocalX;
            if (TROIS_MOMENT.getMethodName().equals(method)
                    || TROIS_MOMENT_R.getMethodName().equals(method)) {
                for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                    double preX;
                    if (preSpanId == 0) {
                        preX = Geometry.supportWidthMap().get(1) / 2;
                    } else {
                        preX = mGeometry.getEffectiveSpansLengthMap().get(preSpanId);
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
                        preSpanLength = mGeometry.spansLengthMap().get(preSpanId);
                        preSupportLength = Geometry.supportWidthMap().get(preSpanId + 1);
                    }
                    globalX += (preSpanLength + preSupportLength);
                }
            }
            return globalX;
        }

        // TODO Simplify this method by removing spanId parameter
        public double getSpanLocalX(int spanId, double globalX, String method) {
            double spanLocalX = globalX;
            if (TROIS_MOMENT.getMethodName().equals(method)) {
                for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                    double preX;
                    if (preSpanId == 0) {
                        preX = Geometry.supportWidthMap().get(1) / 2;
                    } else {
                        preX = mGeometry.getEffectiveSpansLengthMap().get(preSpanId);
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
                        preSpanLength = mGeometry.spansLengthMap().get(preSpanId);
                        preSupportLength = Geometry.supportWidthMap().get(preSpanId + 1);
                    }
                    spanLocalX -= (preSpanLength + preSupportLength);
                }
            }
            return spanLocalX;
        }


        private void addRedistribution(SpanMomentFunction spanMomentFunction) {
            String maxSeriesId = TROIS_MOMENT.getMethodName() + "_ReducedMAX";
            String minSeriesId = TROIS_MOMENT.getMethodName() + "_ReducedMIN";

            mDisableSpinnerBoolean = mDisableSpinnerBoolean.and(Bindings.not(redistributionCheck.selectedProperty()));
            spanNumSpinner.disableProperty().bind(mDisableSpinnerBoolean);

            ELUCombination combination = new ELUCombination(spanMomentFunction);
            MomentRedistribution momentRedistribution = new MomentRedistribution(combination);

            Map<Integer, Double> calculatedFinalRedCoefMap = momentRedistribution.getFinalRedistributionCoefMap();
            Map<Integer, Double> calculatedRedCoefMap = momentRedistribution.getRedistributionCoefMap();

            Map<Integer, Double> usedRedCoefMap = new HashMap<>();
            calculatedFinalRedCoefMap.forEach(usedRedCoefMap::put);

            calculateRedistributionMoment(spanMomentFunction, usedRedCoefMap);

            //TODO This is not the correct way to add method to the method choice box
            methodsChoiceBox.getItems().add(spanMomentFunction);

            redistributionCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
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

                    createRedistributionMomentSeries(spanNumSpinner.getValue(), newSpanMomentFunction, MAX, maxELUSeries);

                    createRedistributionMomentSeries(spanNumSpinner.getValue(), newSpanMomentFunction, MIN, minELUSeries);

                    spanNumSpinner.valueProperty().addListener((observable1, oldValue1, newValue1) -> {
                        maxELUSeries.getData().clear();
                        minELUSeries.getData().clear();
                        createRedistributionMomentSeries(spanNumSpinner.getValue(), newSpanMomentFunction, MAX, maxELUSeries);
                        createRedistributionMomentSeries(spanNumSpinner.getValue(), newSpanMomentFunction, MIN, minELUSeries);
                    });


                    mStringSeriesMap.put(maxSeriesId, maxELUSeries);
                    mStringSeriesMap.put(minSeriesId, minELUSeries);

                    mLineChart.getData().addAll(
                            mStringSeriesMap.get(maxSeriesId),
                            mStringSeriesMap.get(minSeriesId)
                    );
                } else {
                    mLineChart.getData().removeAll(
                            mStringSeriesMap.get(maxSeriesId),
                            mStringSeriesMap.get(minSeriesId)
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
                inputControllerAdder.addRealNumberControllerTo(coefValue);
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
                redistributionCheck.setSelected(false);
                redistributionCheck.setSelected(true);
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

            configurationButton.setOnAction(event -> {
                configStage.show();
            });

            confirmButton.setOnAction(event -> {
                redistributionCheck.setSelected(false);
                redistributionCheck.setSelected(true);
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

            SpanMomentFunction_SpecialLoadCase newSpanMomentFunction = new SpanMomentFunction_SpecialLoadCase(supportMomentMap_AD, mGeometry);
            // match the calculate method name to the related spanMomentFunction
            return newSpanMomentFunction;
        }
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void createMomentLineChart(SpanMomentFunction... spanMomentFunctions){
        new MomentLineChart(spanMomentFunctions);
    }
}
