package com.beamcalculate.controllers;

import com.beamcalculate.enums.DeflectionParam;
import com.beamcalculate.model.calculator.Deflection;
import com.beamcalculate.model.page_manager.PageScaleHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static com.beamcalculate.enums.NumericalFormat.TWO_DECIMALS;
import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

/**
 * Created by rchen on 21/11/2017.
 */
public class DeflectionPageController implements Initializable {
    @FXML ScrollPane scrollContainer;
    @FXML GridPane gridPane;
    @FXML AnchorPane deflectionPageAnchorPane;

    private final double PAGE_MIN_HEIGHT = 510;
    private final double PAGE_MIN_WIDTH = 720;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PageScaleHandler scaleHandler = new PageScaleHandler();
        scaleHandler.AddScaleListener(scrollContainer, deflectionPageAnchorPane, PAGE_MIN_HEIGHT, PAGE_MIN_WIDTH);

    }

    public void createDeflectionPage(Deflection deflection) {
        Map<Integer, Map<DeflectionParam, Double>> deflectionParam = deflection.getSpanDeflectionParam();
        gridPane.getChildren().clear();

        Label selectedRebarLabel = new Label(
                getBundleText("result.deflection.paraName.selected_rebar") + " : "
        );
        selectedRebarLabel.getStyleClass().add("header");
        gridPane.add(selectedRebarLabel, 0, 1);


        int rowNum = 2;
        for (Map.Entry<DeflectionParam, Double> entry : deflectionParam.get(1).entrySet()){
            DeflectionParam param = entry.getKey();
            Label paramNameLabel = new Label(
                    param.getParaName() + param.getUnit(true) + " : "
            );
            paramNameLabel.getStyleClass().add("header");
            gridPane.add(paramNameLabel, 0, rowNum);

            rowNum++;
        }

        Label statusLabel = new Label(
                getBundleText("result.deflection.paraName.status") + " : "
        );
        statusLabel.getStyleClass().add("header");
        gridPane.add(statusLabel, 0, rowNum);

        deflectionParam.forEach((spanNo, paramValueMap) -> {
            Label headerLabel = new Label(getBundleText("label.span") + " " + spanNo);
            headerLabel.getStyleClass().add("header");
            gridPane.add(headerLabel, spanNo,0);

            Label rebarCaseLabel = new Label(deflection.getRebarSelectionList().get(spanNo - 1).toString());
            gridPane.add(rebarCaseLabel, spanNo, 1);

            int rowNum2 = 2;
            for (Map.Entry<DeflectionParam, Double> entry : paramValueMap.entrySet()){
                DeflectionParam param = entry.getKey();
                double paramValue = entry.getValue();

                Label paramValueLabel = new Label(
                        param.getSymbol() + " = " + TWO_DECIMALS.format(paramValue)
                );
                gridPane.add(paramValueLabel, spanNo, rowNum2);

                rowNum2++;
            }

            if (paramValueMap.get(DeflectionParam.d_L_D) <=
                    paramValueMap.get(DeflectionParam.c_LIMIT_L_D)){
                Label statusValue = new Label(
                        getBundleText("result.deflection.status_pass")
                );
                statusValue.getStyleClass().add("pass");
                gridPane.add(statusValue, spanNo, rowNum2);
            } else {
                Label statusValue = new Label(
                        getBundleText("result.deflection.status_fail")
                );
                statusValue.getStyleClass().add("fail");
                gridPane.add(statusValue, spanNo, rowNum2);
            }
        });

        gridPane.getChildren().forEach(node -> {
            if(GridPane.getColumnIndex(node) != 0){
                GridPane.setHalignment(node, HPos.CENTER);
            }
        });
    }

    public AnchorPane getAnchorPane() {
        return deflectionPageAnchorPane;
    }
}
