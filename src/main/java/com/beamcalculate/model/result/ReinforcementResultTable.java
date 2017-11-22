package com.beamcalculate.model.result;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;
import com.beamcalculate.enums.Pivots;
import com.beamcalculate.enums.ReinforcementParam;
import com.beamcalculate.model.calculator.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

import static com.beamcalculate.enums.NumericalFormat.FOUR_DECIMALS;
import static com.beamcalculate.enums.ReinforcementParam.b_MU;
import static com.beamcalculate.enums.ReinforcementParam.k_PIVOT;


public class ReinforcementResultTable {

    private final Geometry mGeometry;
    private Stage mResultTableStage;

    public ReinforcementResultTable(Reinforcement reinforcement) {
        mGeometry = reinforcement.getSpanMomentFunction().getInputs().getGeometry();

        HBox spanParamHBox = new HBox();
        spanParamHBox.getStyleClass().add("hbox");
        spanParamHBox.setAlignment(Pos.CENTER);

        spanParamHBox.getChildren().add(getParamNameVBox(reinforcement, "span_function"));
        spanParamHBox.getChildren().add(getParamValuesHBox(reinforcement, "span_function"));

        HBox supportParamHBox = new HBox();
        supportParamHBox.getStyleClass().add("hbox");
        supportParamHBox.setAlignment(Pos.CENTER);

        supportParamHBox.getChildren().add(getParamNameVBox(reinforcement, "support_moment"));
        supportParamHBox.getChildren().add(getParamValuesHBox(reinforcement, "support_moment"));

        StringBuilder tableTitle = new StringBuilder();
        tableTitle.append(getBundleText("label.momentCalculateMethod")).
                append(" : ").
                append(reinforcement.getSpanMomentFunction().getMethod());
        if (mGeometry.isOnTSection()){
            tableTitle.append(" (").
                    append(getBundleText("title.onTSection")).
                    append(")");
        }
        Label methodTitle = new Label(tableTitle.toString());
        methodTitle.getStyleClass().add("title");

        // Show the cross section diagram
        VBox container = new VBox();
        container.setPadding(new Insets(10,20,10,20));
        container.setSpacing(20);
        container.setAlignment(Pos.CENTER);

        container.getChildren().addAll(methodTitle, spanParamHBox, supportParamHBox);

        mResultTableStage = new Stage();
        mResultTableStage.setTitle(getBundleText("window.title.reinforcementTable"));
        mResultTableStage.getIcons().add(new Image("image/section_32x32.png"));


        double sceneWidth = mGeometry.getNumSpan() * 180 + 430;
        Scene scene = new Scene(container, sceneWidth, 880);
        scene.getStylesheets().add("/css/rebar_calculate_table.css");
        mResultTableStage.setScene(scene);
    }

    // TODO It's better to transfer these VBox, HBox to a GridPane
    private VBox getParamNameVBox(Reinforcement reinforcement, String string){
        VBox paramNameVBox = new VBox();
        paramNameVBox.getStyleClass().add("vbox");
        Label blank = new Label("");
        paramNameVBox.getChildren().add(blank);

        Map<Integer, Map<ReinforcementParam, Double>> reinforceParamMap;
        if (string.equals("span_function")){
            reinforceParamMap = reinforcement.getSpanReinforceParam();
        } else {
            reinforceParamMap = reinforcement.getSupportReinforceParam();
        }

        reinforceParamMap.get(1).forEach((param, value)->{
            if (param == b_MU){
                Label paramName = new Label(
                        param.getParaName() + param.getUnit(true) + " : "
                );
                Label pivotParam = new Label(
                        k_PIVOT.getParaName() + " : "
                );
                paramNameVBox.getChildren().addAll(paramName, pivotParam);
            } else {
                Label paramName = new Label(
                        param.getParaName() + param.getUnit(true) + " : "
                );
                paramNameVBox.getChildren().add(paramName);
            }

        });
        return paramNameVBox;
    }

    private HBox getParamValuesHBox(Reinforcement reinforcement, String spanOrSupport){
        HBox paramValuesHBox = new HBox();
        paramValuesHBox.getStyleClass().add("hbox");
        String sectionLabelString;

        Map<Integer, Map<ReinforcementParam, Double>> reinforceParamMap;
        Map<Integer, Pivots> pivotMap;
        if (spanOrSupport.equals("span_function")){
            reinforceParamMap = reinforcement.getSpanReinforceParam();
            pivotMap = reinforcement.getSpanPivotMap();
            sectionLabelString = getBundleText("label.span");
        } else {
            reinforceParamMap = reinforcement.getSupportReinforceParam();
            pivotMap = reinforcement.getSupportPivotMap();
            sectionLabelString = getBundleText("label.support");
        }

        reinforceParamMap.forEach((sectionId, paramValueMap)->{
            VBox paramValueVBox = new VBox();
            if (spanOrSupport.equals("support_moment") && sectionId != 1 && sectionId != mGeometry.getNumSupport()
                    || spanOrSupport.equals("span_function")){
                paramValueVBox.getStyleClass().add("vbox");
                Label crossSectionLabel = new Label(sectionLabelString + " " + sectionId);
                paramValueVBox.getChildren().add(crossSectionLabel);
                paramValueMap.forEach((param, value)->{
                    if (param == b_MU){
                        Label paramValue = new Label(
                                param.getSymbol() + " = " + FOUR_DECIMALS.format(value)
                        );
                        paramValue.getStyleClass().add("value");
                        Label pivotValue = new Label(
                                pivotMap.get(sectionId).getContent()
                        );
                        pivotValue.getStyleClass().add("value");
                        paramValueVBox.getChildren().addAll(paramValue, pivotValue);
                    } else {
                        Label paramValue = new Label(
                                param.getSymbol() + " = " + FOUR_DECIMALS.format(value)
                        );
                        paramValue.getStyleClass().add("value");
                        paramValueVBox.getChildren().add(paramValue);
                    }
                });
            }

            paramValuesHBox.getChildren().add(paramValueVBox);
        });
        return paramValuesHBox;
    }

    public Stage getStage() {
        return mResultTableStage;
    }

}
