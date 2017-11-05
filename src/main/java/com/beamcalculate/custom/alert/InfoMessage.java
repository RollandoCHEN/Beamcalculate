package com.beamcalculate.custom.alert;

import com.beamcalculate.BeamCalculatorApp;
import com.beamcalculate.model.LanguageManager;
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
        Image infoIcon = new Image("image/info-icon_64x64.png");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LanguageManager.getBundleText(titleKey));
        alert.setHeaderText(LanguageManager.getBundleText(headKey));
        alert.setContentText(LanguageManager.getBundleText(messageBodyKey));
        alert.setGraphic(infoGraphic);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(infoIcon);
        alert.showAndWait();
    }
}
