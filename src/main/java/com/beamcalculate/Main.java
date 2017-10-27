package com.beamcalculate;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.*;

public class Main extends Application {

    private BorderPane borderPane = new BorderPane();
    private StringProperty windowTitle = new SimpleStringProperty();
    private StringProperty menuText = new SimpleStringProperty();
    private Map<Locale,StringProperty> languagesItems = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        borderPane.setTop(createMenuBar());
        loadView(Locale.getDefault());
        primaryStage.titleProperty().bind(windowTitle);
        primaryStage.setScene(new Scene(borderPane, 1020, 930));
        primaryStage.getIcons().add(new Image("image/main.png"));
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle(Main.getBundleText("window.title.love"));
//                alert.setHeaderText(null);
//                alert.setContentText(Main.getBundleText("message.love"));
//                alert.setGraphic(new ImageView("image/my love.png"));
//                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
//                stage.getIcons().add(new Image("image/love.png"));
//                alert.showAndWait();
            }
        });

        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu();
        menu.textProperty().bind(menuText);
        getSupportedLocales().forEach(locale -> {
            MenuItem item = new MenuItem();
            StringProperty languageItem = new SimpleStringProperty();
            languagesItems.put(locale,languageItem);
            item.textProperty().bind(languageItem);

            item.setOnAction(event -> {
                loadView(locale);
            });
            menu.getItems().add(item);
        });
        menuBar.getMenus().addAll(menu);
        return menuBar;
    }

    public static ResourceBundle getResourceBundle(){
        ResourceBundle bundle = ResourceBundle.getBundle("UIResources", Main.AppSettings.currentLocal);
        return bundle;
    }

    public static String getBundleText(String key){
        ResourceBundle bundle = ResourceBundle.getBundle("UIResources", Main.AppSettings.currentLocal);
        return bundle.getString(key);
    }

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.US, Locale.FRANCE, Locale.CHINA));
    }

    private void loadView(Locale locale) {
        try {
            Pane pane = FXMLLoader.load(
                    getClass().getResource("/fxml/main.fxml"),
                    ResourceBundle.getBundle("UIResources", locale)
            );
            borderPane.setCenter(pane);
            AppSettings.currentLocal = locale;
            windowTitle.setValue(getBundleText("window.title.main"));
            menuText.setValue(getBundleText("menu.languages"));
            languagesItems.forEach((itemLocale, languageItem) -> {
                if(locale.equals(itemLocale)){
                    languageItem.setValue(getBundleText("menuItem." + itemLocale.getLanguage()));

                } else {
                    languageItem.setValue(
                            ResourceBundle.getBundle("UIResources", itemLocale).getString("menuItem." + itemLocale.getLanguage())
                                    + "("
                                    + getBundleText("menuItem." + itemLocale.getLanguage())
                                    + ")"
                    );
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Main.getBundleText("window.title.idiot"));
        alert.setHeaderText(null);
        alert.setContentText(Main.getBundleText("message.idiot"));
        alert.setGraphic(new ImageView("image/idiot.png"));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("image/idiot-icon_256x256.png"));
        ButtonType buttonYes = new ButtonType(Main.getBundleText("button.yes"));
        ButtonType buttonNo = new ButtonType(Main.getBundleText("button.no"));

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonYes){
            // ... user chose "Yes"
        } else if (result.get() == buttonNo) {
            // ... user chose "No"
            Alert finishMessage = new Alert(Alert.AlertType.INFORMATION);
            finishMessage.setTitle(Main.getBundleText("window.title.idiot"));
            finishMessage.setHeaderText(null);
            finishMessage.setContentText(Main.getBundleText("message.eliott"));
            finishMessage.setGraphic(new ImageView("image/eliott.png"));
            Stage finishStage = (Stage) finishMessage.getDialogPane().getScene().getWindow();
            finishStage.getIcons().add(new Image("image/mdr.png"));
            finishMessage.showAndWait();
            System.exit(0);
        } else {
            // ... user chose CANCEL or closed the dialog
        }

    }

    public static class AppSettings {
        public static Locale currentLocal;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
