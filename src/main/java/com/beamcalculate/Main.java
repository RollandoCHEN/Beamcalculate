package com.beamcalculate;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class Main extends Application {

    private BorderPane mBorderPane = new BorderPane();
    private StringProperty mWindowTitle = new SimpleStringProperty();
    private StringProperty mMenuText = new SimpleStringProperty();
    private Map<Locale,StringProperty> mLanguagesItems = new HashMap<>();
    private static Stage mPrimaryStage = new Stage();

    @Override
    public void start(Stage stage) throws Exception{
        mBorderPane.setTop(createMenuBar());
        loadView(Locale.getDefault());
        ScrollPane scrollPane = new ScrollPane(mBorderPane);
        scrollPane.setFitToWidth(true);
        mPrimaryStage.titleProperty().bind(mWindowTitle);
        mPrimaryStage.setScene(new Scene(scrollPane, 1050, 950));
        mPrimaryStage.getIcons().add(new Image("image/main.png"));

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        if (primaryScreenBounds.getHeight() < mPrimaryStage.getScene().getHeight()){
            mPrimaryStage.setHeight(primaryScreenBounds.getHeight());
        }

        mPrimaryStage.setOnCloseRequest(we -> {
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle(Main.getBundleText("info.title.love"));
//                alert.setHeaderText(null);
//                alert.setContentText(Main.getBundleText("info.content.love"));
//                alert.setGraphic(new ImageView("image/my_love.png"));
//                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
//                stage.getIcons().add(new Image("image/love.png"));
//                alert.showAndWait();
        });

        mPrimaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu();
        menu.textProperty().bind(mMenuText);
        getSupportedLocales().forEach(locale -> {
            MenuItem item = new MenuItem();
            StringProperty languageItem = new SimpleStringProperty();
            mLanguagesItems.put(locale,languageItem);
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
            mBorderPane.setCenter(pane);
            AppSettings.currentLocal = locale;
            mWindowTitle.setValue(getBundleText("window.title.main"));
            mMenuText.setValue(getBundleText("menu.languages"));
            mLanguagesItems.forEach((itemLocale, languageItem) -> {
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
    }

    public static class AppSettings {
        public static Locale currentLocal;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
