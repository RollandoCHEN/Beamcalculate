package com.beamcalculate.model.page_manager;

import com.beamcalculate.BeamCalculatorApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.*;

import static java.util.ResourceBundle.*;

/**
 * Created by Ruolin on 01/11/2017 for Beamcalculate.
 */
public class LanguageManager {

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.US, Locale.FRANCE, Locale.CHINA));
    }

    public static class AppSettings {
        private static Locale currentLocal = Locale.getDefault();

        public static Locale getCurrentLocal() {
            return currentLocal;
        }

        public static void setCurrentLocal(Locale currentLocal) {
            AppSettings.currentLocal = currentLocal;
        }
    }

    public static ResourceBundle getResourceBundle(){
        return getBundle("UIResources", AppSettings.currentLocal);
    }

    public static String getBundleText(String key){
        ResourceBundle bundle = getBundle("UIResources", AppSettings.currentLocal);
        return bundle.getString(key);
    }

    public void setAppLanguage(Locale locale){
        AppSettings.setCurrentLocal(locale);

        int previousScreenStatus;
        if(BeamCalculatorApp.getPrimaryStage().isFullScreen()){
            previousScreenStatus = 2;
        } else if (BeamCalculatorApp.getPrimaryStage().isMaximized()){
            previousScreenStatus = 1;
        } else {
            previousScreenStatus = 0;
        }
        try {
            Parent parent = FXMLLoader.load(
                    getClass().getResource("/fxml/MainAccess.fxml"),
                    getBundle("UIResources", locale)
            );
            BeamCalculatorApp.getPrimaryStage().setScene(new Scene(parent));

            //after setting scene, the primary stage window performance weirdly
            //so need to reset the window size
            if (previousScreenStatus == 2){
                BeamCalculatorApp.getPrimaryStage().setFullScreen(true);
            } else if (previousScreenStatus == 1) {
//                BeamCalculatorApp.getPrimaryStage().setMaximized(false);
//                BeamCalculatorApp.getPrimaryStage().setMaximized(true);
            } else {
//                BeamCalculatorApp.getPrimaryStage().setMaximized(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
