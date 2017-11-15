package com.beamcalculate.controllers;

import com.beamcalculate.model.custom_alert.InfoMessage;
import com.beamcalculate.model.custom_node.LineChartWithMarkers;
import com.beamcalculate.model.page_manager.InputControllerAdder;
import com.beamcalculate.model.calculate.ELUCombination;
import com.beamcalculate.model.calculate.MomentRedistribution;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.calculate.span_function.AbstractSpanMoment;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Inputs;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT;
import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.NumericalFormat.*;
import static com.beamcalculate.enums.UltimateCase.MAX;
import static com.beamcalculate.enums.UltimateCase.MIN;
import static com.beamcalculate.model.MyMethods.round;
import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;
import static com.beamcalculate.model.page_manager.MomentLineChartTreater.*;

/**
 * Created by Ruolin on 01/11/2017 for Beamcalculate.
 */
public class MomentPageController {
    @FXML AnchorPane momentPageAnchorPane;
    @FXML BorderPane borderPaneContainer;
    @FXML Spinner<Integer> totalNumOnSpanSpinner;
    @FXML HBox methodsCheckHBox;
    @FXML Label conditionInfoLabel;
    @FXML JFXCheckBox redistributionCheck;
    @FXML Button configurationButton;
    @FXML JFXComboBox<AbstractSpanMoment> methodsChoiceBox;
    @FXML Button rebarCalculateButton;
    @FXML JFXComboBox<Integer> spanChoiceBox;
    @FXML Label abscissaLimit;
    @FXML HBox abscissaFieldHBox;
//    @FXML Button momentCalculateButton;

    @FXML JFXSlider mySlider;

    private Inputs mInputs;
    private Geometry mGeometry;

    private InputControllerAdder inputControllerAdder = new InputControllerAdder();

    private MainAccessController mMainAccessController;

    private BooleanProperty mShowRebarPage = new SimpleBooleanProperty(false);

    public class MomentPageCreator {
        private LineChartWithMarkers<Number, Number> mLineChart;
        private BooleanBinding mDisableSpinnerBoolean = null;
        private NumberAxis mXAxis = new NumberAxis();
        private NumberAxis mYAxis = new NumberAxis();
        private Map<String, XYChart.Series<Number, Number>> mStringSeriesMap = new HashMap<>();
        private Map<Integer, StringProperty> mEnteredRdsCoef = new HashMap<>();

        public MomentPageCreator(SpanMomentFunction spanMomentFunction) {
            final String methodName = spanMomentFunction.getMethod();
            mInputs = spanMomentFunction.getInputs();
            mGeometry = mInputs.getGeometry();

            mySlider.setMin(0);
            mySlider.setMax(spanMomentFunction.getInputs().getGeometry().getTotalLength());
            mySlider.setValue(mySlider.getMin());
            mySlider.setValueFactory(slider ->
                    Bindings.createStringBinding(
                            () -> (TWO_DECIMALS.format(slider.getValue())) + "",
                            slider.valueProperty()
                    )
            );
            mySlider.disableProperty().bind(spanChoiceBox.disableProperty());



            //define axes
            mXAxis = defineAxis(spanMomentFunction).get(0);
            mYAxis = defineAxis(spanMomentFunction).get(1);

            //create line chart
            mLineChart = new LineChartWithMarkers<>(mXAxis, mYAxis);
            mLineChart.setTitle("");
            mLineChart.setCursor(Cursor.CROSSHAIR);
            borderPaneContainer.setCenter(mLineChart);

            //Methods applying condition label
            conditionInfoLabel.setOnMouseEntered(e -> conditionInfoLabel.getStyleClass().add("clickable-mouse-enter"));
            conditionInfoLabel.setOnMouseExited(e -> conditionInfoLabel.getStyleClass().remove("clickable-mouse-enter"));
            conditionInfoLabel.setOnMouseClicked(e -> new InfoMessage(
                    "info.title.methodConditions",
                    "info.head.methodConditions",
                    "info.content.methodConditions"
            ));

            methodsCheckHBox.getChildren().clear();                 //Clear the existed check boxes
            prepareMomentSeriesAndAddToLineChart(spanMomentFunction);

            //Calculating Module : moment calculating and rebar calculating

            addMethodsChoicesForCalculating(spanMomentFunction);

            //Moment Calculating Button setting : disable value and on action
            abscissaLimit.setText("(0 ~ 0)");
            abscissaFieldHBox.getChildren().clear();
            JFXTextField textField = new JFXTextField();
            textField.setDisable(true);
            textField.setPromptText(getBundleText("label.xOnSpan"));
            abscissaFieldHBox.getChildren().add(textField);

//            momentCalculateButton.disableProperty().bind(
//                    Bindings.isNull(methodsChoiceBox.valueProperty())
//                            .or(Bindings.isNull(spanChoiceBox.valueProperty()))
//                            .or(Bindings.isEmpty(textField.textProperty()))
//            );

//            momentCalculateButton.setOnAction(e -> {
//                double maxY, minY;
//                AbstractSpanMoment chosenMethod = methodsChoiceBox.getValue();
//                int chosenSpan = getSpanId(mySlider.getValue(), chosenMethod);
//                double enteredXValue = getSpanLocalX(mySlider.getValue(), chosenMethod);
//
//                if (chosenMethod.getMethod().equals(TROIS_MOMENT_R.getMethodName())) {
//                    SpanMomentFunction_SpecialLoadCase newSpanMoment = (SpanMomentFunction_SpecialLoadCase) chosenMethod;
//                    maxY = newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
//                            enteredXValue, chosenSpan, MAX
//                    );
//                    minY = newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
//                            enteredXValue, chosenSpan, MIN
//                    );
//                } else {
//                    ELUCombination eluCombination = new ELUCombination(chosenMethod);
//                    maxY = eluCombination.getCombinedUltimateMomentAtXOfSpan(
//                            enteredXValue, chosenSpan, MAX
//                    );
//                    minY = eluCombination.getCombinedUltimateMomentAtXOfSpan(
//                            enteredXValue, chosenSpan, MIN
//                    );
//                }
//                maxCaseMomentValue.setText(FOUR_DECIMALS.format(maxY));
//                minCaseMomentValue.setText(FOUR_DECIMALS.format(minY));
//            });

            //Rebar Calculating Button setting : disable value and on action
            rebarCalculateButton.disableProperty().bind(Bindings.isNull(methodsChoiceBox.valueProperty()));
            rebarCalculateButton.visibleProperty().bind(Bindings.not(InputPageController.isDisabledRebarCalculateProperty()));
            rebarCalculateButton.setOnAction(event -> {
                AbstractSpanMoment chosenMethod = methodsChoiceBox.getValue();
                Reinforcement reinforcement = new Reinforcement(chosenMethod);
                Rebar rebar = new Rebar(reinforcement);
                mMainAccessController.generateRebarSelectionCasesTable(rebar);

                mShowRebarPage.setValue(true);
                mMainAccessController.getRebarCasesPageButton().setSelected(true);
            });

            redistributionCheck.visibleProperty().bind(Bindings.not(InputPageController.isDisabledRebarCalculateProperty()));
            configurationButton.visibleProperty().bind(Bindings.not(InputPageController.isDisabledRebarCalculateProperty()));
            //if the methodName of calculate is "3 moment", add redistribution for the methodName
            if (spanMomentFunction.getInputs().getGeometry().getNumSpan() > 1
                    && methodName.equals(TROIS_MOMENT.getMethodName())
                    && !InputPageController.isDisabledRebarCalculate()
                    ) {
                addRedistributionOption(spanMomentFunction);
            }
        }

        public MomentPageCreator(SpanMomentFunction... spanMomentFunctions) {
            this(spanMomentFunctions[0]);
            for (int i = 1; i < spanMomentFunctions.length; i++) {
                addNewMomentChart(spanMomentFunctions[i]);
            }
        }

        private void addMethodsChoicesForCalculating(SpanMomentFunction spanMomentFunction) {
            //Method choice
            methodsChoiceBox.setItems(FXCollections.observableArrayList(spanMomentFunction));

            XYChart.Data<Number, Number> verticalMarker = new XYChart.Data<>(mySlider.getMax()/2, 0);
            methodsChoiceBox.valueProperty().addListener((observable -> {
                spanChoiceBox.getSelectionModel().clearSelection();
                abscissaLimit.setText("(0 ~ 0)");

                if (!methodsChoiceBox.getSelectionModel().isEmpty()){
                    AbstractSpanMoment chosenMethod = methodsChoiceBox.getValue();
                    Label maxCaseMomentLabel = new Label(
                            getBundleText("label.maxMoment") +
                            " (" + getBundleText("unit.moment") + ") : "
                    );
                    Label maxCaseMomentValue = new Label();
                    Label minCaseMomentLabel = new Label(
                            getBundleText("label.minMoment") +
                                    " (" + getBundleText("unit.moment") + ") : "
                    );
                    Label minCaseMomentValue = new Label();

                    if (TROIS_MOMENT_R.getMethodName().equals(chosenMethod.getMethod())) {
                        SpanMomentFunction_SpecialLoadCase newSpanMoment = (SpanMomentFunction_SpecialLoadCase) chosenMethod;
                        maxCaseMomentValue.textProperty().bind(
                                Bindings.createStringBinding(
                                        () -> FOUR_DECIMALS.format(
                                                newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                                                        getSpanLocalX(mySlider.valueProperty().get(), chosenMethod),
                                                        getSpanId(mySlider.valueProperty().get(), chosenMethod),
                                                        MAX)
                                        ) + "",
                                        mySlider.valueProperty()
                                )
                        );
                        minCaseMomentValue.textProperty().bind(
                                Bindings.createStringBinding(
                                        () -> FOUR_DECIMALS.format(
                                                newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                                                        getSpanLocalX(mySlider.valueProperty().get(), chosenMethod),
                                                        getSpanId(mySlider.valueProperty().get(), chosenMethod),
                                                        MIN
                                                )
                                        ) + "",
                                        mySlider.valueProperty()
                                )
                        );
                    } else {
                        ELUCombination eluCombination = new ELUCombination(chosenMethod);
                        maxCaseMomentValue.textProperty().bind(
                                Bindings.createStringBinding(
                                        () -> FOUR_DECIMALS.format(
                                                eluCombination.getCombinedUltimateMomentAtXOfSpan(
                                                        getSpanLocalX(mySlider.valueProperty().get(), chosenMethod),
                                                        getSpanId(mySlider.valueProperty().get(), chosenMethod),
                                                        MAX
                                                )
                                        ) + "",
                                        mySlider.valueProperty()
                                )
                        );
                        minCaseMomentValue.textProperty().bind(
                                Bindings.createStringBinding(
                                        () -> FOUR_DECIMALS.format(
                                                eluCombination.getCombinedUltimateMomentAtXOfSpan(
                                                        getSpanLocalX(mySlider.valueProperty().get(), chosenMethod),
                                                        getSpanId(mySlider.valueProperty().get(), chosenMethod),
                                                        MIN
                                                )
                                        ) + "",
                                        mySlider.valueProperty()
                                )
                        );
                    }

                    HBox topHBox = new HBox(minCaseMomentLabel, minCaseMomentValue);
                    topHBox.getStyleClass().add("max_value_hbox");
                    HBox bottomHBox = new HBox(maxCaseMomentLabel, maxCaseMomentValue);
                    bottomHBox.getStyleClass().add("max_value_hbox");

                    mLineChart.removeVerticalValueMarker(verticalMarker);
                    mLineChart.addVerticalValueMarker(verticalMarker, topHBox, bottomHBox);
                    mySlider.valueProperty().bindBidirectional(verticalMarker.XValueProperty());

                    mySlider.valueProperty().addListener(new ChangeListener<Number>() {
                        private boolean changing;

                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            if (!changing) {
                                try {
                                    changing = true;
                                    spanChoiceBox.setValue(getSpanId(newValue.doubleValue(), methodsChoiceBox.getValue()));
                                } finally {
                                    changing = false;
                                }
                            }
                        }
                    });
                }
            }));

            // match the calculate methodName name to the related spanMomentFunction
            spanChoiceBox.setItems(FXCollections.observableArrayList(mGeometry.spansLengthMap().keySet()));
            spanChoiceBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
                if (newValue != null && newValue != 0) {
                    int selectedSpanId = spanChoiceBox.getValue();
                    AbstractSpanMoment chosenMethod = methodsChoiceBox.getValue();
                    JFXTextField abscissaField = new JFXTextField();
                    abscissaField.setPromptText(getBundleText("label.xOnSpan"));
                    abscissaFieldHBox.getChildren().clear();
//                    inputControllerAdder.addMaxValueValidation(abscissaField, round(chosenMethod.getCalculateSpanLengthMap().get(selectedSpanId),2), true);
                    abscissaFieldHBox.getChildren().add(abscissaField);
                    abscissaField.disableProperty().bind(Bindings.isNull(spanChoiceBox.valueProperty()));

                    mySlider.valueProperty().addListener(new ChangeListener<Number>() {
                        private boolean changing;
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            if( !changing ) {
                                try {
                                    changing = true;
                                    spanChoiceBox.setValue(getSpanId(newValue.doubleValue(), methodsChoiceBox.getValue()));
                                    TextField textField = (TextField) abscissaFieldHBox.getChildren().get(0);
                                    textField.setText(TWO_DECIMALS.format(getSpanLocalX(newValue.doubleValue(), chosenMethod)));
                                }
                                finally {
                                    changing = false;
                                }
                            }
                        }
                    });

                    abscissaField.focusedProperty().addListener((observable2, oldValue2, newValue2) -> {
                        if(!newValue2) {
                            inputControllerAdder.addPatternMatchTo(abscissaField, true);
                            if (abscissaField.getText().isEmpty()){
                                mySlider.setValue(getGlobalX(spanChoiceBox.getValue(), 0, chosenMethod));
                            }else {
                                double maxX = round(chosenMethod.getCalculateSpanLengthMap().get(selectedSpanId),2);
                                if (Double.parseDouble(abscissaField.getText()) > maxX) {       //entered value > max limit
                                    abscissaField.setText(String.valueOf(maxX));                                          //remove the value
                                    mySlider.setValue(getGlobalX(spanChoiceBox.getValue(), maxX, chosenMethod));
                                } else {
                                    mySlider.setValue(getGlobalX(spanChoiceBox.getValue(), Double.parseDouble(abscissaField.getText()), chosenMethod));
                                }
                            }
                        }
                    });

//                    abscissaField.setOnKeyPressed(keyEvent -> {
//                        if (keyEvent.getCode() == KeyCode.ENTER) {
//                            inputControllerAdder.addPatternMatchTo(abscissaField, true);
//                            if (abscissaField.getText().isEmpty()){
//                                mySlider.setValue(getGlobalX(spanChoiceBox.getValue(), 0, chosenMethod));
//                            }else {
//                                double maxX = round(chosenMethod.getCalculateSpanLengthMap().get(selectedSpanId),2);
//                                if (Double.parseDouble(abscissaField.getText()) > maxX) {       //entered value > max limit
//                                    abscissaField.setText(String.valueOf(maxX));                                          //remove the value
//                                    mySlider.setValue(getGlobalX(spanChoiceBox.getValue(), maxX, chosenMethod));
//                                } else {
//                                    mySlider.setValue(getGlobalX(spanChoiceBox.getValue(), Double.parseDouble(abscissaField.getText()), chosenMethod));
//                                }
//                            }
//                        }
//                    });

//                    momentCalculateButton.disableProperty().bind(
//                            Bindings.isNull(methodsChoiceBox.valueProperty())
//                                    .or(Bindings.isNull(spanChoiceBox.valueProperty()))
//                                    .or(Bindings.isEmpty(abscissaField.textProperty()))
//                    );
                    abscissaLimit.setText("(0 ~ "
                            + TWO_DECIMALS.format(chosenMethod.getCalculateSpanLengthMap().get(selectedSpanId))
                            + ")");
                }
            }));
            spanChoiceBox.disableProperty().bind(Bindings.isNull(methodsChoiceBox.valueProperty()));

        }

        private void prepareMomentSeriesAndAddToLineChart(SpanMomentFunction spanMomentFunction) {
            String methodName = spanMomentFunction.getMethod();
            String maxSeriesId = methodName + " - " + getBundleText("label.max");
            String minSeriesId = methodName + " - " + getBundleText("label.min");
            //Set checkbox to show or hide line chart
            JFXCheckBox methodCheck = new JFXCheckBox(methodName);
            methodCheck.selectedProperty().addListener((arg0, oldValue, newValue) -> {
                if (newValue) {
                    //Define series
                    LineChart.Series<Number, Number> maxELUSeries = new LineChart.Series<>();
                    addDataToMomentSeries(totalNumOnSpanSpinner.getValue(), spanMomentFunction, MAX, maxELUSeries);

                    LineChart.Series<Number, Number> minELUSeries = new LineChart.Series<>();
                    addDataToMomentSeries(totalNumOnSpanSpinner.getValue(), spanMomentFunction, MIN, minELUSeries);

                    //through this mStringSeriesMap to store all the series
                    //when add series to the line chart, use also mStringSeriesMap, so when remove series, we can identify the series ??

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
            methodCheck.setSelected(true);
            //Set action for the spinner to re-load the moment chart line
            totalNumOnSpanSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                mStringSeriesMap.get(maxSeriesId).getData().clear();
                mStringSeriesMap.get(minSeriesId).getData().clear();
                addDataToMomentSeries(totalNumOnSpanSpinner.getValue(), spanMomentFunction, MAX, mStringSeriesMap.get(maxSeriesId));
                addDataToMomentSeries(totalNumOnSpanSpinner.getValue(), spanMomentFunction, MIN, mStringSeriesMap.get(minSeriesId));
            });

            methodsCheckHBox.getChildren().add(methodCheck);

            if (mDisableSpinnerBoolean == null){
                mDisableSpinnerBoolean = Bindings.not(methodCheck.selectedProperty());
            }else {
                mDisableSpinnerBoolean = mDisableSpinnerBoolean.and(Bindings.not(methodCheck.selectedProperty()));
            }
            totalNumOnSpanSpinner.disableProperty().bind(mDisableSpinnerBoolean);


        }

        private void addNewMomentChart(SpanMomentFunction spanMomentFunction) {
            prepareMomentSeriesAndAddToLineChart(spanMomentFunction);

            //add margin to the y axis
            ELUCombination combination = new ELUCombination(spanMomentFunction);
            double maxSpanMomentValue = -Math.max(-mYAxis.getLowerBound(), 1.2 * combination.getUltimateMomentValue(MAX));
            double maxSupportMomentValue = -Math.min(-mYAxis.getUpperBound(), 1.2 * combination.getUltimateMomentValue(MIN));

            mYAxis.lowerBoundProperty().set(maxSpanMomentValue);
            mYAxis.upperBoundProperty().set(maxSupportMomentValue);

            methodsChoiceBox.getItems().add(spanMomentFunction);

            //if the method of calculate is "3 moment", add redistribution for the method
            if (spanMomentFunction.getInputs().getGeometry().getNumSpan() > 1
                    && spanMomentFunction.getMethod().equals(TROIS_MOMENT.getMethodName())
                    && !InputPageController.isDisabledRebarCalculate()
                    ) {
                addRedistributionOption(spanMomentFunction);
            }
        }

        private void addRedistributionOption(SpanMomentFunction spanMomentFunction) {
            String maxSeriesId = TROIS_MOMENT.getMethodName() + "_ReducedMAX";
            String minSeriesId = TROIS_MOMENT.getMethodName() + "_ReducedMIN";

            mDisableSpinnerBoolean = mDisableSpinnerBoolean.and(Bindings.not(redistributionCheck.selectedProperty()));
            totalNumOnSpanSpinner.disableProperty().bind(mDisableSpinnerBoolean);

            MomentRedistribution momentRedistribution = new MomentRedistribution(spanMomentFunction);

            Map<Integer, Double> calculatedFinalRedCoefMap = momentRedistribution.getFinalRedistributionCoefMap();
            Map<Integer, Double> calculatedRedCoefMap = momentRedistribution.getRedistributionCoefMap();

            Map<Integer, Double> usedRedCoefMap = new HashMap<>();
            calculatedFinalRedCoefMap.forEach(usedRedCoefMap::put);

            calculateRedistributionMoment(spanMomentFunction, usedRedCoefMap);

            methodsChoiceBox.getItems().add(calculateRedistributionMoment(spanMomentFunction, usedRedCoefMap));

            redistributionCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                XYChart.Series<Number, Number> maxELUSeries = new XYChart.Series<>();
                XYChart.Series<Number, Number> minELUSeries = new XYChart.Series<>();
                if (newValue) {
                    for (int i = 1; i < mGeometry.getNumSupport(); i++) {
                        try {
                            usedRedCoefMap.put(i, Double.parseDouble(mEnteredRdsCoef.get(i).get()));
                        } catch (Exception exp) {
                            usedRedCoefMap.put(i, calculatedFinalRedCoefMap.get(i));
                        }
                    }

                    SpanMomentFunction_SpecialLoadCase newSpanMomentFunction = calculateRedistributionMoment(spanMomentFunction, usedRedCoefMap);

                    addDataToRedistributionMomentSeries(
                            totalNumOnSpanSpinner.getValue(), newSpanMomentFunction, MAX, maxELUSeries
                    );

                    addDataToRedistributionMomentSeries(
                            totalNumOnSpanSpinner.getValue(), newSpanMomentFunction, MIN, minELUSeries
                    );

                    totalNumOnSpanSpinner.valueProperty().addListener((observable1, oldValue1, newValue1) -> {
                        maxELUSeries.getData().clear();
                        minELUSeries.getData().clear();
                        addDataToRedistributionMomentSeries(
                                totalNumOnSpanSpinner.getValue(), newSpanMomentFunction, MAX, maxELUSeries
                        );
                        addDataToRedistributionMomentSeries(
                                totalNumOnSpanSpinner.getValue(), newSpanMomentFunction, MIN, minELUSeries
                        );
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

            HBox centerHBox = new HBox();
            centerHBox.setSpacing(20);
            centerHBox.setAlignment(Pos.CENTER);

            VBox paramNameVBox = new VBox();
            paramNameVBox.setSpacing(15);
            paramNameVBox.setAlignment(Pos.CENTER_LEFT);
            Label blank = new Label("");
            Label rdsCoef = new Label(getBundleText("label.theoRdsCoef"));
            Label minRdsCoef = new Label(getBundleText("label.minRdsCoef"));
            Label finalRdsCoef = new Label(getBundleText("label.finalRdsCoef"));
            blank.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
            paramNameVBox.getChildren().addAll(blank, rdsCoef, minRdsCoef, finalRdsCoef);
            centerHBox.getChildren().add(paramNameVBox);

            calculatedRedCoefMap.forEach((supportId, coef) -> {
                VBox supportParamValueVBox = new VBox();
                supportParamValueVBox.setSpacing(15);
                supportParamValueVBox.setAlignment(Pos.CENTER);
                Label sectionLabel = new Label(getBundleText("label.support") + " " + supportId.toString());
                sectionLabel.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
                Label rdsCoefValue = new Label(
                        THREE_DECIMALS.format(coef)
                );
                Label finalCoefValue = new Label(
                        THREE_DECIMALS.format(calculatedFinalRedCoefMap.get(supportId))
                );

                TextField coefValue = new TextField();
                coefValue.setPrefWidth(65);
                coefValue.textProperty().setValue(
                        THREE_DECIMALS.format(calculatedFinalRedCoefMap.get(supportId))
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
                centerHBox.getChildren().add(supportParamValueVBox);
            });

            HBox bottomHBox = new HBox();
            bottomHBox.setSpacing(10);
            bottomHBox.setAlignment(Pos.CENTER_RIGHT);
            Button confirmButton = new Button(getBundleText("button.ok"));
            Button applyButton = new Button(getBundleText("button.apply"));
            bottomHBox.getChildren().addAll(applyButton, confirmButton);
            applyButton.setOnAction(event -> {
                redistributionCheck.setSelected(false);
                redistributionCheck.setSelected(true);
            });

            BorderPane container = new BorderPane();
            container.setPadding(new Insets(0, 20, 20, 20));
            container.setCenter(centerHBox);
            container.setBottom(bottomHBox);

            Stage configStage = new Stage();
            configStage.setTitle(getBundleText("window.title.redistribution"));
            configStage.getIcons().add(new Image("image/setting-icon_256x256.png"));
            configStage.setAlwaysOnTop(true);
            configStage.setResizable(false);
            configStage.initModality(Modality.WINDOW_MODAL);
            configStage.initOwner(configurationButton.getScene().getWindow());

            int numOfSupport = mGeometry.getNumSupport();
            Scene scene = new Scene(container, numOfSupport * 90 + 320, 280);
            configStage.setScene(scene);

            configurationButton.setOnAction(event -> configStage.show());

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

            // match the calculate method name to the related spanMomentFunction
            return new SpanMomentFunction_SpecialLoadCase(supportMomentMap_AD, mInputs);
        }
    }

    public AnchorPane getAnchorPane() {
        return momentPageAnchorPane;
    }

    public void createMomentPage(SpanMomentFunction... spanMomentFunctions){
        new MomentPageCreator(spanMomentFunctions);
    }

    public void injectMainController(MainAccessController mainAccessController) {
        mMainAccessController = mainAccessController;
    }

    public boolean showRebarPage() {
        return mShowRebarPage.get();
    }

    public BooleanProperty showRebarPageProperty() {
        return mShowRebarPage;
    }
}
