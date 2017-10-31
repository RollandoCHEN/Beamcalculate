package com.beamcalculate.custom.alert;

import com.beamcalculate.BeamCalculatorApp;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Created by Ruolin on 29/10/2017 for Beamcalculate.
 */
public class InfoMessage {

    public InfoMessage(String titleKey, String headKey, String messageBodyKey){
        ImageView infoGraphic = new ImageView("image/info-icon_64x64.png");
        Image infoIcon = new Image("image/info-icon_256x256.png");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(BeamCalculatorApp.getBundleText(titleKey));
        alert.setHeaderText(BeamCalculatorApp.getBundleText(headKey));
        alert.setContentText(BeamCalculatorApp.getBundleText(messageBodyKey));
        alert.setGraphic(infoGraphic);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(infoIcon);
        alert.showAndWait();
    }
}
