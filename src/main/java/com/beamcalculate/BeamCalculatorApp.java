package com.beamcalculate;

import static com.beamcalculate.model.LanguageManager.AppSettings;

import com.beamcalculate.model.LanguageManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class BeamCalculatorApp extends Application {

    private static Stage mPrimaryStage = new Stage();
    private LanguageManager mLanguageManager = new LanguageManager();

    @Override
    public void start(Stage stage) throws Exception{
        mLanguageManager.setAppLanguage(AppSettings.currentLocal);
        mPrimaryStage.setTitle("BeamCalculator 1.1.0-SNAPSHOT");
        mPrimaryStage.getIcons().add(new Image("image/icon.png"));

        mPrimaryStage.setOnCloseRequest(we -> {
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle(BeamCalculatorApp.getBundleText("info.title.love"));
//                alert.setHeaderText(null);
//                alert.setContentText(BeamCalculatorApp.getBundleText("info.content.love"));
//                alert.setGraphic(new ImageView("image/my_love.png"));
//                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
//                stage.getIcons().add(new Image("image/love.png"));
//                alert.showAndWait();
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
