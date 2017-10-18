package com.beamcalculate.model.result;

import com.beamcalculate.Main;
import com.beamcalculate.enums.MyMath;
import com.beamcalculate.model.calculate.ELUCombination;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.span.AbstractSpanMoment;
import com.beamcalculate.model.calculate.span.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Map;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT;
import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.ReinforcementParam.a_M;
import static com.beamcalculate.enums.ReinforcementParam.j_A_S;
import static com.beamcalculate.enums.UltimateCase.MAX;
import static com.beamcalculate.enums.UltimateCase.MIN;

public class RebarChart {

    private AbstractSpanMoment mSpanMoment;
    private double mFirstLayerMoment;
    private double mCumulativeMoment;

    private double mAnchorageLength_mm;

    public RebarChart(Rebar rebar, int spanId, int caseNum) {
        mSpanMoment = rebar.getReinforcement().getSpanMomentFunction();

        NumberAxis xAxis = MomentLineChart.defineAxis(mSpanMoment).get(0);
        NumberAxis yAxis = MomentLineChart.defineAxis(mSpanMoment).get(1);

        XYChart.Series<Number, Number> maxSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> minSeries = new XYChart.Series<>();

        if (mSpanMoment.getMethod().equals(TROIS_MOMENT_R.getBundleText())) {
            MomentLineChart.createRedistributionMomentSeries(300, (SpanMomentFunction_SpecialLoadCase) mSpanMoment, MAX, maxSeries);
            MomentLineChart.createRedistributionMomentSeries(300, (SpanMomentFunction_SpecialLoadCase) mSpanMoment, MIN, minSeries);

        } else {
            ELUCombination combination = new ELUCombination(mSpanMoment);
            MomentLineChart.createMomentSeries(300, combination, MAX, maxSeries);
            MomentLineChart.createMomentSeries(300, combination, MIN, minSeries);
        }

        //for all series, take date, each data has Node (symbol) for representing point
        removeLineChartPoints(maxSeries);
        removeLineChartPoints(minSeries);

        LineChart<Number, Number> lineChart = new LineChart(xAxis, yAxis);

        lineChart.getData().addAll(maxSeries, minSeries);

        double moveDistance = 1.25 * 0.9 * Geometry.getEffectiveHeight();

        double startPoint = getStartGlobalXOfSpan(spanId);
        double endPoint = startPoint + mSpanMoment.getCalculateSpanLengthMap().get(spanId);

        double maxMomentValueOfSpan = 0;
        double preMaxMomentXValue = 0;
        for (int i=0; i < maxSeries.getData().size(); i++){
            double xValue = maxSeries.getData().get(i).getXValue().doubleValue();
            // TODO When change the sign of the axis, this negative sign should be removed
            double maxMomentValue = - maxSeries.getData().get(i).getYValue().doubleValue();
            if(xValue >= startPoint && xValue <= endPoint && maxMomentValue > maxMomentValueOfSpan){
                maxMomentValueOfSpan = maxMomentValue;
                preMaxMomentXValue = xValue;
            }
        }

        double rebarAreaMomentRatio =
                - rebar.getReinforcement().getSpanReinforceParam().get(spanId).get(a_M) /
                        rebar.getReinforcement().getSpanReinforceParam().get(spanId).get(j_A_S);

        Map<Integer, Double> layer_rebarArea_map = rebar.getRebarAreaListForEachLayerOfSpan(spanId).get(caseNum);

        // TODO this switch statement should be finished, the number of layers could be more than 2
        switch (layer_rebarArea_map.size()){
            case 1 : {
                setCumulativeMoment(layer_rebarArea_map.get(1) * rebarAreaMomentRatio);
                XYChart.Data<Number, Number> data3 = new XYChart.Data<>(startPoint, getCumulativeMoment());
                XYChart.Data<Number, Number> data4 = new XYChart.Data<>(endPoint, getCumulativeMoment());
                XYChart.Series<Number, Number> cumulativeSeries = new XYChart.Series();
                cumulativeSeries.getData().addAll(data3, data4);
                removeLineChartPoints(cumulativeSeries);
                cumulativeSeries.setName("MR1er+2ème lit");

                yAxis.setLowerBound(1.2 * getCumulativeMoment());

                lineChart.getData().addAll(cumulativeSeries);
                break;
            }
            case 2 : {
                double secondLayerRebarDiameter = rebar.getRebarCasesListOfSpan(spanId).get(caseNum).get(2).getRebarType().getDiameter_mm();
                setAnchorageLength_mm(Math.max(40 * secondLayerRebarDiameter, getAnchorageLength_mm()));

                setFirstLayerMoment(layer_rebarArea_map.get(1) * rebarAreaMomentRatio);
                setCumulativeMoment(getFirstLayerMoment() + layer_rebarArea_map.get(2) * rebarAreaMomentRatio);

                XYChart.Data<Number, Number> data1 = new XYChart.Data<>(startPoint, getFirstLayerMoment());
                XYChart.Data<Number, Number> data2 = new XYChart.Data<>(endPoint, getFirstLayerMoment());
                XYChart.Series<Number, Number> firstLayerSeries = new XYChart.Series();
                firstLayerSeries.getData().addAll(data1, data2);
                removeLineChartPoints(firstLayerSeries);
                firstLayerSeries.setName("MR1er lit");

                XYChart.Data<Number, Number> data3 = new XYChart.Data<>(startPoint, getCumulativeMoment());
                XYChart.Data<Number, Number> data4 = new XYChart.Data<>(endPoint, getCumulativeMoment());
                XYChart.Series<Number, Number> cumulativeSeries = new XYChart.Series();
                cumulativeSeries.getData().addAll(data3, data4);
                removeLineChartPoints(cumulativeSeries);
                cumulativeSeries.setName("MR1er+2ème lit");

                yAxis.setLowerBound(1.2 * getCumulativeMoment());

                lineChart.getData().addAll(firstLayerSeries, cumulativeSeries);

                double finalMaxMomentXValue = preMaxMomentXValue;
                XYChart.Series<Number, Number> newMaxSeries = new XYChart.Series<>();
                maxSeries.getData().forEach(numberData -> {
                    double xValue = numberData.getXValue().doubleValue();
                    double yValue = numberData.getYValue().doubleValue();
                    if (yValue <= 0 && xValue >= startPoint && xValue <= endPoint) {
                        if (xValue < finalMaxMomentXValue) {
                            XYChart.Data<Number, Number> data = new XYChart.Data<>(xValue - moveDistance, yValue);
                            newMaxSeries.getData().add(data);
                        } else if (xValue > finalMaxMomentXValue) {
                            XYChart.Data<Number, Number> data = new XYChart.Data<>(xValue + moveDistance, yValue);
                            newMaxSeries.getData().add(data);
                        }
                    }
                });
                // removeLineChartPoints(newMaxSeries);
                newMaxSeries.setName("new");
                lineChart.getData().add(newMaxSeries);

                XYChart.Data<Number, Number> startData = new XYChart.Data<>(startPoint, getFirstLayerMoment());
                XYChart.Data<Number, Number> endeData = new XYChart.Data<>(endPoint, getFirstLayerMoment());
                XYChart.Series<Number, Number> global = new XYChart.Series();
                global.setName("MR global des aciers");
                lineChart.getData().add(global);

                global.getData().add(startData);
                newMaxSeries.getData().forEach(numberData -> {
                    double xValue = numberData.getXValue().doubleValue();
                    double yValue = numberData.getYValue().doubleValue();

                    // TODO To find a more reliable way to located the intersection point
                    double tolerance = Math.abs(rebar.getReinforcement().getSpanReinforceParam().get(spanId).get(a_M) / 170);
                    if (Math.abs(MyMath.round(yValue, 4) - MyMath.round(getFirstLayerMoment(), 4))
                            > tolerance){
                        StackPane stackPane = new StackPane();
                        stackPane.setVisible(false);
                        numberData.setNode(stackPane);
                    } else {
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
                global.getData().add(endeData);
            }
        }

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(lineChart);
        borderPane.setPadding(new Insets(20, 20, 20, 20));

        Stage rebarChartStage = new Stage();
        rebarChartStage.setTitle(Main.getBundleText("window.title.rebar"));
        rebarChartStage.getIcons().add(new Image("image/chart.png"));

        Scene scene = new Scene(borderPane, 1800, 800);
        rebarChartStage.setScene(scene);
        rebarChartStage.show();
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
        if (mSpanMoment.getMethod().equals(TROIS_MOMENT_R.getBundleText())) {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preX;
                if (preSpanId == 0) {
                    preX = Geometry.supportWidthMap().get(1) / 2;
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
}
