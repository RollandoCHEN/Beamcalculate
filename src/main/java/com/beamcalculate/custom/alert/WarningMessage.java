package com.beamcalculate.custom.alert;

import com.beamcalculate.Main;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Set;

/**
 * Created by Ruolin on 29/10/2017 for Beamcalculate.
 */
public class WarningMessage {

    public WarningMessage(Set<String> messageInputSet, String messageBodyKey){
        if(!messageInputSet.isEmpty()) {
            ImageView warningGraphic = new ImageView("image/warning-icon_64x64.png");
            Image warningIcon = new Image("image/warning-icon_256x256.png");

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(Main.getBundleText("window.title.warning"));
            alert.setHeaderText(null);

            StringBuffer messageFromSet = new StringBuffer();
            messageInputSet.forEach (messageItem -> messageFromSet.append("\n- " + messageItem));

            String infoMessage = Main.getBundleText(messageBodyKey) + messageFromSet;
            alert.setContentText(infoMessage);
            alert.setGraphic(warningGraphic);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(warningIcon);
            alert.showAndWait();
            messageInputSet.clear();
        }
    }
}
