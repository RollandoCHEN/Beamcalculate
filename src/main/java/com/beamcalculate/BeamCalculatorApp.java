package com.beamcalculate;

import static com.beamcalculate.model.LanguageManager.AppSettings;

import com.beamcalculate.model.LanguageManager;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.*;

public class BeamCalculatorApp extends Application {

    private BorderPane mBorderPane = new BorderPane();
    private StringProperty mWindowTitle = new SimpleStringProperty();
    private StringProperty mMenuText = new SimpleStringProperty();
    private Map<Locale,StringProperty> mLanguagesItems = new HashMap<>();
    private static Stage mPrimaryStage = new Stage();
    private LanguageManager mLanguageManager = new LanguageManager();

    @Override
    public void start(Stage stage) throws Exception{
//        mBorderPane.setTop(createMenuBar());
//        loadView(Locale.getDefault());
//        ScrollPane scrollPane = new ScrollPane(mBorderPane);
//        scrollPane.setFitToWidth(true);

        mLanguageManager.setAppLanguage(AppSettings.currentLocal);
        mPrimaryStage.setTitle("BeamCalculator 1.1.0-SNAPSHOT");
        mPrimaryStage.getIcons().add(new Image("image/icon.png"));
        mPrimaryStage.setMaximized(true);

//        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
//        if (primaryScreenBounds.getHeight() < mPrimaryStage.getScene().getHeight()){
//            mPrimaryStage.setHeight(primaryScreenBounds.getHeight());
//        }

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
//
//    private MenuBar createMenuBar() {
//        MenuBar menuBar = new MenuBar();
//        Menu menu = new Menu();
//        menu.textProperty().bind(mMenuText);
//        getSupportedLocales().forEach(locale -> {
//            MenuItem item = new MenuItem();
//            StringProperty languageItem = new SimpleStringProperty();
//            mLanguagesItems.put(locale,languageItem);
//            item.textProperty().bind(languageItem);
//
//            item.setOnAction(event -> {
//                loadView(locale);
//            });
//            menu.getItems().add(item);
//        });
//        menuBar.getMenus().addAll(menu);
//        return menuBar;
//    }

//    public static ResourceBundle getResourceBundle(){
//        ResourceBundle bundle = ResourceBundle.getBundle("UIResources", BeamCalculatorApp.AppSettings.currentLocal);
//        return bundle;
//    }
//
//    public static String getBundleText(String key){
//        ResourceBundle bundle = ResourceBundle.getBundle("UIResources", BeamCalculatorApp.AppSettings.currentLocal);
//        return bundle.getString(key);
//    }
//
//    public static List<Locale> getSupportedLocales() {
//        return new ArrayList<>(Arrays.asList(Locale.US, Locale.FRANCE, Locale.CHINA));
//    }
//
//    private void loadView(Locale locale) {
//        try {
//            Pane pane = FXMLLoader.load(
//                    getClass().getResource("/fxml/InputPage.fxml"),
//                    ResourceBundle.getBundle("UIResources", locale)
//            );
//            mBorderPane.setCenter(pane);
//            AppSettings.currentLocal = locale;
//            mWindowTitle.setValue(getBundleText("window.title.main"));
//            mMenuText.setValue(getBundleText("menu.languages"));
//            mLanguagesItems.forEach((itemLocale, languageItem) -> {
//                if(locale.equals(itemLocale)){
//                    languageItem.setValue(getBundleText("menuItem." + itemLocale.getLanguage()));
//
//                } else {
//                    languageItem.setValue(
//                            ResourceBundle.getBundle("UIResources", itemLocale).getString("menuItem." + itemLocale.getLanguage())
//                                    + "("
//                                    + getBundleText("menuItem." + itemLocale.getLanguage())
//                                    + ")"
//                    );
//                }
//            });
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//
//    public static class AppSettings {
//        public static Locale currentLocal;
//
//    }

    public static Stage getPrimaryStage(){
        return mPrimaryStage;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
