package com.beamcalculate.controllers;

import com.beamcalculate.BeamCalculatorApp;
import com.beamcalculate.model.custom_alert.ConfirmationMessage;
import com.beamcalculate.model.page_manager.LanguageManager;
import com.beamcalculate.model.calculate.Rebar;
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

import java.net.URL;
import java.util.*;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

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
    @FXML private Parent rebarCasesPage;
    @FXML private InputPageController inputPageController;
    @FXML private MomentPageController momentPageController;
    @FXML private RebarCasesPageController rebarCasesPageController;

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

        momentPageButton.disableProperty().bind(inputPageController.newInputProperty());
        rebarCasesPageButton.disableProperty().bind(
                Bindings.or(
                        inputPageController.newInputProperty(),
                        Bindings.not(momentPageController.showRebarPageProperty())
                )
        );

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

        rebarCasesPageButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!oldValue) {
                setButtonPressed(
                        rebarCasesPageButton.mouseTransparentProperty(),
                        rebarCasesPageButton.selectedProperty()
                );
                mainPageTabPane.getSelectionModel().select(2);
            }
        }));


        inputPageButton.setSelected(true);
        //When the main fxml is loaded, inject the main controller to the input page controller
        inputPageController.injectMainController(this);

        momentPageController.injectMainController(this);

        double leftMenuWidth = leftPartOfSplitPane.getMinWidth();
        getInputPageAnchorPane().prefHeightProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().heightProperty(),110)
        );
        getInputPageAnchorPane().prefWidthProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().widthProperty(), leftMenuWidth + 50)
        );

        getMomentPageAnchorPane().prefHeightProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().heightProperty(),110)
        );
        getMomentPageAnchorPane().prefWidthProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().widthProperty(), leftMenuWidth + 50)
        );
        getRebarCasesPageAnchorPane().prefHeightProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().heightProperty(),110)
        );
        getRebarCasesPageAnchorPane().prefWidthProperty().bind(
                Bindings.subtract(BeamCalculatorApp.getPrimaryStage().widthProperty(), leftMenuWidth + 50)
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
                ConfirmationMessage confirmationMessage =
                        new ConfirmationMessage(
                                "window.title.confirmation",
                                "confirmation.content.changeLanguage"
                        );
                if(confirmationMessage.okChosen()) {
                    setTextForLanguageMenu();
                    mLanguageManager.setAppLanguage(locale);
                }
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

    public ToggleButton getRebarCasesPageButton() {
        return rebarCasesPageButton;
    }

    //Get anchor pane from input page controller
    public AnchorPane getInputPageAnchorPane() { return inputPageController.getAnchorPane(); }

    //Get anchor pane from moment page controller
    public AnchorPane getMomentPageAnchorPane() { return momentPageController.getAnchorPane(); }

    public BooleanProperty getShowRebarPageProperty() { return momentPageController.showRebarPageProperty(); }

    //Get anchor pane from rebar cases page controller
    public AnchorPane getRebarCasesPageAnchorPane() { return rebarCasesPageController.getAnchorPane(); }

    public void createMomentLineChart(SpanMomentFunction... spanMomentFunctions) { momentPageController.createMomentPage(spanMomentFunctions);}

    public void generateRebarSelectionCasesTable(Rebar rebar) { rebarCasesPageController.createRebarCasesPage(rebar);}
    @FXML
    public void handleFullScreen(ActionEvent actionEvent) {
        BeamCalculatorApp.getPrimaryStage().setFullScreen(true);
    }
}
