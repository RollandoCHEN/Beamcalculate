package com.beamcalculate.model.custom_alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.Set;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

/**
 * Created by Ruolin on 29/10/2017 for Beamcalculate.
 */
public class WarningMessage {
    private Alert mAlert;
    private Optional<ButtonType> mResult;
    private ButtonType mButtonTypeOk;
    private ButtonType mButtonTypeCancel;

    public enum WarningMessageOption {
        WITH_CONFIRM(true),
        WITHOUT_CONFIRM(false);
        private boolean withConfirmation;

        WarningMessageOption(boolean withConfirmation) {
            this.withConfirmation = withConfirmation;
        }

        public boolean withConfirmation() {
            return withConfirmation;
        }
    }

    public WarningMessage(Set<String> messageInputSet, String messageBodyKey, WarningMessageOption option){
        if(!messageInputSet.isEmpty()) {
            ImageView warningGraphic = new ImageView("image/warning-icon_64x64.png");
            Image warningIcon = new Image("image/warning-icon_64x64.png");

            mAlert = new Alert(Alert.AlertType.CONFIRMATION);
            mAlert.setTitle(getBundleText("window.title.warning"));
            mAlert.setHeaderText(null);

            StringBuilder messageFromSet = new StringBuilder();
            messageInputSet.forEach (messageItem -> messageFromSet.append("\n- " + messageItem));
            mAlert.setGraphic(warningGraphic);
            Stage stage = (Stage) mAlert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(warningIcon);

            StringBuilder infoMessage = new StringBuilder(getBundleText(messageBodyKey) + messageFromSet);

            mButtonTypeOk = new ButtonType(getBundleText("button.ok"), ButtonBar.ButtonData.OK_DONE);
            if (option.withConfirmation()) {
                mButtonTypeCancel = new ButtonType(getBundleText("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
                mAlert.getButtonTypes().setAll(mButtonTypeOk, mButtonTypeCancel);
                infoMessage.append("\n\n").append(getBundleText("warning.content.confirmation"));
            } else {
                mAlert.getButtonTypes().setAll(mButtonTypeOk);
            }

            mAlert.setContentText(infoMessage.toString());
            mResult = mAlert.showAndWait();
            messageInputSet.clear();
        }
    }

    public Boolean okChosen(){
        return mResult.get() == mButtonTypeOk;
    }

    public Boolean cancelChosen(){
        return mResult.get() == mButtonTypeCancel;
    }
}
