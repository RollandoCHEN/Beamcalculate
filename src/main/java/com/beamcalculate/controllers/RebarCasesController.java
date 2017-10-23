package com.beamcalculate.controllers;

import com.beamcalculate.Main;
import com.beamcalculate.enums.MyMethods;
import com.beamcalculate.model.RebarType_Number;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.result.ReinforcementResultTable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.beamcalculate.enums.NumericalFormat.TWODECIMALS;
import static com.beamcalculate.enums.NumericalFormat.ZERODECIMAL;
import static com.beamcalculate.enums.ReinforcementParam.j_A_S;

public class RebarCasesController implements Initializable {
    @FXML private Label currentSectionNameLabel = new Label();
    @FXML private Label currentRebarCase = new Label();
    @FXML private Label methodNameText = new Label();
    @FXML private GridPane spanRebarSelectionGridPane = new GridPane();
    @FXML private GridPane supportRebarselectionGridPane = new GridPane();
    @FXML private VBox rebarVBox = new VBox();
    @FXML private HBox flangeWidthHBox = new HBox();
    @FXML private VBox flangeHeightVBox = new VBox();
    @FXML private VBox widthVBox = new VBox();
    @FXML private HBox heightHBox = new HBox();

    private Rebar mRebar;
    private Reinforcement mReinforcement;

    private DoubleProperty totalHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedTotalHeight = new SimpleDoubleProperty();
    private DoubleProperty webWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedWebWidth = new SimpleDoubleProperty();
    private DoubleProperty flangeWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedFlangeWidth = new SimpleDoubleProperty();
    private DoubleProperty flangeHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedFlangeHeight = new SimpleDoubleProperty();
    private DoubleProperty flangeCompHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedFlangeCompHeight = new SimpleDoubleProperty();
    private DoubleProperty flangeCompWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedFlangeCompWidth = new SimpleDoubleProperty();
    private DoubleProperty webCompHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedWebCompHeight = new SimpleDoubleProperty();
    private DoubleProperty webCompWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedWebCompWidth = new SimpleDoubleProperty();

    private DoubleProperty compRegionHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedCompRegionHeight = new SimpleDoubleProperty();

    private DoubleProperty leftGridPaneWidth = new SimpleDoubleProperty();
    private DoubleProperty rightGridPaneWidth = new SimpleDoubleProperty();

    private StringProperty compRegionHeightString = new SimpleStringProperty();
    private StringProperty webCompWidthString = new SimpleStringProperty();
    private StringProperty flangeWidthString = new SimpleStringProperty();
    private StringProperty flangeHeightString = new SimpleStringProperty();
    private StringProperty totalHeightString = new SimpleStringProperty();

    private double mRatio;
    private double mCoverThickness_cm;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // height of cross section diagram is fixed at 200px
        mCoverThickness_cm = 3;
        double fixHeight = 300;
        mRatio = fixHeight / (Geometry.getSectionHeight() * 100);

        displayedTotalHeight.bind(Bindings.multiply(totalHeight, mRatio));
        displayedWebWidth.bind(Bindings.multiply(webWidth, mRatio));
        displayedWebCompHeight.bind(Bindings.multiply(webCompHeight, mRatio));
        displayedWebCompWidth.bind(Bindings.multiply(webCompWidth, mRatio));
        displayedCompRegionHeight.bind(Bindings.multiply(compRegionHeight, mRatio));
        displayedFlangeWidth.bind(Bindings.multiply(flangeWidth, mRatio));
        displayedFlangeHeight.bind(Bindings.multiply(flangeHeight, mRatio));
        displayedFlangeCompHeight.bind(Bindings.multiply(flangeCompHeight, mRatio));
        displayedFlangeCompWidth.bind(Bindings.multiply(flangeCompWidth, mRatio));

        totalHeight.bind(Bindings.multiply(Geometry.sectionHeightProperty(),100));
        webWidth.bind(Bindings.multiply(Geometry.sectionWidthProperty(),100));
        flangeHeight.bind(totalHeight);
        flangeWidth.bind(webWidth);
        webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(1),100));
        webCompWidth.bind(webWidth);

        setCurrentSectionNameLabel(1);


        // cross section schema
        if (MainController.isOnTSection()) {
            flangeHeight.bind(Bindings.multiply(Geometry.slabThicknessProperty(),100));
            flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(1),100));
            flangeCompHeight.bind(Bindings.multiply(Reinforcement.getFlangeCompressionsHeightMap().get(1),100));
            flangeCompWidth.bind(flangeWidth);
        } else {
            widthVBox.getChildren().remove(flangeWidthHBox);
            heightHBox.getChildren().remove(flangeHeightVBox);

        }

        if(webCompHeight.get()!=0) {
            compRegionHeight.bind(webCompHeight);
        } else {
            compRegionHeight.bind(flangeCompHeight);
        }

        Bindings.bindBidirectional(webCompWidthString, webCompWidth, ZERODECIMAL.getDecimalFormat());
        Bindings.bindBidirectional(compRegionHeightString, compRegionHeight, TWODECIMALS.getDecimalFormat());
        Bindings.bindBidirectional(flangeWidthString, flangeWidth, ZERODECIMAL.getDecimalFormat());
        Bindings.bindBidirectional(flangeHeightString, flangeHeight, ZERODECIMAL.getDecimalFormat());
        Bindings.bindBidirectional(totalHeightString, totalHeight, ZERODECIMAL.getDecimalFormat());

        DoubleProperty maxDisplayedFlangeWidth = new SimpleDoubleProperty();
        Reinforcement.getEffectiveWidthPropertyMap().forEach((spanId, effectiveWidthProperty) -> {
            if(effectiveWidthProperty.get() > maxDisplayedFlangeWidth.get()){
                maxDisplayedFlangeWidth.set(effectiveWidthProperty.get() * 100 * mRatio);
            }
        });
        double maxSchemaWidth;
        if(MainController.isOnTSection()){
            maxSchemaWidth = maxDisplayedFlangeWidth.get();
        } else {
            maxSchemaWidth = displayedWebWidth.get();
        }

        leftGridPaneWidth.set(maxSchemaWidth + 200);
        rightGridPaneWidth.set(Geometry.getNumSpan() * 140 + 130);
    }

    public void generateRebarSelectionCasesTable() {
        mReinforcement = mRebar.getReinforcement();

        StringBuilder tableTitle = new StringBuilder();
        tableTitle.append(
                Main.getBundleText("label.momentCalculateMethod") +
                        " : " +
                        mReinforcement.getSpanMomentFunction().getMethod()
        );
        if (MainController.isOnTSection()){
            tableTitle.append(
                    " (" + Main.getBundleText("title.onTSection") + ")"
            );
        }

        methodNameText.setText(tableTitle.toString());

        int maxNumOfCases = 1;
        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++) {
            int rebarCases = mRebar.getRebarCasesListOfSpan(spanId).size();
            maxNumOfCases = Math.max(rebarCases, maxNumOfCases);
        }

        for (int caseNum = 1; caseNum < maxNumOfCases+1; caseNum++){
            Label caseLabel = new Label(Main.getBundleText("label.case") + " " + caseNum);
            spanRebarSelectionGridPane.add(caseLabel, 0, caseNum);
        }

        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++) {
            Label spanIdLabel = new Label(Main.getBundleText("label.span") + " " + spanId);
            spanIdLabel.setStyle("-fx-font-weight: bold;");

            double calculatedArea = mReinforcement.getSpanReinforceParam().get(spanId).get(j_A_S);
            Label calculatedAreaLabel = new Label(
                    j_A_S.getSymbol() + " = " + TWODECIMALS.getDecimalFormat().format(calculatedArea) + " " + Main.getBundleText("unit.area.cm2")
            );
            calculatedAreaLabel.setStyle("-fx-font-style: italic; -fx-font-weight: bold;");

            VBox spanVBox = new VBox(spanIdLabel, calculatedAreaLabel);

            int columnNum = spanId;
            spanRebarSelectionGridPane.add(spanVBox, columnNum, 0);

            List<Map<Integer, RebarType_Number>> rebarCasesList = mRebar.getRebarCasesListOfSpan(spanId);
            int caseVariable;

            double minRebarArea;
            if (rebarCasesList.size() == 0){
                Button rebarCaseButton = new Button("Exceed limit!");
                VBox vBox = new VBox(rebarCaseButton);
                spanRebarSelectionGridPane.add(vBox, columnNum, 1);
                rebarCaseButton.setOnAction(event -> {
                    // switch the cross section
                    setCurrentSectionNameLabel(columnNum);
                    if (MainController.isOnTSection()) {
                        flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(columnNum),100));
                        flangeCompHeight.bind(Bindings.multiply(Reinforcement.getFlangeCompressionsHeightMap().get(columnNum),100));
                    }
                    webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(columnNum),100));
                    webCompWidth.bind(webWidth);
                    rebarVBox.getChildren().clear();
                });
            } else {
                minRebarArea = mRebar.getTotalRebarAreaListOfSpan(spanId).get(0);

                for (int caseNum = 0; caseNum < rebarCasesList.size(); caseNum++){
                    minRebarArea = Math.min(mRebar.getTotalRebarAreaListOfSpan(spanId).get(caseNum), minRebarArea);
                }

                for (caseVariable = 0; caseVariable < rebarCasesList.size(); caseVariable++){
                    int caseNum = caseVariable;

                    //create button for each rebar case
                    Button rebarCaseButton = new Button();
                    //set rebar selection for button text
                    rebarCaseButton.setText(getRebarCaseString(rebarCasesList, caseNum));

                    //create label to show rebar area for each rebar selection case
                    double rebarArea = mRebar.getTotalRebarAreaListOfSpan(spanId).get(caseNum);
                    Label rebarAreaLabel = new Label(
                            j_A_S.getSymbol() + " = "
                                    + TWODECIMALS.getDecimalFormat().format(rebarArea)
                                    + " " + Main.getBundleText("unit.area.cm2")
                    );
                    rebarAreaLabel.setStyle("-fx-font-style: italic");
                    if (rebarArea == minRebarArea){
                        rebarAreaLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-style: italic;");
                    }

                    //add button and area label to the span rebar selection grid pane
                    VBox vBox = new VBox(rebarCaseButton, rebarAreaLabel);
                    spanRebarSelectionGridPane.add(vBox, columnNum, caseNum + 1);

                    //add click action to the button
                    rebarCaseButton.setOnAction(event -> {
//                    RebarCutChart rebarCutChart = new RebarCutChart(getRebar(), columnNum, caseNum);

                        // switch the cross section
                        setCurrentSectionNameLabel(columnNum);
                        currentRebarCase.setText(rebarCaseButton.getText());

                        if (MainController.isOnTSection()) {
                            flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(columnNum),100));
                            flangeCompHeight.bind(Bindings.multiply(Reinforcement.getFlangeCompressionsHeightMap().get(columnNum),100));
                        }
                        webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(columnNum),100));
                        webCompWidth.bind(webWidth);

                        // add rebar to the cross section figure
                        rebarVBox.getChildren().clear();
                        for (int layerNum = rebarCasesList.get(caseNum).size(); layerNum > 0; layerNum--){
                            RebarType_Number rebarType_number = rebarCasesList.get(caseNum).get(layerNum);
                            int numberOfRebar = rebarType_number.getNumberOfRebar();
                            double rebarDiameter_cm = rebarType_number.getRebarType().getDiameter_mm()/10;
                            double rebarSpacing_cm = (webCompWidth.get() - mCoverThickness_cm * 2 - numberOfRebar * rebarDiameter_cm) / (numberOfRebar - 1);

                            HBox thisLayerRebarHBox = new HBox();
                            if (layerNum == 1){
                                thisLayerRebarHBox.setPadding(
                                        new Insets(0,mCoverThickness_cm * mRatio,mCoverThickness_cm * mRatio,mCoverThickness_cm * mRatio)
                                );
                            } else {
                                thisLayerRebarHBox.setPadding(
                                        new Insets(1 * mRatio,mCoverThickness_cm * mRatio,1 * mRatio,mCoverThickness_cm * mRatio)
                                );
                            }
                            thisLayerRebarHBox.setAlignment(Pos.BOTTOM_CENTER);
                            thisLayerRebarHBox.setSpacing(rebarSpacing_cm * mRatio);
                            for (int i=0; i<numberOfRebar; i++){
                                Circle rebar = new Circle();
                                rebar.setFill(Paint.valueOf("red"));
                                rebar.setRadius(rebarDiameter_cm * mRatio / 2);
                                thisLayerRebarHBox.getChildren().add(rebar);
                            }

                            rebarVBox.getChildren().add(thisLayerRebarHBox);
                        }

                    });
                }
            }

        }
    }

    private String getRebarCaseString(List<Map<Integer, RebarType_Number>> rebarCasesList, int caseNum) {
        StringBuilder buttonString = new StringBuilder();
        int lastLayer = rebarCasesList.get(caseNum).size();
        for (int layerNum = lastLayer; layerNum > 0; layerNum--){
            RebarType_Number rebarType_number = rebarCasesList.get(caseNum).get(layerNum);

            if (layerNum != lastLayer){
                buttonString.append("\n");
            }
            String rebarTypeName = rebarType_number.getRebarType().name();
            int numberOfRebar = rebarType_number.getNumberOfRebar();
            buttonString.append(MyMethods.getOrdinalNumber(layerNum))
                    .append(Main.getBundleText("label.steelRebarLayer"))
                    .append(" : ").append(numberOfRebar).append(rebarTypeName);
        }
        return buttonString.toString();
    }

    private void setCurrentSectionNameLabel(int spanId) {
        currentSectionNameLabel.setText(
                Main.getBundleText("title.crossSection") +
                        " (" +Main.getBundleText("unit.length.cm") + ")" +
                        " - " + Main.getBundleText("label.span") + " " + spanId
        );
    }

    @FXML
    public void showCalculateDetail(ActionEvent actionEvent) {
        ReinforcementResultTable reinforcementResult = new ReinforcementResultTable(mReinforcement);
        reinforcementResult.showStage();
    }

    public double getTotalHeight() {
        return totalHeight.get();
    }

    public DoubleProperty totalHeightProperty() {
        return totalHeight;
    }

    public double getFlangeWidth() {
        return flangeWidth.get();
    }

    public DoubleProperty flangeWidthProperty() {
        return flangeWidth;
    }

    public double getWebWidth() {
        return webWidth.get();
    }

    public DoubleProperty webWidthProperty() {
        return webWidth;
    }

    public double getFlangeHeight() {
        return flangeHeight.get();
    }

    public DoubleProperty flangeHeightProperty() {
        return flangeHeight;
    }

    public double getFlangeCompHeight() {
        return flangeCompHeight.get();
    }

    public DoubleProperty flangeCompHeightProperty() {
        return flangeCompHeight;
    }

    public double getFlangeCompWidth() {
        return flangeCompWidth.get();
    }

    public DoubleProperty flangeCompWidthProperty() {
        return flangeCompWidth;
    }

    public double getWebCompHeight() {
        return webCompHeight.get();
    }

    public DoubleProperty webCompHeightProperty() {
        return webCompHeight;
    }

    public double getWebCompWidth() {
        return webCompWidth.get();
    }

    public DoubleProperty webCompWidthProperty() {
        return webCompWidth;
    }

    public String getCompRegionHeightString() {
        return compRegionHeightString.get();
    }

    public StringProperty compRegionHeightStringProperty() {
        return compRegionHeightString;
    }

    public String getWebCompWidthString() {
        return webCompWidthString.get();
    }

    public StringProperty webCompWidthStringProperty() {
        return webCompWidthString;
    }

    public double getCompRegionHeight() {
        return compRegionHeight.get();
    }

    public DoubleProperty compRegionHeightProperty() {
        return compRegionHeight;
    }

    public String getFlangeWidthString() {
        return flangeWidthString.get();
    }

    public StringProperty flangeWidthStringProperty() {
        return flangeWidthString;
    }

    public String getFlangeHeightString() {
        return flangeHeightString.get();
    }

    public StringProperty flangeHeightStringProperty() {
        return flangeHeightString;
    }

    public String getTotalHeightString() {
        return totalHeightString.get();
    }

    public StringProperty totalHeightStringProperty() {
        return totalHeightString;
    }

    public double getDisplayedTotalHeight() {
        return displayedTotalHeight.get();
    }

    public DoubleProperty displayedTotalHeightProperty() {
        return displayedTotalHeight;
    }

    public double getDisplayedWebWidth() {
        return displayedWebWidth.get();
    }

    public DoubleProperty displayedWebWidthProperty() {
        return displayedWebWidth;
    }

    public double getDisplayedFlangeWidth() {
        return displayedFlangeWidth.get();
    }

    public DoubleProperty displayedFlangeWidthProperty() {
        return displayedFlangeWidth;
    }

    public double getDisplayedFlangeHeight() {
        return displayedFlangeHeight.get();
    }

    public DoubleProperty displayedFlangeHeightProperty() {
        return displayedFlangeHeight;
    }

    public double getDisplayedFlangeCompHeight() {
        return displayedFlangeCompHeight.get();
    }

    public DoubleProperty displayedFlangeCompHeightProperty() {
        return displayedFlangeCompHeight;
    }

    public double getDisplayedFlangeCompWidth() {
        return displayedFlangeCompWidth.get();
    }

    public DoubleProperty displayedFlangeCompWidthProperty() {
        return displayedFlangeCompWidth;
    }

    public double getDisplayedWebCompHeight() {
        return displayedWebCompHeight.get();
    }

    public DoubleProperty displayedWebCompHeightProperty() {
        return displayedWebCompHeight;
    }

    public double getDisplayedWebCompWidth() {
        return displayedWebCompWidth.get();
    }

    public DoubleProperty displayedWebCompWidthProperty() {
        return displayedWebCompWidth;
    }

    public double getDisplayedCompRegionHeight() {
        return displayedCompRegionHeight.get();
    }

    public DoubleProperty displayedCompRegionHeightProperty() {
        return displayedCompRegionHeight;
    }

    public double getLeftGridPaneWidth() {
        return leftGridPaneWidth.get();
    }

    public DoubleProperty leftGridPaneWidthProperty() {
        return leftGridPaneWidth;
    }

    public double getRightGridPaneWidth() {
        return rightGridPaneWidth.get();
    }

    public DoubleProperty rightGridPaneWidthProperty() {
        return rightGridPaneWidth;
    }

    public Rebar getRebar() {
        return mRebar;
    }

    public void setRebar(Rebar rebar) {
        mRebar = rebar;
    }
}
