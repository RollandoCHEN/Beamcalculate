package com.beamcalculate.model.custom_node;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.util.Objects;

/**
 * Created by Ruolin on 14/11/2017 for Beamcalculate.
 */
public class LineChartWithMarkers<X,Y> extends LineChart {

    private ObservableList<Data<X, Y>> horizontalMarkers;
    private ObservableList<Data<X, Y>> verticalMarkers;

    public LineChartWithMarkers(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        horizontalMarkers = FXCollections.observableArrayList(data -> new Observable[] {data.YValueProperty()});
        horizontalMarkers.addListener((InvalidationListener) observable -> layoutPlotChildren());
        verticalMarkers = FXCollections.observableArrayList(data -> new Observable[] {data.XValueProperty()});
        verticalMarkers.addListener((InvalidationListener)observable -> layoutPlotChildren());
    }

    public void addHorizontalValueMarker(Data<X, Y> marker) {
        Objects.requireNonNull(marker, "the marker must not be null");
        if (horizontalMarkers.contains(marker)) return;
        Line line = new Line();
        marker.setNode(line );
        line.getStyleClass().add("line");
        getPlotChildren().add(line);
        horizontalMarkers.add(marker);
    }

    public void removeHorizontalValueMarker(Data<X, Y> marker) {
        Objects.requireNonNull(marker, "the marker must not be null");
        if (marker.getNode() != null) {
            getPlotChildren().remove(marker.getNode());
            marker.setNode(null);
        }
        horizontalMarkers.remove(marker);
    }

    public void addVerticalValueMarker(Data<X, Y> marker, Node topNode, Node bottomNode) {
        Objects.requireNonNull(marker, "the marker must not be null");
        if (verticalMarkers.contains(marker)) return;
        Line line = new Line();

        AnchorPane anchorPane = new AnchorPane(topNode, bottomNode);
        anchorPane.setMouseTransparent(true);

        StackPane stackPane = new StackPane(line, anchorPane);
        stackPane.setMouseTransparent(true);

        line.getStyleClass().add("line");
        marker.setNode(stackPane);
        getPlotChildren().add(stackPane);
        verticalMarkers.add(marker);
    }

    public void removeVerticalValueMarker(Data<X, Y> marker) {
        Objects.requireNonNull(marker, "the marker must not be null");
        if (marker.getNode() != null) {
            getPlotChildren().remove(marker.getNode());
            marker.setNode(null);
        }
        verticalMarkers.remove(marker);
    }


    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        for (Data<X, Y> horizontalMarker : horizontalMarkers) {
            Line line = (Line) horizontalMarker.getNode();
            line.setStartX(0);
            line.setEndX(getBoundsInLocal().getWidth());
            line.setStartY(getYAxis().getDisplayPosition(horizontalMarker.getYValue()) + 0.5); // 0.5 for crispness
            line.setEndY(line.getStartY());
            line.toFront();
        }
        for (Data<X, Y> verticalMarker : verticalMarkers) {
            StackPane stackPane = (StackPane) verticalMarker.getNode();
            stackPane.setLayoutX(getXAxis().getDisplayPosition(verticalMarker.getXValue()) + 0.5);      // 0.5 for crispness
            stackPane.setLayoutY(getYAxis().getDisplayPosition(verticalMarker.getYValue()));

            Line line = (Line)stackPane.getChildren().get(0);
            line.setStartY(0);
            line.setStartY(getBoundsInLocal().getHeight());

            AnchorPane anchorPane = (AnchorPane)stackPane.getChildren().get(1);
            anchorPane.setMinHeight(getBoundsInLocal().getHeight());
            AnchorPane.setTopAnchor(anchorPane.getChildren().get(0), 0.11 * getBoundsInLocal().getHeight());
            AnchorPane.setBottomAnchor(anchorPane.getChildren().get(1), 0.13 * getBoundsInLocal().getHeight());

            stackPane.toFront();
        }
    }

}

