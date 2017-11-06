package com.beamcalculate;

import static com.beamcalculate.model.page_manager.LanguageManager.AppSettings;

import com.beamcalculate.model.page_manager.LanguageManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class BeamCalculatorApp extends Application {

    private static Stage mPrimaryStage = new Stage();
    private LanguageManager mLanguageManager = new LanguageManager();

    @Override
    public void start(Stage stage) throws Exception{
        mLanguageManager.setAppLanguage(AppSettings.getCurrentLocal());
        mPrimaryStage.setTitle("BeamCalculator 1.1.0-SNAPSHOT");
        mPrimaryStage.getIcons().add(new Image("image/icon.png"));

        mPrimaryStage.setOnCloseRequest(we -> {
//                Alert custom_alert = new Alert(Alert.AlertType.INFORMATION);
//                custom_alert.setTitle(BeamCalculatorApp.getBundleText("info.title.love"));
//                custom_alert.setHeaderText(null);
//                custom_alert.setContentText(BeamCalculatorApp.getBundleText("info.content.love"));
//                custom_alert.setGraphic(new ImageView("image/my_love.png"));
//                Stage stage = (Stage) custom_alert.getDialogPane().getScene().getWindow();
//                stage.getIcons().add(new Image("image/love.png"));
//                custom_alert.showAndWait();
        });

        mPrimaryStage.show();
    }

    public static Stage getPrimaryStage(){
        return mPrimaryStage;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
