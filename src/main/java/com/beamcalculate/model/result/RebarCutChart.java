package com.beamcalculate.model.result;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

import com.beamcalculate.model.RebarCase;
import com.beamcalculate.model.custom_node.HoveredThresholdNode;
import com.beamcalculate.model.MyMethods;
import com.beamcalculate.model.RebarType_Amount;
import com.beamcalculate.model.calculator.Rebar;
import com.beamcalculate.model.calculator.span_function.AbstractSpanMoment;
import com.beamcalculate.model.calculator.span_function.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.ReinforcementParam.a_M;
import static com.beamcalculate.enums.ReinforcementParam.j_A_S;
import static com.beamcalculate.enums.UltimateCase.MAX_MOMENT_TAG;
import static com.beamcalculate.enums.UltimateCase.MIN_MOMENT_TAG;
import static com.beamcalculate.model.page_manager.MomentLineChartTreater.*;

public class RebarCutChart {
    private AbstractSpanMoment mSpanMoment;
    private double mFirstLayerMoment;
    private double mCumulativeMoment;

    private double mAnchorageLength_mm;

    private NumberAxis mXAxis;
    private NumberAxis mYAxis;
    private double mSecondLayerRebarEnd;
    private double mSecondLayerRebarStart;

    private Scene mScene;

    private Geometry mGeometry;

    public RebarCutChart(Rebar rebar, int spanId, int caseNum) {
        mSpanMoment = rebar.getReinforcement().getSpanMomentFunction();
        mGeometry = mSpanMoment.getInputs().getGeometry();
        String calculateMethod = mSpanMoment.getMethod();

        mXAxis = defineAxis(mSpanMoment).get(0);
        mYAxis = defineAxis(mSpanMoment).get(1);

        XYChart.Series<Number, Number> maxSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> minSeries = new XYChart.Series<>();

        if (calculateMethod.equals(TROIS_MOMENT_R.getMethodName())) {
            addDataToRedistributionMomentSeries(500, (SpanMomentFunction_SpecialLoadCase) mSpanMoment, MAX_MOMENT_TAG, maxSeries);
            addDataToRedistributionMomentSeries(500, (SpanMomentFunction_SpecialLoadCase) mSpanMoment, MIN_MOMENT_TAG, minSeries);

        } else {
            addDataToMomentSeries(500, mSpanMoment, MAX_MOMENT_TAG, maxSeries);
            addDataToMomentSeries(500, mSpanMoment, MIN_MOMENT_TAG, minSeries);
        }

        //for all series, take date, each data has custom_node (symbol) for representing point
        removeLineChartPoints(maxSeries);
        removeLineChartPoints(minSeries);

        LineChart<Number, Number> lineChart = new LineChart<>(mXAxis, mYAxis);

        lineChart.getData().addAll(maxSeries, minSeries);

        double moveDistance = 1.25 * 0.9 * mGeometry.getEffectiveHeight();

        double startPoint = getStartGlobalXOfSpan(spanId);
        double endPoint = startPoint + mSpanMoment.getCalculateSpanLengthMap().get(spanId);

        addLimitsToLineChart(lineChart, startPoint, endPoint);

        double maxMomentValueOfSpan = 0;
        double preMaxMomentXValue = 0;
        for (int i=0; i < maxSeries.getData().size(); i++){
            double xValue = maxSeries.getData().get(i).getXValue().doubleValue();
            // TODO When inverse the y axis properly, this negative sign should be removed
            double maxMomentValue = - maxSeries.getData().get(i).getYValue().doubleValue();
            if(xValue >= startPoint && xValue <= endPoint && maxMomentValue > maxMomentValueOfSpan){
                maxMomentValueOfSpan = maxMomentValue;
                preMaxMomentXValue = xValue;
            }
        }

        // Negative to meet the inverse y axis
        double rebarAreaMomentRatio =
                - rebar.getReinforcement().getSpanReinforceParam().get(spanId).get(a_M) /
                        rebar.getReinforcement().getSpanReinforceParam().get(spanId).get(j_A_S);

        Map<Integer, Double> layer_rebarArea_map = rebar.getRebarAreaListForEachLayerOfSpan_cm2(spanId).get(caseNum);

        // TODO this switch statement should be finished, the number of layers could be more than 2
        switch (layer_rebarArea_map.size()){
            case 1 : {
                setCumulativeMoment(layer_rebarArea_map.get(1) * rebarAreaMomentRatio);
                XYChart.Data<Number, Number> data3 = new XYChart.Data<>(startPoint, getCumulativeMoment());
                XYChart.Data<Number, Number> data4 = new XYChart.Data<>(endPoint, getCumulativeMoment());
                XYChart.Series<Number, Number> cumulativeSeries = new XYChart.Series<>();
                cumulativeSeries.getData().addAll(data3, data4);
                removeLineChartPoints(cumulativeSeries);
                cumulativeSeries.setName(getBundleText("legend.momentRst_1stLayer"));

                mYAxis.setLowerBound(1.2 * getCumulativeMoment());

                lineChart.getData().addAll(cumulativeSeries);
                break;
            }
            case 2 : {
                List<Double> secondLayerRebarCutPointsList = new ArrayList<>();

                double secondLayerRebarDiameter = rebar.getRebarCasesListOfSpan(spanId).get(caseNum).getRebarDiamOfLayer_mm(2);
                setAnchorageLength_mm(40 * secondLayerRebarDiameter);

                setFirstLayerMoment(layer_rebarArea_map.get(1) * rebarAreaMomentRatio);
                setCumulativeMoment(getFirstLayerMoment() + layer_rebarArea_map.get(2) * rebarAreaMomentRatio);

                XYChart.Data<Number, Number> data1 = new XYChart.Data<>(startPoint, getFirstLayerMoment());
                XYChart.Data<Number, Number> data2 = new XYChart.Data<>(endPoint, getFirstLayerMoment());
                XYChart.Series<Number, Number> firstLayerSeries = new XYChart.Series<>();
                firstLayerSeries.getData().addAll(data1, data2);
                removeLineChartPoints(firstLayerSeries);
                firstLayerSeries.setName(getBundleText("legend.momentRst_1stLayer"));

                XYChart.Data<Number, Number> data3 = new XYChart.Data<>(startPoint, getCumulativeMoment());
                XYChart.Data<Number, Number> data4 = new XYChart.Data<>(endPoint, getCumulativeMoment());
                XYChart.Series<Number, Number> cumulativeSeries = new XYChart.Series<>();
                cumulativeSeries.getData().addAll(data3, data4);
                removeLineChartPoints(cumulativeSeries);
                cumulativeSeries.setName(getBundleText("legend.momentRst_1+2Layer"));

                mYAxis.setLowerBound(1.2 * getCumulativeMoment());

                lineChart.getData().addAll(firstLayerSeries, cumulativeSeries);

                double finalMaxMomentXValue = preMaxMomentXValue;
                XYChart.Series<Number, Number> offsetMaxSeries = new XYChart.Series<>();
                maxSeries.getData().forEach(numberData -> {
                    double xValue = numberData.getXValue().doubleValue();
                    double yValue = numberData.getYValue().doubleValue();
                    if (yValue <= 0 && xValue >= startPoint && xValue <= endPoint) {
                        if (xValue < finalMaxMomentXValue) {
                            XYChart.Data<Number, Number> data = new XYChart.Data<>(xValue - moveDistance, yValue);
                            offsetMaxSeries.getData().add(data);
                        } else if (xValue > finalMaxMomentXValue) {
                            XYChart.Data<Number, Number> data = new XYChart.Data<>(xValue + moveDistance, yValue);
                            offsetMaxSeries.getData().add(data);
                        }
                    }
                });
                offsetMaxSeries.setName(getBundleText("legend.offsetEnvelopCurve"));
                lineChart.getData().add(offsetMaxSeries);

                XYChart.Data<Number, Number> startData = new XYChart.Data<>(startPoint, getFirstLayerMoment());
                XYChart.Data<Number, Number> endData = new XYChart.Data<>(endPoint, getFirstLayerMoment());
                XYChart.Series<Number, Number> global = new XYChart.Series<>();
                global.setName(getBundleText("legend.momentRst_global"));
                lineChart.getData().add(global);

                global.getData().add(startData);
                offsetMaxSeries.getData().forEach(numberData -> {
                    double xValue = numberData.getXValue().doubleValue();
                    double yValue = numberData.getYValue().doubleValue();

                    double tolerance = Math.abs(rebar.getReinforcement().getSpanReinforceParam().get(spanId).get(a_M) / 250);

                    // TODO Find a more reliable way to located the intersection point  !!! URGENT !!!
                    if (Math.abs(MyMethods.round(yValue, 4) - MyMethods.round(getFirstLayerMoment(), 4))
                            > tolerance)
                    {
                        StackPane stackPane = new StackPane();
                        stackPane.setVisible(false);
                        numberData.setNode(stackPane);

                    } else {
                        secondLayerRebarCutPointsList.add(xValue);
                        final XYChart.Data<Number, Number> firstLayerIntersectionData = new XYChart.Data<>(xValue, getFirstLayerMoment());
                        firstLayerIntersectionData.setNode(new HoveredThresholdNode(xValue, xValue, getFirstLayerMoment()));
                        global.getData().add(firstLayerIntersectionData);
                        if (xValue < finalMaxMomentXValue) {
                            final XYChart.Data<Number, Number> secondLayerIntersectionData =
                                    new XYChart.Data<>(xValue + getAnchorageLength_mm()/1000, getCumulativeMoment());
                            secondLayerIntersectionData.setNode(
                                    new HoveredThresholdNode(
                                            xValue + getAnchorageLength_mm()/1000,
                                            xValue + getAnchorageLength_mm()/1000,
                                            getCumulativeMoment()
                                    )
                            );
                            global.getData().add(secondLayerIntersectionData);
                        } else {
                            final XYChart.Data<Number, Number> secondLayerIntersectionData =
                                    new XYChart.Data<>(xValue - getAnchorageLength_mm()/1000, getCumulativeMoment());
                            secondLayerIntersectionData.setNode(
                                    new HoveredThresholdNode(
                                            xValue + getAnchorageLength_mm()/1000,
                                            xValue + getAnchorageLength_mm()/1000,
                                            getCumulativeMoment()
                                    )
                            );
                            global.getData().add(secondLayerIntersectionData);
                        }
                    }
                });
                global.getData().add(endData);

                if (secondLayerRebarCutPointsList.isEmpty()){
                    mSecondLayerRebarStart = 0;
                    mSecondLayerRebarEnd = 0;
                    System.out.println("No intersection points with second layer moment line !!!");

                } else {
                    mSecondLayerRebarStart = getSpanLocalX(
                            Collections.min(secondLayerRebarCutPointsList), mSpanMoment
                    );
                    mSecondLayerRebarEnd = getSpanLocalX(
                            Collections.max(secondLayerRebarCutPointsList), mSpanMoment
                    );
                }
            }
        }

        Label titleSpanLabel = new Label(getBundleText("label.span") + " " + spanId + " :");
        Label titleRebarCaseLabel = new Label();
        titleSpanLabel.getStyleClass().add("title");
        List<RebarCase> rebarCasesList = rebar.getRebarCasesListOfSpan(spanId);
        titleRebarCaseLabel.setText(rebarCasesList.get(caseNum).toString());
        titleRebarCaseLabel.getStyleClass().add("rebar");

        HBox titleHBox = new HBox(titleSpanLabel, titleRebarCaseLabel);
        titleHBox.setSpacing(10);
        titleHBox.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(titleHBox);
        borderPane.setCenter(lineChart);
        borderPane.setPadding(new Insets(20, 20, 20, 20));

        mScene = new Scene(borderPane, 800, 800);
        mScene.getStylesheets().add("/css/rebar_cut_chart.css");
    }

    private void addLimitsToLineChart(LineChart<Number, Number> lineChart, double startPoint, double endPoint) {
        DoubleProperty maxSupportMoment = new SimpleDoubleProperty();
        lineChart.getData().forEach(numberNumberSeries -> {
            List<XYChart.Data<Number, Number>> newDataList = new ArrayList<>();
            numberNumberSeries.getData().forEach(numberNumberData -> {
                if(startPoint <= numberNumberData.getXValue().doubleValue() && numberNumberData.getXValue().doubleValue() <= endPoint ){
                    newDataList.add(numberNumberData);
                    maxSupportMoment.set(Math.max(numberNumberData.getYValue().doubleValue(), maxSupportMoment.get()));
                }
            });
            numberNumberSeries.getData().clear();
            numberNumberSeries.getData().addAll(newDataList);
        });

        mYAxis.upperBoundProperty().set(1.2 * maxSupportMoment.get());

        mXAxis.lowerBoundProperty().set(startPoint - 1);
        mXAxis.upperBoundProperty().set(endPoint + 1);
    }

    private void removeLineChartPoints(XYChart.Series<Number, Number> numberSeriesSeries) {
        for (XYChart.Data<Number, Number> data : numberSeriesSeries.getData()) {
            StackPane stackPane = new StackPane();
            stackPane.setVisible(false);
            data.setNode(stackPane);
        }
    }

    private double getStartGlobalXOfSpan(int spanId){
        double globalX = 0;
        if (mSpanMoment.getMethod().equals(TROIS_MOMENT_R.getMethodName())) {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preX;
                if (preSpanId == 0) {
                    preX = mGeometry.supportWidthMap().get(1) / 2;
                } else {
                    preX = mSpanMoment.getCalculateSpanLengthMap().get(preSpanId);
                }
                globalX += preX;
            }
        } else {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preSpanLength = 0;
                double preSupportLength;
                if (preSpanId == 0) {
                    preSupportLength = mGeometry.supportWidthMap().get(1);
                } else {
                    preSpanLength = mGeometry.spansLengthMap().get(preSpanId);
                    preSupportLength = mGeometry.supportWidthMap().get(preSpanId + 1);
                }
                globalX += (preSpanLength + preSupportLength);
            }
        }
        return globalX;
    }

    public Scene getScene() {
        return mScene;
    }

    public double getFirstLayerMoment() {
        return mFirstLayerMoment;
    }

    public void setFirstLayerMoment(double firstLayerMoment) {
        mFirstLayerMoment = firstLayerMoment;
    }

    public double getCumulativeMoment() {
        return mCumulativeMoment;
    }

    public void setCumulativeMoment(double cumulativeMoment) {
        mCumulativeMoment = cumulativeMoment;
    }

    public double getAnchorageLength_mm() {
        return mAnchorageLength_mm;
    }

    public void setAnchorageLength_mm(double anchorageLength_mm) {
        mAnchorageLength_mm = anchorageLength_mm;
    }

    public double getCalculateLengthOfSpan(int spanId){
        return mSpanMoment.getCalculateSpanLengthMap().get(spanId);
    }

    public double getSecondLayerRebarEnd() {
        return mSecondLayerRebarEnd;
    }

    public double getSecondLayerRebarStart() {
        return mSecondLayerRebarStart;
    }
}
