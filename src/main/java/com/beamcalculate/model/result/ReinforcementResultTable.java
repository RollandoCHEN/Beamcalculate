package com.beamcalculate.model.result;

import com.beamcalculate.Main;
import com.beamcalculate.controllers.MainController;
import com.beamcalculate.enums.ReinforcementParam;
import com.beamcalculate.model.calculate.Reinforcement;
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

import static com.beamcalculate.enums.NumericalFormat.FOURDECIMALS;
import static com.beamcalculate.enums.ReinforcementParam.b_MU;
import static com.beamcalculate.enums.ReinforcementParam.k_PIVOT;


public class ReinforcementResultTable {

    public ReinforcementResultTable(Reinforcement reinforcement) {

        HBox spanParamHBox = new HBox();
        spanParamHBox.setSpacing(20);
        spanParamHBox.setAlignment(Pos.CENTER);

        spanParamHBox.getChildren().add(getParamNameVBox(reinforcement, "span"));
        spanParamHBox.getChildren().add(getParamValuesHBox(reinforcement, "span"));

        HBox supportParamHBox = new HBox();
        supportParamHBox.setSpacing(20);
        supportParamHBox.setAlignment(Pos.CENTER);

        supportParamHBox.getChildren().add(getParamNameVBox(reinforcement, "support"));
        supportParamHBox.getChildren().add(getParamValuesHBox(reinforcement, "support"));

        StringBuilder tableTitle = new StringBuilder();
        tableTitle.append(
                Main.getBundleText("label.momentCalculateMethod") +
                " : " +
                reinforcement.getSpanMomentFunction().getMethod()
        );
        if (MainController.isOnTSection()){
            tableTitle.append(
                    " (" + Main.getBundleText("title.onTSection") + ")"
            );
        }
        Label methodTitle = new Label(tableTitle.toString());
        methodTitle.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");

        // TODO Add a button to the reinforcement result window to show the cross section diagram

        VBox container = new VBox();
        container.setPadding(new Insets(20,20,20,20));
        container.setSpacing(20);
        container.setAlignment(Pos.CENTER);

        container.getChildren().addAll(methodTitle, spanParamHBox, supportParamHBox);

        Stage resultStage = new Stage();
        resultStage.setTitle(Main.getBundleText("window.title.result"));
        resultStage.getIcons().add(new Image("image/reinforcement.png"));

        Scene scene = new Scene(container, 1000, 900);
        resultStage.setScene(scene);
        resultStage.show();
    }

    private VBox getParamNameVBox(Reinforcement reinforcement, String string){
        VBox paramNameVBox = new VBox();
        paramNameVBox.setSpacing(15);
        Label blank = new Label("");
        blank.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
        paramNameVBox.getChildren().add(blank);

        Map<Integer, Map<ReinforcementParam, Double>> reinforceParamMap;
        if (string.equals("span")){
            reinforceParamMap = reinforcement.getSpanReinforceParam();
        } else {
            reinforceParamMap = reinforcement.getSupportReinforceParam();
        }

        reinforceParamMap.get(1).forEach((param, value)->{
            if (param == b_MU){
                Label paramName = new Label(
                        param.getParaNameBundleKey() + " " + param.getUnit() + " : "
                );
                Label pivotParam = new Label(
                        k_PIVOT.getParaNameBundleKey() + " : "
                );
                paramNameVBox.getChildren().addAll(paramName, pivotParam);
            } else {
                Label paramName = new Label(
                        param.getParaNameBundleKey() + " " + param.getUnit() + " : "
                );
                paramNameVBox.getChildren().add(paramName);
            }

        });
        return paramNameVBox;
    }

    private HBox getParamValuesHBox(Reinforcement reinforcement, String spanOrSupport){
        HBox paramValuesHBox = new HBox();
        paramValuesHBox.setSpacing(20);
        String sectionLabelString;

        Map<Integer, Map<ReinforcementParam, Double>> reinforceParamMap;
        if (spanOrSupport.equals("span")){
            reinforceParamMap = reinforcement.getSpanReinforceParam();
            sectionLabelString = Main.getBundleText("label.span");
        } else {
            reinforceParamMap = reinforcement.getSupportReinforceParam();
            sectionLabelString = Main.getBundleText("label.support");
        }

        reinforceParamMap.forEach((sectionId, paramValueMap)->{
            VBox paramValueVBox = new VBox();
            if (spanOrSupport.equals("support") && sectionId != 1 && sectionId != Geometry.getNumSupport()
                    || spanOrSupport.equals("span")){
                paramValueVBox.setSpacing(15);
                Label sectionLabel = new Label(sectionLabelString + " " + sectionId.toString());
                sectionLabel.setStyle("-fx-font-size:14px; -fx-font-weight: bold;");
                paramValueVBox.getChildren().add(sectionLabel);
                paramValueMap.forEach((param, value)->{
                    if (param == b_MU){
                        Label paramValue = new Label(
                                param.getSymbol() + " = " + FOURDECIMALS.getDecimalFormat().format(value)
                        );
                        Label pivotValue = new Label(
                                reinforcement.getPivotMap().get(sectionId).getContent()
                        );
                        paramValueVBox.getChildren().addAll(paramValue, pivotValue);
                    } else {
                        Label paramValue = new Label(
                                param.getSymbol() + " = " + FOURDECIMALS.getDecimalFormat().format(value)
                        );
                        paramValueVBox.getChildren().add(paramValue);
                    }
                });
            }

            paramValuesHBox.getChildren().add(paramValueVBox);
        });
        return paramValuesHBox;
    }
}
