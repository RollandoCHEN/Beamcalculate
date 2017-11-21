package com.beamcalculate.model.page_manager;

import static com.beamcalculate.model.MyMethods.round;
import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

import com.beamcalculate.model.calculator.ELUCombination;
import com.beamcalculate.model.calculator.span_function.AbstractSpanMoment;
import com.beamcalculate.model.calculator.span_function.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.enums.UltimateCase;
import com.beamcalculate.model.custom_node.HoveredThresholdNode;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT;
import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.UltimateCase.MAX_MOMENT_TAG;
import static com.beamcalculate.enums.UltimateCase.MIN_MOMENT_TAG;

public class MomentLineChartTreater {

    public static List<NumberAxis> defineAxis(AbstractSpanMoment spanMomentFunction) {
        double maxSpanMomentValue;
        double maxSupportMomentValue;

        if (spanMomentFunction.getMethod().equals(TROIS_MOMENT_R.getMethodName())) {
            SpanMomentFunction_SpecialLoadCase newSpanMomentFunction = (SpanMomentFunction_SpecialLoadCase) spanMomentFunction;
            maxSupportMomentValue = newSpanMomentFunction.getUltimateMomentValue(MIN_MOMENT_TAG);
            maxSpanMomentValue = newSpanMomentFunction.getUltimateMomentValue(MAX_MOMENT_TAG);
        } else {
            ELUCombination combination = new ELUCombination(spanMomentFunction);
            maxSupportMomentValue = combination.getUltimateMomentValue(MIN_MOMENT_TAG);
            maxSpanMomentValue = combination.getUltimateMomentValue(MAX_MOMENT_TAG);
        }

        NumberAxis xAxis = new NumberAxis(
                -1,
                spanMomentFunction.getInputs().getGeometry().getTotalLength() + 1,
                1
        );
        NumberAxis yAxis = new NumberAxis(
                -1.2 * maxSpanMomentValue,
                -1.2 * maxSupportMomentValue,
                0.05
        );

        // TODO Find a way to inverse the y axis properly
        // !!!!!!!!! Trick to display the minus tick label for the y axis !!!!!!
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number value) {
                // note we are printing minus value
                return String.format("%.4f", -value.doubleValue());
            }
        });

        xAxis.setLabel(getBundleText("label.abscissa") + " (" + getBundleText("unit.length.m") + ")");
        yAxis.setLabel(getBundleText("label.ordinate") + " (" + getBundleText("unit.moment") + ")");

        List<NumberAxis> axisList = new ArrayList<>();
        axisList.add(xAxis);
        axisList.add(yAxis);

        return axisList;
    }

    public static void addDataToMomentSeries(
            int numSection,
            AbstractSpanMoment spanMomentFunction, UltimateCase ultimateCase,
            XYChart.Series<Number, Number> series
    ) {
        Geometry geometry = spanMomentFunction.getInputs().getGeometry();
        ELUCombination eluCombination = new ELUCombination(spanMomentFunction);
        for (int spanId = 1; spanId < geometry.getNumSpan() + 1; spanId++) {
            double spanLength = eluCombination.getSpanMomentFunction().getCalculateSpanLengthMap().get(spanId);
            double spanLocalX = 0;

            double globalX = getGlobalX(spanId, spanLocalX, spanMomentFunction);

            for (int i = 0; i < numSection + 1; i++) {             // Number of data (moment value) is numSection+1
                // TODO When inverse the y axis properly, this negative sign should be removed
                // negative just because can't inverse the Y axis to show the span_function moment underside of 0 axis
                double moment = - eluCombination.getCombinedUltimateMomentAtXOfSpan(spanLocalX, spanId, ultimateCase);
                final XYChart.Data<Number, Number> data = new XYChart.Data<>(globalX, moment);
                data.setNode(new HoveredThresholdNode(globalX, spanLocalX, moment));
                series.getData().add(data);
                spanLocalX += spanLength / numSection;
                globalX += spanLength / numSection;
            }
        }
        series.setName(eluCombination.getSpanMomentFunction().getMethod()
                + " - "
                + getBundleText("label." + ultimateCase.toString().toLowerCase())
        );
    }

    public static void addDataToRedistributionMomentSeries(
            int numSection,
            SpanMomentFunction_SpecialLoadCase spanMomentFunction, UltimateCase ultimateCase,
            XYChart.Series series
    ) {
        spanMomentFunction.getSpanMomentFunctionMap().forEach((spanId, loadCaseMomentFunctionMap) -> {
            double spanLength = spanMomentFunction.getGeometry().getEffectiveSpansLengthMap().get(spanId);
            double spanLocalX = 0;
            double globalX = getGlobalX(spanId, spanLocalX, spanMomentFunction);

            for (int i = 0; i < numSection + 1; i++) {             // Number of data (moment value) is numSection+1
                // TODO When inverse the y axis properly, this negative sign should be removed
                // negative just because can't inverse the Y axis to show the span_function moment underside of 0 axis
                double moment = - spanMomentFunction.getUltimateMomentForSpecialLoadCaseAtXOfSpan(
                        spanLocalX, spanId, ultimateCase
                );
                final XYChart.Data<Double, Double> data = new XYChart.Data<>(globalX, moment);
                data.setNode(new HoveredThresholdNode(globalX, spanLocalX, moment));
                series.getData().add(data);
                spanLocalX += spanLength / numSection;
                globalX += spanLength / numSection;
            }
        });
        series.setName(TROIS_MOMENT_R.getMethodName()
                + " - "
                + getBundleText("label." + ultimateCase.toString().toLowerCase())
        );
    }

    public static double getGlobalX(int spanId, double spanLocalX, AbstractSpanMoment spanMoment) {
        String method = spanMoment.getMethod();
        Geometry geometry = spanMoment.getInputs().getGeometry();
        double globalX = spanLocalX;
        if (TROIS_MOMENT.getMethodName().equals(method)
                || TROIS_MOMENT_R.getMethodName().equals(method)) {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preX;
                if (preSpanId == 0) {
                    preX = round(geometry.supportWidthMap().get(1) / 2, 2);
                } else {
                    preX = geometry.getEffectiveSpansLengthMap().get(preSpanId);
                }
                globalX = round((globalX + preX), 2);
            }
        } else {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preSpanLength = 0;
                double preSupportLength;
                if (preSpanId == 0) {
                    preSupportLength = geometry.supportWidthMap().get(1);
                } else {
                    preSpanLength = geometry.spansLengthMap().get(preSpanId);
                    preSupportLength = geometry.supportWidthMap().get(preSpanId + 1);
                }
                globalX += (preSpanLength + preSupportLength);
            }
        }
        return globalX;
    }

    // TODO Simplify this method by removing spanId parameter
    public static double getSpanLocalX(double globalX, AbstractSpanMoment spanMoment) {
        String method = spanMoment.getMethod();
        Geometry geometry = spanMoment.getInputs().getGeometry();
        double spanLocalX = globalX;
        if (TROIS_MOMENT.getMethodName().equals(method)
                || TROIS_MOMENT_R.getMethodName().equals(method)) {
            int preSpanId = 0;
            while(globalX >= 0){
                spanLocalX = globalX;
                double preX;
                if (preSpanId == 0) {
                    preX = round(geometry.supportWidthMap().get(1) / 2, 2);
                } else {
                    preX = geometry.getEffectiveSpansLengthMap().get(preSpanId);
                }
                globalX = round((globalX - preX), 2);
                preSpanId++;
                if (preSpanId > geometry.getNumSpan()){
                    break;
                }
            }
        } else {
            int preSpanId = 0;
            while(globalX >= 0){
                spanLocalX = globalX;
                double preSpanLength;
                double preSupportLength;
                if (preSpanId == 0) {
                    preSpanLength = 0;
                    preSupportLength = geometry.supportWidthMap().get(1);
                } else {
                    preSpanLength = geometry.spansLengthMap().get(preSpanId);
                    preSupportLength = geometry.supportWidthMap().get(preSpanId + 1);
                }
                globalX -= (preSpanLength + preSupportLength);
                preSpanId++;
            }
        }
        return spanLocalX;
    }

    public static int getSpanId(double globalX, AbstractSpanMoment spanMoment) {
        String method = spanMoment.getMethod();
        Geometry geometry = spanMoment.getInputs().getGeometry();
        int spanId = 1;
        if (TROIS_MOMENT.getMethodName().equals(method)
                || TROIS_MOMENT_R.getMethodName().equals(method)) {
            int preSpanId = 0;
            while(globalX >= 0){
                spanId = preSpanId;
                double preX;
                if (preSpanId == 0) {
                    preX = round(geometry.supportWidthMap().get(1) / 2, 2);
                } else {
                    preX = geometry.getEffectiveSpansLengthMap().get(preSpanId);
                }
                globalX = round((globalX - preX), 2);
                preSpanId++;
                if (preSpanId > geometry.getNumSpan()){
                    break;
                }
            }
        } else {
            int preSpanId = 0;
            while(globalX >= 0){
                spanId = preSpanId;
                double preSpanLength;
                double preSupportLength;
                if (preSpanId == 0) {
                    preSpanLength = 0;
                    preSupportLength = geometry.supportWidthMap().get(1);
                } else {
                    preSpanLength = geometry.spansLengthMap().get(preSpanId);
                    preSupportLength = geometry.supportWidthMap().get(preSpanId + 1);
                }
                globalX -= (preSpanLength + preSupportLength);
                preSpanId++;
            }
        }
        return spanId;
    }

}


