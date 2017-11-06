package com.beamcalculate.model.page_manager;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

import com.beamcalculate.model.calculate.ELUCombination;
import com.beamcalculate.model.calculate.span_function.AbstractSpanMoment;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.enums.UltimateCase;
import com.beamcalculate.model.custom_node.HoveredThresholdNode;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT;
import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.UltimateCase.MAX;
import static com.beamcalculate.enums.UltimateCase.MIN;

public class MomentLineChartTreater {

    public static List<NumberAxis> defineAxis(AbstractSpanMoment spanMomentFunction) {
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

    public static void createMomentSeries(
            int numSection,
            AbstractSpanMoment spanMomentFunction, UltimateCase ultimateCase,
            XYChart.Series<Number, Number> series
    ) {
        Geometry geometry = spanMomentFunction.getInputs().getGeometry();
        ELUCombination eluCombination = new ELUCombination(spanMomentFunction);
        for (int spanId = 1; spanId < geometry.getNumSpan() + 1; spanId++) {

            double spanLength = eluCombination.getSpanMomentFunction().getCalculateSpanLengthMap().get(spanId);
            double spanLocalX = 0;

            String calculateMethod = eluCombination.getSpanMomentFunction().getMethod();
            double globalX = getGlobalX(spanId, spanLocalX, calculateMethod, geometry);

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
        series.setName(getBundleText("label." + ultimateCase.toString().toLowerCase()) + " - " + eluCombination.getSpanMomentFunction().getMethod());
    }

    public static void createRedistributionMomentSeries(
            int numSection,
            SpanMomentFunction_SpecialLoadCase spanMomentFunction, UltimateCase ultimateCase,
            XYChart.Series series
    ) {
        Geometry geometry = spanMomentFunction.getGeometry();
        spanMomentFunction.getSpanMomentFunctionMap().forEach((spanId, loadCaseMomentFunctionMap) -> {
            double spanLength = spanMomentFunction.getGeometry().getEffectiveSpansLengthMap().get(spanId);
            double spanLocalX = 0;
            double globalX = getGlobalX(spanId, spanLocalX, TROIS_MOMENT.getMethodName(), geometry);

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
        series.setName(getBundleText("label."
                + ultimateCase.toString().toLowerCase())
                + " - "
                + TROIS_MOMENT_R.getMethodName());
    }

    public static double getGlobalX(int spanId, double spanLocalX, String method, Geometry geometry) {
        double globalX = spanLocalX;
        if (TROIS_MOMENT.getMethodName().equals(method)
                || TROIS_MOMENT_R.getMethodName().equals(method)) {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preX;
                if (preSpanId == 0) {
                    preX = geometry.supportWidthMap().get(1) / 2;
                } else {
                    preX = geometry.getEffectiveSpansLengthMap().get(preSpanId);
                }
                globalX += preX;
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
    public static double getSpanLocalX(int spanId, double globalX, String method, Geometry geometry) {
        double spanLocalX = globalX;
        if (TROIS_MOMENT.getMethodName().equals(method)) {
            for (int preSpanId = 0; preSpanId < spanId; preSpanId++) {
                double preX;
                if (preSpanId == 0) {
                    preX = geometry.supportWidthMap().get(1) / 2;
                } else {
                    preX = geometry.getEffectiveSpansLengthMap().get(preSpanId);
                }
                spanLocalX -= preX;
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
                spanLocalX -= (preSpanLength + preSupportLength);
            }
        }
        return spanLocalX;
    }

}


