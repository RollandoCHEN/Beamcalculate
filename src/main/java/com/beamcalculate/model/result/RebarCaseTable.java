package com.beamcalculate.model.result;

import com.beamcalculate.Main;
import com.beamcalculate.controllers.MainController;
import com.beamcalculate.enums.RebarType;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

import static com.beamcalculate.enums.NumericalFormat.TWODECIMALS;
import static com.beamcalculate.enums.ReinforcementParam.j_A_S;

public class RebarCaseTable {
    private Rebar mRebar;

    public RebarCaseTable(Rebar rebar) {
        mRebar = rebar;
        Reinforcement reinforcement = rebar.getReinforcement();

        GridPane spanGridPane = new GridPane();
        spanGridPane.setAlignment(Pos.CENTER);
        spanGridPane.setHgap(10);
        spanGridPane.setVgap(10);
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
            Label caseLabel = new Label("Case " + caseNum);
            spanGridPane.add(caseLabel, 0, caseNum);
        }

        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++) {
            Label spanIdLabel = new Label(Main.getBundleText("label.span") + " " + spanId);
            spanIdLabel.setStyle("-fx-font-size:14px; -fx-font-weight: bold;");

            double calculatedArea = reinforcement.getSpanReinforceParam().get(spanId).get(j_A_S);
            Label calculatedAreaLabel = new Label(
                    j_A_S.getSymbol() + " = " + TWODECIMALS.getDecimalFormat().format(calculatedArea)
            );
            calculatedAreaLabel.setStyle("-fx-font-style: italic");

            VBox spanVBox = new VBox(spanIdLabel, calculatedAreaLabel);

            int columnNum = spanId;
            spanGridPane.add(spanVBox, columnNum, 0);

            List<Map<Integer, Map<RebarType, Integer>>> rebarCasesList = rebar.getRebarCasesListOfSpan(spanId);
            int caseVariable;
            for (caseVariable = 0; caseVariable < rebarCasesList.size(); caseVariable++){
                int caseNum = caseVariable;
                Button rebarCaseButton = new Button();
                StringBuilder buttonString = new StringBuilder();
                rebarCasesList.get(caseVariable).forEach((layerNum, rebarType_number_map) -> {
                    if (layerNum != 1){
                        buttonString.append("\n");
                    }
                    rebarType_number_map.forEach((rebarType, number) ->
                            buttonString.append("Layer ").append(layerNum).append(" : ")
                                    .append(number).append(rebarType.name()));
                });
                rebarCaseButton.setText(buttonString.toString());

                double rebarArea = rebar.getTotalRebarAreaListOfSpan(spanId).get(caseNum);
                Label rebarAreaLabel = new Label(
                        j_A_S.getSymbol() + " = " + TWODECIMALS.getDecimalFormat().format(rebarArea)
                );
                rebarAreaLabel.setStyle("-fx-font-style: italic");

                VBox vBox = new VBox(rebarCaseButton, rebarAreaLabel);
                spanGridPane.add(vBox, columnNum, caseNum + 1);

                rebarCaseButton.setOnAction(event -> {
                    RebarChart rebarChart = new RebarChart(getRebar(), columnNum, caseNum);
                });
            }
        }

        VBox container = new VBox();
        container.setPadding(new Insets(10,20,10,20));
        container.setSpacing(20);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(methodTitle, spanGridPane, supportGridPane);

        Stage resultStage = new Stage();
        resultStage.setTitle(Main.getBundleText("window.title.result"));
        resultStage.getIcons().add(new Image("image/reinforcement.png"));

        double sceneWidth = Geometry.getNumSpan() * 100 + 300;
        Scene scene = new Scene(container, sceneWidth, 600);
        resultStage.setScene(scene);
        resultStage.show();
    }

    public Rebar getRebar() {
        return mRebar;
    }

    public void setRebar(Rebar rebar) {
        mRebar = rebar;
    }
}
