package com.beamcalculate.model.result;

import com.beamcalculate.Main;
import com.beamcalculate.model.calculate.ELUCombination;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.span.AbstractSpanMoment;
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

import static com.beamcalculate.enums.UltimateCase.MAX;
import static com.beamcalculate.enums.UltimateCase.MIN;

public class RebarChart {

    public RebarChart(Rebar rebar) {
        AbstractSpanMoment spanMoment = rebar.getReinforcement().getSpanMomentFunction();
        ELUCombination combination = new ELUCombination(spanMoment);

        NumberAxis xAxis;
        NumberAxis yAxis;

        double maxMomentValue = -combination.getUltimateMomentValue(MAX);
        double minMomentValue = -combination.getUltimateMomentValue(MIN);

        xAxis = new NumberAxis(-1, Geometry.getTotalLength() + 1, 1);
        yAxis = new NumberAxis(1.2 * maxMomentValue, 1.2 * minMomentValue, 0.05);

        xAxis.setLabel(Main.getBundleText("label.abscissa") + " (" + Main.getBundleText("unit.length.m") + ")");
        yAxis.setLabel(Main.getBundleText("label.ordinate") + " (" + Main.getBundleText("unit.moment") + ")");

        XYChart.Series<Number, Number> maxSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> minSeries = new XYChart.Series<>();

        MomentLineChart.createMomentSeries(50, combination, MAX, maxSeries);
        MomentLineChart.createMomentSeries(50, combination, MIN, minSeries);

        //for all series, take date, each data has Node (symbol) for representing point
        for (XYChart.Data<Number, Number> data : maxSeries.getData()) {
            // this node is StackPane
            StackPane stackPane = (StackPane) data.getNode();
            stackPane.setVisible(false);
        }
        for (XYChart.Data<Number, Number> data : minSeries.getData()) {
            // this node is StackPane
            StackPane stackPane = (StackPane) data.getNode();
            stackPane.setVisible(false);
        }
        LineChart<Number, Number> lineChart = new LineChart(xAxis, yAxis);

        lineChart.getData().addAll(
                maxSeries, minSeries
        );



        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(lineChart);
        borderPane.setPadding(new Insets(20, 20, 20, 20));

//        set mCrossSectionStage and scene

        Stage rebarChartStage = new Stage();
        rebarChartStage.setTitle(Main.getBundleText("window.title.rebar"));
        rebarChartStage.getIcons().add(new Image("image/chart.png"));

        Scene scene = new Scene(borderPane, 1800, 800);
        rebarChartStage.setScene(scene);
        rebarChartStage.show();
    }
}
