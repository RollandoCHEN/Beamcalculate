package com.beamcalculate.model.result;

import com.beamcalculate.Main;
import com.beamcalculate.controllers.MainController;
import com.beamcalculate.enums.Pivots;
import com.beamcalculate.enums.ReinforcementParam;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

import static com.beamcalculate.enums.NumericalFormat.FOURDECIMALS;
import static com.beamcalculate.enums.ReinforcementParam.b_MU;
import static com.beamcalculate.enums.ReinforcementParam.k_PIVOT;


public class ReinforcementResultTable {

    private Stage mResultTableStage;

    public ReinforcementResultTable(Reinforcement reinforcement) {

        HBox spanParamHBox = new HBox();
        spanParamHBox.setSpacing(20);
        spanParamHBox.setAlignment(Pos.CENTER);

        spanParamHBox.getChildren().add(getParamNameVBox(reinforcement, "span_function"));
        spanParamHBox.getChildren().add(getParamValuesHBox(reinforcement, "span_function"));

        HBox supportParamHBox = new HBox();
        supportParamHBox.setSpacing(20);
        supportParamHBox.setAlignment(Pos.CENTER);

        supportParamHBox.getChildren().add(getParamNameVBox(reinforcement, "support_moment"));
        supportParamHBox.getChildren().add(getParamValuesHBox(reinforcement, "support_moment"));

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

        // Show the cross section diagram
        VBox containerVBox = new VBox();
        containerVBox.setPadding(new Insets(10,20,10,20));
        containerVBox.setSpacing(20);
        containerVBox.setAlignment(Pos.CENTER);

        containerVBox.getChildren().addAll(methodTitle, spanParamHBox, supportParamHBox);

        ImageView love = new ImageView("image/love-icon_16x16.png");
        love.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Main.getBundleText("info.title.love"));
            alert.setHeaderText(null);
            alert.setContentText(Main.getBundleText("info.content.love"));
            alert.setGraphic(new ImageView("image/my_love.png"));
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("image/love.png"));
            alert.showAndWait();
        });
        HBox loveBox = new HBox(love);
        loveBox.setAlignment(Pos.BOTTOM_RIGHT);
        loveBox.setPadding(new Insets(0, 5, 5, 0));
        StackPane container = new StackPane(containerVBox, loveBox);

        mResultTableStage = new Stage();
        mResultTableStage.setTitle(Main.getBundleText("window.title.reinforcementTable"));
        mResultTableStage.getIcons().add(new Image("image/reinforcement.png"));

        double sceneWidth = Geometry.getNumSpan() * 180 + 430;
        Scene scene = new Scene(container, sceneWidth, 880);
        mResultTableStage.setScene(scene);
    }

    // TODO It's better to transfer these VBox, HBox to a GridPane
    private VBox getParamNameVBox(Reinforcement reinforcement, String string){
        VBox paramNameVBox = new VBox();
        paramNameVBox.setSpacing(15);
        Label blank = new Label("");
        blank.setStyle("-fx-font-size:14px; -fx-font-weight: bold;");
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
                        param.getParaName() + getBracketedUnit(param) + " : "
                );
                Label pivotParam = new Label(
                        k_PIVOT.getParaName() + " : "
                );
                paramNameVBox.getChildren().addAll(paramName, pivotParam);
            } else {
                Label paramName = new Label(
                        param.getParaName() + getBracketedUnit(param) + " : "
                );
                paramNameVBox.getChildren().add(paramName);
            }

        });
        return paramNameVBox;
    }

    private String getBracketedUnit(ReinforcementParam param) {
        if (param.getUnit().equals("")){
            return "";
        }else {
            return " (" + param.getUnit() + ")";
        }
    }

    private HBox getParamValuesHBox(Reinforcement reinforcement, String spanOrSupport){
        HBox paramValuesHBox = new HBox();
        paramValuesHBox.setSpacing(30);
        String sectionLabelString;

        Map<Integer, Map<ReinforcementParam, Double>> reinforceParamMap;
        Map<Integer, Pivots> pivotMap;
        if (spanOrSupport.equals("span_function")){
            reinforceParamMap = reinforcement.getSpanReinforceParam();
            pivotMap = reinforcement.getSpanPivotMap();
            sectionLabelString = Main.getBundleText("label.span");
        } else {
            reinforceParamMap = reinforcement.getSupportReinforceParam();
            pivotMap = reinforcement.getSupportPivotMap();
            sectionLabelString = Main.getBundleText("label.support");
        }

        reinforceParamMap.forEach((sectionId, paramValueMap)->{
            VBox paramValueVBox = new VBox();
            if (spanOrSupport.equals("support_moment") && sectionId != 1 && sectionId != Geometry.getNumSupport()
                    || spanOrSupport.equals("span_function")){
                paramValueVBox.setSpacing(15);
                Label crossSectionLabel = new Label(sectionLabelString + " " + sectionId);
                crossSectionLabel.setStyle("-fx-font-size:14px; -fx-font-weight: bold;");
                paramValueVBox.getChildren().add(crossSectionLabel);
                paramValueMap.forEach((param, value)->{
                    if (param == b_MU){
                        Label paramValue = new Label(
                                param.getSymbol() + " = " + FOURDECIMALS.getDecimalFormat().format(value)
                        );
                        Label pivotValue = new Label(
                                pivotMap.get(sectionId).getContent()
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

    public Stage getStage() {
        return mResultTableStage;
    }
}
