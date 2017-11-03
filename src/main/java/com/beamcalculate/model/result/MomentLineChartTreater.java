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

public class MomentLineChartTreater {

    private static Geometry mGeometry;

    public MomentLineChartTreater(SpanMomentFunction spanMomentFunction) {
        mGeometry = spanMomentFunction.getGeometry();
    }

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

        NumberAxis xAxis = new NumberAxis(-1, mGeometry.getTotalLength() + 1, 1);
        NumberAxis yAxis = new NumberAxis(-1.2 * maxSpanMomentValue, -1.2 * maxSupportMomentValue, 0.05);

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

    public static void createRedistributionMomentSeries(
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

    public static double getGlobalX(int spanId, double spanLocalX, String method) {
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
    public static double getSpanLocalX(int spanId, double globalX, String method) {
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

}


