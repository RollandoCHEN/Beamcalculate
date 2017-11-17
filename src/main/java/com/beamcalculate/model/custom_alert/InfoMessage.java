package com.beamcalculate.model.custom_alert;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

/**
 * Created by Ruolin on 29/10/2017 for Beamcalculate.
 */
public class InfoMessage {

    public InfoMessage(String titleKey, String headKey, String messageBodyKey){
        ImageView infoGraphic = new ImageView("image/info-icon_64x64.png");
        Image infoIcon = new Image("image/info-icon_64x64.png");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(getBundleText(titleKey));
        alert.setHeaderText(getBundleText(headKey));
        alert.setContentText(getBundleText(messageBodyKey));
        alert.setGraphic(infoGraphic);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(infoIcon);
        Scene scene = alert.getDialogPane().getScene();
        scene.getStylesheets().add("/css/alert_window.css");
        alert.showAndWait();
    }
}
