package com.beamcalculate.controllers;

import com.beamcalculate.BeamCalculatorApp;
import com.beamcalculate.model.LanguageManager;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.*;

import static com.beamcalculate.model.LanguageManager.getBundleText;

/**
 * Created by Ruolin on 01/11/2017 for Beamcalculate.
 */
public class MainAccessController implements Initializable {
    @FXML Menu languageMenu;
    @FXML AnchorPane leftPartOfSplitPane;
    @FXML TabPane mainPageTabPane;
    @FXML ToggleButton inputPageButton;
    @FXML ToggleButton momentPageButton;
    @FXML ToggleButton rebarCasesPageButton;
    @FXML MenuItem fullScreenItem;

    @FXML private Parent inputPage;
    @FXML private Parent momentPage;
    @FXML private InputPageController inputPageController;
    @FXML private MomentPageController momentPageController;

    private List<BooleanProperty> mBttPressedIndicPropertiesList = new ArrayList<>();

    private Map<Locale,StringProperty> mLanguagesItems = new HashMap<>();
    private LanguageManager mLanguageManager = new LanguageManager();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mBttPressedIndicPropertiesList.addAll(Arrays.asList(
                inputPageButton.mouseTransparentProperty(),
                inputPageButton.selectedProperty(),
                momentPageButton.mouseTransparentProperty(),
                momentPageButton.selectedProperty(),
                rebarCasesPageButton.mouseTransparentProperty(),
                rebarCasesPageButton.selectedProperty()
        ));

        languageMenu.getItems().addAll(getMenuItemList());
        setTextForLanguageMenu();
        fullScreenItem.setAccelerator(KeyCombination.keyCombination("Ctrl+F"));

        inputPageButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!oldValue) {
                setButtonPressed(
                        inputPageButton.mouseTransparentProperty(),
                        inputPageButton.selectedProperty()
                );
                mainPageTabPane.getSelectionModel().select(0);
            }
        }));

        momentPageButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!oldValue) {
                setButtonPressed(
                        momentPageButton.mouseTransparentProperty(),
                        momentPageButton.selectedProperty()
                );
                mainPageTabPane.getSelectionModel().select(1);
            }
        }));

        inputPageButton.setSelected(true);
        //When the main fxml is loaded, inject the main controller to the input page controller
        inputPageController.injectMainController(this);

        double leftMenuWidth = leftPartOfSplitPane.getMinWidth();
        getMomentPageAnchorPane().prefHeightProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().heightProperty(),90)
        );
        getMomentPageAnchorPane().prefWidthProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().widthProperty(), leftMenuWidth + 30)
        );

        getInputPageAnchorPane().prefHeightProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().heightProperty(),90)
        );
        getInputPageAnchorPane().prefWidthProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().widthProperty(), leftMenuWidth + 30)
        );
    }

    private void setButtonPressed(BooleanProperty... buttonProperties){
        mBttPressedIndicPropertiesList.forEach(booleanProperty -> {
            if (!Arrays.asList(buttonProperties).contains(booleanProperty)) {
                booleanProperty.setValue(false);
            }
            Arrays.asList(buttonProperties).forEach(buttonProperty -> buttonProperty.setValue(true));
        });
    }

    private List<MenuItem> getMenuItemList() {
        List<MenuItem> menuItemList = new ArrayList<>();
        LanguageManager.getSupportedLocales().forEach(locale -> {
            MenuItem menuItem = new MenuItem();
            StringProperty languageStrP = new SimpleStringProperty();
            mLanguagesItems.put(locale,languageStrP);
            menuItem.textProperty().bind(languageStrP);

            menuItem.setOnAction(event -> {
                setTextForLanguageMenu();
                mLanguageManager.setAppLanguage(locale);
            });

            menuItemList.add(menuItem);
        });
        return menuItemList;
    }

    private void setTextForLanguageMenu(){
        mLanguagesItems.forEach((itemLocale, languageItem) -> {
            if(LanguageManager.AppSettings.currentLocal.equals(itemLocale)){
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
    }

    public ToggleButton getMomentPageButton() {
        return momentPageButton;
    }

    //Get nodes from input page controller
    public AnchorPane getInputPageAnchorPane() { return inputPageController.getAnchorPane(); }

    //Get nodes from moment page controller to pass them to the input page controller
    public AnchorPane getMomentPageAnchorPane() { return momentPageController.getAnchorPane(); }

    public void createMomentLineChart(SpanMomentFunction... spanMomentFunctions) { momentPageController.createMomentLineChart(spanMomentFunctions);}

    @FXML
    public void handleFullScreen(ActionEvent actionEvent) {
        BeamCalculatorApp.getPrimaryStage().setFullScreen(true);
    }
}
