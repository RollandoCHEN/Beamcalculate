package com.beamcalculate.model.result;

import com.beamcalculate.Main;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.span.AbstractSpanMoment;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Map;

public class RebarChart {

    public RebarChart(Rebar rebar) {
        AbstractSpanMoment spanMoment = rebar.getReinforcement().getSpanMomentFunction();

        NumberAxis xAxis = MomentLineChart.getxAxis();
        NumberAxis yAxis = MomentLineChart.getyAxis();

        Map<String, XYChart.Series> stringSeriesMap = MomentLineChart.getStringSeriesMap();

        LineChart<Number, Number> lineChart = new LineChart(xAxis, yAxis);

        lineChart.getData().addAll(
                stringSeriesMap.get(spanMoment.getMethod() + "_" + Main.getBundleText("label.max")),
                stringSeriesMap.get(spanMoment.getMethod() + "_" + Main.getBundleText("label.min"))
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
