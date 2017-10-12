package com.beamcalculate.model.result;

import com.beamcalculate.Main;
import com.beamcalculate.controllers.MainController;
import com.beamcalculate.controllers.TSectionController;
import com.beamcalculate.model.calculate.ELUCombination;
import com.beamcalculate.model.calculate.MomentRedistribution;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.calculate.span.AbstractSpanMoment;
import com.beamcalculate.model.calculate.span.SpanMomentFunction;
import com.beamcalculate.model.calculate.span.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.enums.UltimateCase;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT;
import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.NumericalFormat.FOURDECIMALS;
import static com.beamcalculate.enums.NumericalFormat.THREEDECIMALS;
import static com.beamcalculate.enums.UltimateCase.MAX;
import static com.beamcalculate.enums.UltimateCase.MIN;

public class MomentLineChart {

    private Spinner<Integer> mIntegerSpinner;
    private final LineChart<Number, Number> mLineChart;
    private HBox mHBoxMethod;
    private BooleanBinding mDisableSpinner;
    private DoubleProperty maxMomentValue = new SimpleDoubleProperty();
    private DoubleProperty minMomentValue = new SimpleDoubleProperty();
    private StringProperty mMethodChoiceValue = new SimpleStringProperty();
    private static NumberAxis xAxis = new NumberAxis();
    private static NumberAxis yAxis = new NumberAxis();
    private ChoiceBox<String> mMethodChoice;
    private Map<String, AbstractSpanMoment> mMethodChoiceMap = new HashMap<>();
    private GridPane mGridPaneTop;
    private static Map<String, XYChart.Series> mStringSeriesMap = new HashMap<>();
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

    public MomentLineChart(SpanMomentFunction spanMomentFunction) {
        ELUCombination combination = new ELUCombination(spanMomentFunction);

        // match the calculate method name to the related spanMomentFunction
        mMethodChoiceMap.put(spanMomentFunction.getMethod(), spanMomentFunction);


        Label intSpinnerLabel = new Label(Main.getBundleText("label.numberOfSections"));
        mIntegerSpinner = new Spinner<>(1, 30, 10, 1);
        mIntegerSpinner.setPrefWidth(70);
        mIntegerSpinner.setEditable(true);


        HBox hBoxSpinner = new HBox(intSpinnerLabel, mIntegerSpinner);
        hBoxSpinner.setSpacing(15);
        hBoxSpinner.setAlignment(Pos.CENTER_LEFT);

//        defining the axes

        maxMomentValue.set(-combination.getUltimateMomentValue(MAX));
        minMomentValue.set(-combination.getUltimateMomentValue(MIN));

        xAxis = new NumberAxis(-1, Geometry.getTotalLength() + 1, 1);
        yAxis = new NumberAxis(1.2 * maxMomentValue.get(), 1.2 * minMomentValue.get(), 0.05);

        xAxis.setLabel(Main.getBundleText("label.abscissa") + " (" + Main.getBundleText("unit.length.m") + ")");
        yAxis.setLabel(Main.getBundleText("label.ordinate") + " (" + Main.getBundleText("unit.moment") + ")");

//        creating the chart

        mLineChart = new LineChart<>(xAxis, yAxis);

        mLineChart.setTitle("");
        mLineChart.setCursor(Cursor.CROSSHAIR);

//        define series

        XYChart.Series maxELUSeries = new XYChart.Series();
        createMomentSeries(mIntegerSpinner.getValue(), combination, MAX, maxELUSeries);
        maxELUSeries.setName(Main.getBundleText("label.max") + " - " + spanMomentFunction.getMethod());

        XYChart.Series minELUSeries = new XYChart.Series();
        createMomentSeries(mIntegerSpinner.getValue(), combination, MIN, minELUSeries);
        minELUSeries.setName(Main.getBundleText("label.min") + " - " + spanMomentFunction.getMethod());

//        through this mStringSeriesMap to store all the series
//        when add series to the line chart, use also mStringSeriesMap, so when remove series, we can identify the series ??

        mStringSeriesMap.put(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.max"), maxELUSeries);
        mStringSeriesMap.put(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.min"), minELUSeries);

        mLineChart.getData().addAll(
                mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.max")),
                mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.min"))
        );

        mIntegerSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxELUSeries.getData().clear();
            minELUSeries.getData().clear();
            createMomentSeries(mIntegerSpinner.getValue(), combination, MAX, maxELUSeries);
            createMomentSeries(mIntegerSpinner.getValue(), combination, MIN, minELUSeries);
        });

//        module to calculate the Y value for the given X value

        Label methodText = new Label(Main.getBundleText("label.momentCalculateMethod"));
        mMethodChoice = new ChoiceBox(FXCollections.observableArrayList(spanMomentFunction.getMethod()));
        mMethodChoice.setPrefWidth(200);
        Label spanNumText = new Label(Main.getBundleText("label.spanNumb"));
        ChoiceBox<Integer> spanNumChoice = new ChoiceBox(FXCollections.observableArrayList(Geometry.spansLengthMap().keySet()));
        Label xAbscissaText = new Label(Main.getBundleText("label.xOnSpan"));
        TextField xValueField = new TextField();
        Label lengthUnitText = new Label(Main.getBundleText("unit.length.m"));
        Button calculateYButton = new Button(Main.getBundleText("button.calculateMoment"));
        Label maxCaseMomentLabel = new Label(
                Main.getBundleText("label.maxMoment")
                        + " ("
                        + Main.getBundleText("unit.moment")
                        + ") : "
        );
        Label maxCaseMomentValue = new Label("0.0000");
        Label minCaseMomentLabel = new Label(Main.getBundleText("label.minMoment")
                + " ("
                + Main.getBundleText("unit.moment")
                + ") : ");
        Label minCaseMomentValue = new Label("0.0000");

        mMethodChoiceValue.bind(mMethodChoice.valueProperty());

        calculateYButton.setDisable(true);
        calculateYButton.disableProperty().bind(
                Bindings.isNull(mMethodChoice.valueProperty())
                        .or(Bindings.isNull(spanNumChoice.valueProperty()))
                        .or(Bindings.isEmpty(xValueField.textProperty()))
        );


        spanNumChoice.setOnAction(e -> {
            int selectedSpanId = spanNumChoice.getValue();
            addMaxValueValidation(xValueField, mMethodChoiceMap.get(mMethodChoiceValue.get()).getCalculateSpanLengthMap().get(selectedSpanId));
        });
        spanNumChoice.disableProperty().bind(Bindings.isNull(mMethodChoice.valueProperty()));

        xValueField.setMaxWidth(50);
        xValueField.disableProperty().bind(Bindings.isNull(spanNumChoice.valueProperty()));


        calculateYButton.setOnAction(e -> {
            double maxY, minY;
            if (mMethodChoiceValue.get().equals(TROIS_MOMENT_R.getBundleText())){
                SpanMomentFunction_SpecialLoadCase newSpanMoment = (SpanMomentFunction_SpecialLoadCase)mMethodChoiceMap.get(mMethodChoiceValue.get());
                maxY = newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                        Double.parseDouble(xValueField.getText()), spanNumChoice.getValue(), MAX
                );
                minY = newSpanMoment.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                        Double.parseDouble(xValueField.getText()), spanNumChoice.getValue(), MIN
                );
            }else{
                ELUCombination eluCombination = new ELUCombination(mMethodChoiceMap.get(mMethodChoiceValue.get()));
                maxY = eluCombination.getCombinedUltimateMomentAtXOfSpan(
                        Double.parseDouble(xValueField.getText()), spanNumChoice.getValue(), MAX
                );
                minY = eluCombination.getCombinedUltimateMomentAtXOfSpan(
                        Double.parseDouble(xValueField.getText()), spanNumChoice.getValue(), MIN
                );
            }
            maxCaseMomentValue.setText(FOURDECIMALS.getDecimalFormat().format(maxY));
            minCaseMomentValue.setText(FOURDECIMALS.getDecimalFormat().format(minY));
        });


        VBox resultVBox = new VBox(
                new HBox(maxCaseMomentLabel, maxCaseMomentValue),
                new HBox(minCaseMomentLabel, minCaseMomentValue)
        );

        Button rebarCalculatingButton = new Button(Main.getBundleText("button.calculateRebar"));
        rebarCalculatingButton.setStyle("-fx-font-weight:bold;");
        rebarCalculatingButton.disableProperty().bind(
                Bindings.isNull(mMethodChoice.valueProperty())
                        .or(MainController.isDisabledRebarCalculateProperty())
        );
        rebarCalculatingButton.setOnAction(event -> {
            Reinforcement reinforcement = new Reinforcement(mMethodChoiceMap.get(mMethodChoiceValue.get()));
            ReinforcementResultTable reinforcementResult = new ReinforcementResultTable(reinforcement);
            // TODO Add windows to show the T shaped cross section for each span

            Scene scene = null;
            try {
                Pane container = FXMLLoader.load(
                        getClass().getResource("/fxml/section.fxml"),
                        Main.getResourceBundle());

                /*I wander if it's better to pass the reinforcement instance to the fxml controller
                * as like:
                * TSectionController controller = fxmlLoader.<TSectionController>getController();
                * controller.setReinforcement(reinforcement);*/

                double initialSceneWidth;

                /* initial scene width is according to the flange width (flange width + 200px)
                 or according to num of buttons for spans (N° spans * 130)
                 initial scene height is fix, cause the cross section diagram is fixed at 300px*/
                initialSceneWidth = Math.max(TSectionController.getSceneWidth(), 130 * Geometry.getNumSpan());
                scene = new Scene(container, initialSceneWidth, 700);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCrossSectionStage.setTitle(Main.getBundleText("window.title.crossSection"));
            mCrossSectionStage.getIcons().add(new Image("image/section.png"));
            mCrossSectionStage.setScene(scene);
            mCrossSectionStage.show();
        });

        HBox firstLine = new HBox(methodText, mMethodChoice, spanNumText, spanNumChoice, xAbscissaText, xValueField, lengthUnitText);
        firstLine.setSpacing(10);
        firstLine.setAlignment(Pos.CENTER);
        firstLine.setPadding(new Insets(10, 20, 10, 20));

        HBox secondLine = new HBox(rebarCalculatingButton, calculateYButton, resultVBox);
        secondLine.setSpacing(10);
        secondLine.setAlignment(Pos.CENTER);
        secondLine.setPadding(new Insets(10, 20, 10, 20));

        VBox bottomVBox = new VBox(firstLine, secondLine);
        bottomVBox.setAlignment(Pos.CENTER);
        bottomVBox.setPadding(new Insets(10, 20, 10, 20));

//        checkbox to show or hide line chart

        CheckBox methodCheck = new CheckBox();
        Label methodLabel = new Label(spanMomentFunction.getMethod());
        methodCheck.setSelected(true);

        mDisableSpinner = Bindings.not(methodCheck.selectedProperty());
        mIntegerSpinner.disableProperty().bind(mDisableSpinner);

        methodLabel.setOnMouseClicked(event -> {
            methodCheck.setSelected(!methodCheck.selectedProperty().get());
        });
        methodCheck.selectedProperty().addListener((arg0, oldValue, newValue) -> {
            if (newValue) {
                mLineChart.getData().addAll(
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.max")),
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.min"))
                );
            } else {
                mLineChart.getData().removeAll(
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.max")),
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.min"))
                );
            }
        });

        Label conditionsInfos = new Label(Main.getBundleText("label.methodCondition"));
        setClickableStyle(conditionsInfos);

        conditionsInfos.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Main.getBundleText("label.methodCondition"));
            alert.setHeaderText(Main.getBundleText("alert.head.message"));
            alert.setContentText(Main.getBundleText("alert.message"));
            alert.showAndWait();
        });


//        prepare method hbox

        mHBoxMethod = new HBox(methodCheck, methodLabel);
        mHBoxMethod.setSpacing(5);
        mHBoxMethod.setAlignment(Pos.CENTER);


        VBox methodVBox = new VBox(mHBoxMethod, conditionsInfos);
        methodVBox.setSpacing(5);
        methodVBox.setAlignment(Pos.CENTER);

        StackPane methodStackPane = new StackPane();
        methodStackPane.getChildren().add(methodVBox);
        methodCheck.setAlignment(Pos.CENTER_RIGHT);

//        add top grid pane

        mGridPaneTop = new GridPane();
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(50);
        //mGridPaneTop.getColumnConstraints().add(c3);
        mGridPaneTop.getColumnConstraints().addAll(c1, c2, c3);
        mGridPaneTop.add(hBoxSpinner, 0, 0);
        mGridPaneTop.add(methodStackPane, 1, 0);
        mGridPaneTop.setPadding(new Insets(10, 20, 10, 20));

//        if the methode of calculate is "3 moment", add redistribution for the methode

        if (spanMomentFunction.getMethod().equals(TROIS_MOMENT.getBundleText())
                && !MainController.isDisabledRebarCalculate()
                ) {
            addRedistribution(spanMomentFunction);
        }

//        set border pane

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(mGridPaneTop);
        borderPane.setCenter(mLineChart);
        borderPane.setBottom(bottomVBox);
        borderPane.setPadding(new Insets(20, 20, 20, 20));

//        set mCrossSectionStage and scene

        Stage chartStage = new Stage();
        chartStage.setTitle(Main.getBundleText("window.title.envelop"));
        chartStage.getIcons().add(new Image("image/chart.png"));

        Scene scene = new Scene(borderPane, 1800, 800);
        chartStage.setScene(scene);
        chartStage.show();
    }

    public MomentLineChart(SpanMomentFunction spanMomentFunction1,
                           SpanMomentFunction spanMomentFunction2) {
        this(spanMomentFunction1);
        addNewMomentChart(spanMomentFunction2);
    }

    public MomentLineChart(SpanMomentFunction spanMomentFunction1,
                           SpanMomentFunction spanMomentFunction2,
                           SpanMomentFunction spanMomentFunction3) {
        this(spanMomentFunction1, spanMomentFunction2);
        addNewMomentChart(spanMomentFunction3);
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

        mStringSeriesMap.put(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.max"), newMaxELUSeries);
        mStringSeriesMap.put(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.min"), newMinELUSeries);
        mLineChart.getData().addAll(
                mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.max")),
                mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.min"))
        );

//        bind the spinner listener to the new series

        mIntegerSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            newMaxELUSeries.getData().clear();
            newMinELUSeries.getData().clear();
            createMomentSeries(mIntegerSpinner.getValue(), combination, MAX, newMaxELUSeries);
            createMomentSeries(mIntegerSpinner.getValue(), combination, MIN, newMinELUSeries);
        });

//        check box to show and hide new series

        CheckBox newMethodCheck = new CheckBox();
        Label newMethodLabel = new Label(spanMomentFunction.getMethod());
        newMethodCheck.setSelected(true);
        mHBoxMethod.getChildren().addAll(newMethodCheck, newMethodLabel);

        newMethodLabel.setOnMouseClicked(event -> {
            newMethodCheck.setSelected(!newMethodCheck.selectedProperty().get());
        });
        newMethodCheck.selectedProperty().addListener((arg0, oldValue, newValue) -> {
            if (newValue) {
                mLineChart.getData().addAll(
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.max")),
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.min"))
                );
            } else {
                mLineChart.getData().removeAll(
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.max")),
                        mStringSeriesMap.get(spanMomentFunction.getMethod() + "_" + Main.getBundleText("label.min"))
                );
            }
        });

        mDisableSpinner = mDisableSpinner.and(Bindings.not(newMethodCheck.selectedProperty()));
        mIntegerSpinner.disableProperty().bind(mDisableSpinner);

//        add margin to the y axis

        maxMomentValue.set(-Math.max(
                -maxMomentValue.get(),
                combination.getUltimateMomentValue(MAX)
                )
        );
        minMomentValue.set(-Math.min(
                -minMomentValue.get(),
                combination.getUltimateMomentValue(MIN)
                )
        );
        yAxis.lowerBoundProperty().set(1.2 * maxMomentValue.get());
        yAxis.upperBoundProperty().set(1.2 * minMomentValue.get());

        mMethodChoice.getItems().add(spanMomentFunction.getMethod());

        //        if the method of calculate is "3 moment", add redistribution for the method

        if (spanMomentFunction.getMethod().equals(TROIS_MOMENT.getBundleText())
                && !MainController.isDisabledRebarCalculate()
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

    private void createMomentSeries(
            int numSection,
            ELUCombination eluCombination, UltimateCase ultimateCase,
            XYChart.Series series
    ) {

        for (int spanId = 1; spanId < Geometry.getNumSpan() + 1; spanId++) {

            double spanLength = eluCombination.getSpanMomentFunction().getCalculateSpanLengthMap().get(spanId);
            double spanLocalX = 0;
            double globalX = 0;

            if (eluCombination.getSpanMomentFunction().getMethod().equals(TROIS_MOMENT.getBundleText())) {
                for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                    double preX;
                    if (preSpanId == 0) {
                        preX = Geometry.supportWidthMap().get(1) / 2;
                    } else {
                        preX = eluCombination.getSpanMomentFunction().getCalculateSpanLengthMap().get(preSpanId);
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

            for (int i = 0; i < numSection + 1; i++) {             // Number of data (moment value) is numSection+1
                double moment = -eluCombination.getCombinedUltimateMomentAtXOfSpan(spanLocalX, spanId, ultimateCase);         // negative just because can't inverse the Y axis to show the span moment underside of 0 axis
                final XYChart.Data<Double, Double> data = new XYChart.Data<>(globalX, moment);
                data.setNode(new HoveredThresholdNode(globalX, spanLocalX, moment));
                series.getData().add(data);
                spanLocalX += spanLength / numSection;
                globalX += spanLength / numSection;
            }
        }
        series.setName(Main.getBundleText("label." + ultimateCase.toString().toLowerCase()) + " - " + eluCombination.getSpanMomentFunction().getMethod());
    }

    private void createRedistributionMomentSeries(
            int numSection,
            SpanMomentFunction_SpecialLoadCase spanMomentFunction, UltimateCase ultimateCase,
            XYChart.Series series
    ) {
        spanMomentFunction.getSpanMomentFunctionMap().forEach((spanId, loadCaseMomentFunctionMap) ->
                loadCaseMomentFunctionMap.forEach((loadCase, momentFunction) -> {

                    double spanLength = Geometry.getEffectiveSpansLengthMap().get(spanId);
                    double spanLocalX = 0;
                    double globalX = 0;

                    for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                        double preX;
                        if (preSpanId == 0) {
                            preX = Geometry.supportWidthMap().get(1) / 2;
                        } else {
                            preX = Geometry.getEffectiveSpansLengthMap().get(preSpanId);
                        }
                        globalX += preX;
                    }

                    for (int i = 0; i < numSection + 1; i++) {             // Number of data (moment value) is numSection+1
                        double moment = -spanMomentFunction.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                                spanLocalX, spanId, ultimateCase
                        );         // negative just because can't inverse the Y axis to show the span moment underside of 0 axis
                        final XYChart.Data<Double, Double> data = new XYChart.Data<>(globalX, moment);
                        data.setNode(new HoveredThresholdNode(globalX, spanLocalX, moment));
                        series.getData().add(data);
                        spanLocalX += spanLength / numSection;
                        globalX += spanLength / numSection;
                    }


                })
        );
        series.setName(Main.getBundleText("label."
                + ultimateCase.toString().toLowerCase())
                + " - "
                + TROIS_MOMENT_R.getBundleText());
    }


    private void addRedistribution(SpanMomentFunction spanMomentFunction) {

        Label rdsLabel = new Label(Main.getBundleText("label.redistribution"));
        CheckBox rdsCheck = new CheckBox();
        Button rdsConf = new Button(Main.getBundleText("button.rdsConfig"));

        rdsLabel.setOnMouseClicked(event -> {
            rdsCheck.setSelected(!rdsCheck.selectedProperty().get());
        });

        HBox rdsHBox = new HBox(rdsLabel, rdsCheck, rdsConf);
        rdsHBox.setSpacing(10);
        rdsHBox.setAlignment(Pos.CENTER_RIGHT);


        mGridPaneTop.add(rdsHBox, 2, 0);

        mDisableSpinner = mDisableSpinner.and(Bindings.not(rdsCheck.selectedProperty()));
        mIntegerSpinner.disableProperty().bind(mDisableSpinner);

        ELUCombination combination = new ELUCombination(spanMomentFunction);
        MomentRedistribution momentRedistribution = new MomentRedistribution(combination);

        Map<Integer, Double> calculatedFinalRedCoefMap = momentRedistribution.getFinalRedistributionCoefMap();
        Map<Integer, Double> calculatedRedCoefMap = momentRedistribution.getRedistributionCoefMap();

        Map<Integer, Double> usedRedCoefMap = new HashMap<>();
        calculatedFinalRedCoefMap.forEach(usedRedCoefMap::put);

        calculateRedistributionMoment(spanMomentFunction, usedRedCoefMap);

        //TODO This is not the correct way to add method to the method choice box
        mMethodChoice.getItems().add(TROIS_MOMENT_R.getBundleText());

        rdsCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
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


                mStringSeriesMap.put(TROIS_MOMENT.getBundleText() + "_ReducedMAX", maxELUSeries);
                mStringSeriesMap.put(TROIS_MOMENT.getBundleText() + "_ReducedMIN", minELUSeries);

                mLineChart.getData().addAll(
                        mStringSeriesMap.get(TROIS_MOMENT.getBundleText() + "_ReducedMAX"),
                        mStringSeriesMap.get(TROIS_MOMENT.getBundleText() + "_ReducedMIN")
                );
            } else {
                mLineChart.getData().removeAll(
                        mStringSeriesMap.get(TROIS_MOMENT.getBundleText() + "_ReducedMAX"),
                        mStringSeriesMap.get(TROIS_MOMENT.getBundleText() + "_ReducedMIN")
                );

            }
        });

        VBox paramNameVBox = new VBox();
        paramNameVBox.setSpacing(15);
        Label blank = new Label("");
        Label rdsCoef = new Label(Main.getBundleText("label.theoRdsCoef"));
        Label minRdsCoef = new Label(Main.getBundleText("label.minRdsCoef"));
        Label finalRdsCoef = new Label(Main.getBundleText("label.finalRdsCoef"));
        blank.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
        paramNameVBox.getChildren().addAll(blank, rdsCoef, minRdsCoef, finalRdsCoef);


        HBox paramValuesHBox = new HBox();
        paramValuesHBox.setSpacing(20);
        paramValuesHBox.setAlignment(Pos.CENTER);
        calculatedRedCoefMap.forEach((supportId, coef) -> {
            VBox supportParamValueVBox = new VBox();
            supportParamValueVBox.setSpacing(15);
            Label sectionLabel = new Label(Main.getBundleText("label.support") + " " + supportId.toString());
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
                        System.out.println(Main.getBundleText("message.enterCoef"));
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
        Button confirmButton = new Button(Main.getBundleText("button.ok"));
        Button applyButton = new Button(Main.getBundleText("button.apply"));
        bottomHBox.getChildren().addAll(applyButton, confirmButton);
        applyButton.setOnAction(event -> {
            rdsCheck.setSelected(false);
            rdsCheck.setSelected(true);
        });

        BorderPane container = new BorderPane();
        container.setPadding(new Insets(20, 20, 20, 20));
        container.setCenter(centerHBox);
        container.setBottom(bottomHBox);


        Stage configStage = new Stage();
        configStage.setTitle(Main.getBundleText("window.title.redistribution"));
        configStage.getIcons().add(new Image("image/configuration.png"));

        Scene scene = new Scene(container, 1000, 300);
        configStage.setScene(scene);

        rdsConf.setOnAction(event -> {
            configStage.show();
        });

        confirmButton.setOnAction(event -> {
            rdsCheck.setSelected(false);
            rdsCheck.setSelected(true);
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

    public static NumberAxis getxAxis() {
        return xAxis;
    }

    public static NumberAxis getyAxis() {
        return yAxis;
    }

    public static Map<String, XYChart.Series> getStringSeriesMap() {
        return mStringSeriesMap;
    }
}


