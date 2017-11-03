package com.beamcalculate.model;

import com.beamcalculate.BeamCalculatorApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.*;

/**
 * Created by Ruolin on 01/11/2017 for Beamcalculate.
 */
public class LanguageManager {

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.US, Locale.FRANCE, Locale.CHINA));
    }

    public static class AppSettings {
        public static Locale currentLocal = Locale.getDefault();
    }

    public static ResourceBundle getResourceBundle(){
        ResourceBundle bundle = ResourceBundle.getBundle("UIResources", AppSettings.currentLocal);
        return bundle;
    }

    public static String getBundleText(String key){
        ResourceBundle bundle = ResourceBundle.getBundle("UIResources", AppSettings.currentLocal);
        return bundle.getString(key);
    }

    public void setAppLanguage(Locale locale){
        AppSettings.currentLocal = locale;
        try {
            Parent parent = FXMLLoader.load(
                    getClass().getResource("/fxml/MainAccess.fxml"),
                    ResourceBundle.getBundle("UIResources", locale)
            );
            BeamCalculatorApp.getPrimaryStage().setScene(new Scene(parent));
            if (BeamCalculatorApp.getPrimaryStage().isMaximized()) {
                BeamCalculatorApp.getPrimaryStage().setMaximized(false);
                BeamCalculatorApp.getPrimaryStage().setMaximized(true);
            } else {
                BeamCalculatorApp.getPrimaryStage().setMaximized(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
