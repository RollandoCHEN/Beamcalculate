package com.beamcalculate;

import static com.beamcalculate.model.page_manager.LanguageManager.AppSettings;

import com.beamcalculate.model.page_manager.LanguageManager;
import com.jfoenix.controls.JFXSlider;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BeamCalculatorApp extends Application {

    private static Stage mPrimaryStage = new Stage();
    private LanguageManager mLanguageManager = new LanguageManager();

    @Override
    public void start(Stage stage) throws Exception{
        Font.loadFont(BeamCalculatorApp.class.getResource("/fonts/Microsoft-YaHei.ttf").toExternalForm(), 10);
        Font.loadFont(BeamCalculatorApp.class.getResource("/fonts/Microsoft-YaHei-Bold.ttf").toExternalForm(), 10);
//        Font.loadFont(BeamCalculatorApp.class.getResource("/fonts/Roboto-Regular.ttf").toExternalForm(), 10);
//        Font.loadFont(BeamCalculatorApp.class.getResource("/fonts/Roboto-Bold.ttf").toExternalForm(), 10);
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
