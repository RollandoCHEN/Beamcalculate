package com.beamcalculate.controllers;

import com.beamcalculate.enums.DeflectionParam;
import com.beamcalculate.model.calculator.Deflection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static com.beamcalculate.enums.NumericalFormat.FOUR_DECIMALS;
import static com.beamcalculate.enums.NumericalFormat.TWO_DECIMALS;
import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

/**
 * Created by rchen on 21/11/2017.
 */
public class DeflectionPageController implements Initializable {

    @FXML GridPane gridPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void createDeflectionPage(Deflection deflection) {
        Map<Integer, Map<DeflectionParam, Double>> deflectionParam = deflection.getSpanDeflectionParam();
        deflectionParam.forEach((spanNo, paramValueMap) -> {
            Label headerLabel = new Label(getBundleText("label.span") + " " + spanNo);
            headerLabel.getStyleClass().add("header");
            gridPane.add(headerLabel, spanNo,0);

            int rowNum = 1;
            for (Map.Entry<DeflectionParam, Double> entry : paramValueMap.entrySet()){
                DeflectionParam param = entry.getKey();
                double paramValue = entry.getValue();

                Label paramNameLabel = new Label(
                        param.getParaName() + param.getUnit(true) + " : "
                );
                paramNameLabel.getStyleClass().add("header");
                gridPane.add(paramNameLabel, 0, rowNum);

                Label paramValueLabel = new Label(
                        param.getSymbol() + " = " + TWO_DECIMALS.format(paramValue)
                );
                gridPane.add(paramValueLabel, spanNo, rowNum);

                rowNum++;
            }

        });

        gridPane.getChildren().forEach(node -> {
            if(GridPane.getColumnIndex(node) != 0){
                GridPane.setHalignment(node, HPos.CENTER);
            }
        });
    }
}
