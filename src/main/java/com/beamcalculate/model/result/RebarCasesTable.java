package com.beamcalculate.model.result;

import com.beamcalculate.Main;
import com.beamcalculate.controllers.MainController;
import com.beamcalculate.controllers.TSectionController;
import com.beamcalculate.enums.MyMethods;
import com.beamcalculate.enums.RebarType;
import com.beamcalculate.model.RebarType_Number;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.beamcalculate.enums.NumericalFormat.TWODECIMALS;
import static com.beamcalculate.enums.ReinforcementParam.j_A_S;

public class RebarCasesTable {
    private Rebar mRebar;

    public RebarCasesTable(Rebar rebar) {
        mRebar = rebar;
        Reinforcement reinforcement = rebar.getReinforcement();

        GridPane spanGridPane = new GridPane();
        spanGridPane.setAlignment(Pos.CENTER);
        spanGridPane.setHgap(20);
        spanGridPane.setVgap(15);
        GridPane supportGridPane = new GridPane();
        supportGridPane.setAlignment(Pos.CENTER);
        supportGridPane.setHgap(10);
        supportGridPane.setVgap(10);

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

        int maxNumOfCases = 1;
        for (int spanId=1; spanId < Geometry.getNumSpan()+1; spanId++) {
            int rebarCases = rebar.getRebarCasesListOfSpan(spanId).size();
            maxNumOfCases = Math.max(rebarCases, maxNumOfCases);
        }

        for (int caseNum = 1; caseNum < maxNumOfCases+1; caseNum++){
            Label caseLabel = new Label(Main.getBundleText("label.case") + " " + caseNum);
            spanGridPane.add(caseLabel, 0, caseNum);
        }

        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++) {
            Label spanIdLabel = new Label(Main.getBundleText("label.span") + " " + spanId);
            spanIdLabel.setStyle("-fx-font-size:14px; -fx-font-weight: bold;");

            double calculatedArea = reinforcement.getSpanReinforceParam().get(spanId).get(j_A_S);
            Label calculatedAreaLabel = new Label(
                    j_A_S.getSymbol() + " = " + TWODECIMALS.getDecimalFormat().format(calculatedArea) + " " + Main.getBundleText("unit.area.cm2")
            );
            calculatedAreaLabel.setStyle("-fx-font-style: italic; -fx-font-weight: bold;");

            VBox spanVBox = new VBox(spanIdLabel, calculatedAreaLabel);

            int columnNum = spanId;
            spanGridPane.add(spanVBox, columnNum, 0);

            List<Map<Integer, RebarType_Number>> rebarCasesList = rebar.getRebarCasesListOfSpan(spanId);
            int caseVariable;
            double minRebarArea = rebar.getTotalRebarAreaListOfSpan(spanId).get(0);

            for (caseVariable = 0; caseVariable < rebarCasesList.size(); caseVariable++){
                int caseNum = caseVariable;
                minRebarArea = Math.min(rebar.getTotalRebarAreaListOfSpan(spanId).get(caseNum), minRebarArea);
            }

            for (caseVariable = 0; caseVariable < rebarCasesList.size(); caseVariable++){
                int caseNum = caseVariable;
                Button rebarCaseButton = new Button();
                StringBuilder buttonString = new StringBuilder();
                rebarCasesList.get(caseVariable).forEach((layerNum, rebarType_number) -> {
                    if (layerNum != 1){
                        buttonString.append("\n");
                    }
                    RebarType rebarType = rebarType_number.getRebarType();
                    int number = rebarType_number.getNumberOfRebar();
                    buttonString.append(MyMethods.getOrdinalNumber(layerNum)).append(Main.getBundleText("label.steelRebarLayer")).append(" : ").append(number).append(rebarType.name());
                });
                rebarCaseButton.setText(buttonString.toString());

                double rebarArea = rebar.getTotalRebarAreaListOfSpan(spanId).get(caseNum);
                Label rebarAreaLabel = new Label(
                        j_A_S.getSymbol() + " = " + TWODECIMALS.getDecimalFormat().format(rebarArea) + " " + Main.getBundleText("unit.area.cm2")
                );
                rebarAreaLabel.setStyle("-fx-font-style: italic");
                if (rebarArea == minRebarArea){
                    rebarAreaLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }

                VBox vBox = new VBox(rebarCaseButton, rebarAreaLabel);
                spanGridPane.add(vBox, columnNum, caseNum + 1);

                rebarCaseButton.setOnAction(event -> {
                    RebarCutChart rebarCutChart = new RebarCutChart(getRebar(), columnNum, caseNum);
                });
            }
        }

        VBox contentVBox = new VBox();
        contentVBox.setPadding(new Insets(10,20,10,20));
        contentVBox.setSpacing(20);
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.getChildren().addAll(spanGridPane, supportGridPane);

        Button calculateDetailButton = new Button(Main.getBundleText("button.rebarCalculateTable"));
        calculateDetailButton.setStyle("-fx-font-size:16px");
        calculateDetailButton.setOnAction(event -> {

            ReinforcementResultTable reinforcementResult = new ReinforcementResultTable(reinforcement);
            reinforcementResult.showStage();

        });

        VBox topVBox = new VBox(methodTitle);
        topVBox.setPadding(new Insets(30, 30, 30, 20));
        topVBox.setAlignment(Pos.CENTER);

        HBox bottomHBox = new HBox(calculateDetailButton);
        bottomHBox.setSpacing(15);
        bottomHBox.setPadding(new Insets(10,20,10,20));
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);

        BorderPane rebarSelectionBorderPane = new BorderPane();
        rebarSelectionBorderPane.setTop(topVBox);
        rebarSelectionBorderPane.setCenter(contentVBox);
        rebarSelectionBorderPane.setBottom(bottomHBox);

        Pane crossSectionPane = new Pane();
        try {
            crossSectionPane = FXMLLoader.load(
                    getClass().getResource("/fxml/section.fxml"),
                    Main.getResourceBundle());

                /*I wander if it's better to pass the reinforcement instance to the fxml controller
                * as like:
                * TSectionController controller = fxmlLoader.<TSectionController>getController();
                * controller.setReinforcement(reinforcement);*/
            crossSectionPane.setPrefWidth(Math.max(150 * Geometry.getNumSpan(), TSectionController.getMaxSchemaWidth()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridPane leftGridPane = new GridPane();
        leftGridPane.add(crossSectionPane, 0, 0);
        double leftGridPaneWidth = Math.max(150 * Geometry.getNumSpan(), TSectionController.getMaxSchemaWidth());
        leftGridPane.setPrefWidth(leftGridPaneWidth);
        leftGridPane.setAlignment(Pos.CENTER);

//        VBox rightVBox = new VBox(rebarSelectionBorderPane);
//        double rightVBoxWidth = Geometry.getNumSpan() * 130 + 230;
//        rightVBox.setPrefWidth(rightVBoxWidth);
//        rightVBox.setAlignment(Pos.TOP_RIGHT);

        GridPane rightGridPane = new GridPane();
        rightGridPane.add(rebarSelectionBorderPane, 0, 0);
        double rightGridPaneWidth = Geometry.getNumSpan() * 130 + 250;
        rightGridPane.setPrefWidth(rightGridPaneWidth);
        rightGridPane.setAlignment(Pos.TOP_RIGHT);

        GridPane containerGridPane = new GridPane();
        containerGridPane.setAlignment(Pos.CENTER);
        containerGridPane.add(leftGridPane, 0,0);
        containerGridPane.add(rightGridPane, 1,0);
        containerGridPane.setPadding(new Insets(20, 30, 20, 20));

        Stage resultStage = new Stage();
        resultStage.setTitle(Main.getBundleText("window.title.rebarChoices"));
        resultStage.getIcons().add(new Image("image/reinforcement.png"));

        double sceneWidth = leftGridPaneWidth + rightGridPaneWidth;

        double sceneHeight = Math.max(maxNumOfCases * 110 + 100, 600);
        Scene scene = new Scene(containerGridPane, sceneWidth, sceneHeight);
        resultStage.setScene(scene);
        resultStage.setResizable(false);
        resultStage.show();
    }

    public Rebar getRebar() {
        return mRebar;
    }

    public void setRebar(Rebar rebar) {
        mRebar = rebar;
    }
}
