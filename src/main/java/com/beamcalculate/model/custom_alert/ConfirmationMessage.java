package com.beamcalculate.model.custom_alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

/**
 * Created by Ruolin on 02/11/2017 for Beamcalculate.
 */
public class ConfirmationMessage {
    private Optional<ButtonType> mResult;
    private ButtonType mButtonTypeOk;
    private ButtonType mButtonTypeCancel;

    public ConfirmationMessage(String titleKey, String messageBodyKey){
        ImageView questionGraphic = new ImageView("image/question-icon_64x64.png");
        Image questionIcon = new Image("image/question-icon_64x64.png");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(getBundleText(titleKey));
        alert.setHeaderText(null);
        alert.setContentText(getBundleText(messageBodyKey));
        alert.setGraphic(questionGraphic);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(questionIcon);

        mButtonTypeOk = new ButtonType(getBundleText("button.continue"), ButtonBar.ButtonData.OK_DONE);
        mButtonTypeCancel = new ButtonType(getBundleText("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(mButtonTypeOk, mButtonTypeCancel);

        mResult = alert.showAndWait();
    }

    public Boolean okChosen(){
        return mResult.get() == mButtonTypeOk;
    }

    public Boolean cancelChosen(){
        return mResult.get() == mButtonTypeCancel;
    }

}
