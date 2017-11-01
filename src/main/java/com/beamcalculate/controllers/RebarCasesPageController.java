package com.beamcalculate.controllers;

import static com.beamcalculate.model.LanguageManager.getBundleText;
import com.beamcalculate.custom.MyMethods;
import com.beamcalculate.custom.RebarType_Number;
import com.beamcalculate.model.calculate.Rebar;
import com.beamcalculate.model.calculate.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.result.RebarCutChart;
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
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.beamcalculate.enums.NumericalFormat.TWODECIMALS;
import static com.beamcalculate.enums.NumericalFormat.ZERODECIMAL;
import static com.beamcalculate.enums.ReinforcementParam.j_A_S;

public class RebarCasesPageController implements Initializable {
    @FXML private Label currentSectionNameLabel = new Label();
    @FXML private Label currentSpanNameLabel = new Label();
    @FXML private Label currentRebarCase = new Label();
    @FXML private Label methodNameText = new Label();
    @FXML private GridPane spanRebarSelectionGridPane = new GridPane();
    @FXML private GridPane supportRebarselectionGridPane = new GridPane();
    @FXML private VBox crossSectionRebarVBox = new VBox();
    @FXML private VBox elevationRebarVBox = new VBox();
    @FXML private HBox flangeWidthHBox = new HBox();
    @FXML private VBox flangeHeightVBox = new VBox();
    @FXML private VBox widthVBox = new VBox();
    @FXML private HBox heightHBox = new HBox();
    @FXML private Button elevationDetailButton = new Button();
    @FXML private Button rebarAreaDetailButton = new Button();
    @FXML private GridPane rebarDimensionAnnoGridPane = new GridPane();
    @FXML private HBox rebarLeftIndentHBox = new HBox();
    @FXML private HBox rebarRightIndentHBox = new HBox();
    @FXML private HBox rebarLengthHBox = new HBox();


    private Rebar mRebar;
    private Reinforcement mReinforcement;
    private RebarCutChart mRebarCutChart;
    private Stage mRebarChartStage = new Stage();

    private DoubleProperty totalHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedTotalHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedElevationTotalHeight = new SimpleDoubleProperty();
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
    private DoubleProperty displayedLeftSupportWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedRightSupportWidth = new SimpleDoubleProperty();
    private DoubleProperty displayedSpanLength = new SimpleDoubleProperty();

    private DoubleProperty displayedRebarLeftIndent = new SimpleDoubleProperty();
    private DoubleProperty displayedRebarRightIndent = new SimpleDoubleProperty();
    private DoubleProperty displayedRebarLength = new SimpleDoubleProperty();

    private DoubleProperty compRegionHeight = new SimpleDoubleProperty();
    private DoubleProperty displayedCompRegionHeight = new SimpleDoubleProperty();

    private DoubleProperty totalLength = new SimpleDoubleProperty();

    private DoubleProperty leftGridPaneWidth = new SimpleDoubleProperty();
    private DoubleProperty rightGridPaneWidth = new SimpleDoubleProperty();

    private StringProperty compRegionHeightString = new SimpleStringProperty();
    private StringProperty webCompWidthString = new SimpleStringProperty();
    private StringProperty flangeWidthString = new SimpleStringProperty();
    private StringProperty flangeHeightString = new SimpleStringProperty();
    private StringProperty totalHeightString = new SimpleStringProperty();
    private StringProperty rebarLeftIndentString = new SimpleStringProperty();
    private StringProperty rebarLengthString = new SimpleStringProperty();
    private StringProperty rebarRightIndentString = new SimpleStringProperty();
    private StringProperty leftSupportWidthString = new SimpleStringProperty();
    private StringProperty rightSupportWidthString = new SimpleStringProperty();
    private StringProperty spanLengthString = new SimpleStringProperty();

    private double mSectionViewRatio;
    private double mElevationViewLengthRatio;
    private double mElevationViewHeightRatio;

    private double mCoverThickness_cm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // height of cross section diagram is fixed at 300px
        mCoverThickness_cm = 3;
        double fixedDisplayedCrossSectionHeight = 250;
        double fixedDisplayedElevationHeight = 150;
        mSectionViewRatio = fixedDisplayedCrossSectionHeight / (Geometry.getSectionHeight() * 100);
        mElevationViewHeightRatio = fixedDisplayedElevationHeight / (Geometry.getSectionHeight() * 100);

        displayedElevationTotalHeight.bind(Bindings.multiply(totalHeight, mElevationViewHeightRatio));

        displayedTotalHeight.bind(Bindings.multiply(totalHeight, mSectionViewRatio));
        displayedWebWidth.bind(Bindings.multiply(webWidth, mSectionViewRatio));
        displayedWebCompHeight.bind(Bindings.multiply(webCompHeight, mSectionViewRatio));
        displayedWebCompWidth.bind(Bindings.multiply(webCompWidth, mSectionViewRatio));
        displayedCompRegionHeight.bind(Bindings.multiply(compRegionHeight, mSectionViewRatio));
        displayedFlangeWidth.bind(Bindings.multiply(flangeWidth, mSectionViewRatio));
        displayedFlangeHeight.bind(Bindings.multiply(flangeHeight, mSectionViewRatio));
        displayedFlangeCompHeight.bind(Bindings.multiply(flangeCompHeight, mSectionViewRatio));
        displayedFlangeCompWidth.bind(Bindings.multiply(flangeCompWidth, mSectionViewRatio));

        totalHeight.bind(Bindings.multiply(Geometry.sectionHeightProperty(),100));
        webWidth.bind(Bindings.multiply(Geometry.sectionWidthProperty(),100));
        flangeHeight.bind(totalHeight);
        flangeWidth.bind(webWidth);
        webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(1),100));
        webCompWidth.bind(webWidth);

        setCurrentSpanSectionLabel(1);

        // cross section schema
        if (InputPageController.isOnTSection()) {
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

        // scene size depends on the max flange width
        DoubleProperty maxDisplayedFlangeWidth = new SimpleDoubleProperty();
        Reinforcement.getEffectiveWidthPropertyMap().forEach((spanId, effectiveWidthProperty) -> {
            if(effectiveWidthProperty.get() > maxDisplayedFlangeWidth.get()){
                maxDisplayedFlangeWidth.set(effectiveWidthProperty.get() * 100 * mSectionViewRatio);
            }
        });
        double maxSchemaWidth;
        if(InputPageController.isOnTSection()){
            maxSchemaWidth = maxDisplayedFlangeWidth.get();
        } else {
            maxSchemaWidth = displayedWebWidth.get();
        }

        leftGridPaneWidth.set(maxSchemaWidth + 200);
        rightGridPaneWidth.set(Geometry.getNumSpan() * 140 + 130);

        totalLength.set(0.9 * (leftGridPaneWidth.get() + rightGridPaneWidth.get()));

        // initialize the elevation view length depending on the scene size
        double spanLength_cm = Geometry.spansLengthMap().get(1) * 100;
        double leftSupportWidth_cm = Geometry.supportWidthMap().get(1) * 100;
        double rightSupportWidth_cm = Geometry.supportWidthMap().get(2) * 100;

        mElevationViewLengthRatio = totalLength.get() / (spanLength_cm + leftSupportWidth_cm + rightSupportWidth_cm);

        displayedSpanLength.set(spanLength_cm * mElevationViewLengthRatio);
        displayedLeftSupportWidth.set(leftSupportWidth_cm * mElevationViewLengthRatio);
        displayedRightSupportWidth.set(rightSupportWidth_cm * mElevationViewLengthRatio);

        leftSupportWidthString.set(ZERODECIMAL.getDecimalFormat().format(leftSupportWidth_cm));
        rightSupportWidthString.set(ZERODECIMAL.getDecimalFormat().format(rightSupportWidth_cm));
        spanLengthString.set(ZERODECIMAL.getDecimalFormat().format(spanLength_cm));

        rebarDimensionAnnoGridPane.setPadding(
                new Insets(0,displayedRightSupportWidth.get()/2,0,displayedLeftSupportWidth.get()/2)
        );

    }

    public void generateRebarSelectionCasesTable() {
        mReinforcement = mRebar.getReinforcement();

        StringBuilder tableTitle = new StringBuilder();
        tableTitle.append(
                getBundleText("label.momentCalculateMethod") +
                        " : " +
                        mReinforcement.getSpanMomentFunction().getMethod()
        );
        if (InputPageController.isOnTSection()){
            tableTitle.append(
                    " (" + getBundleText("title.onTSection") + ")"
            );
        }

        methodNameText.setText(tableTitle.toString());

        int maxNumOfCases = 1;
        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++) {
            int rebarCases = mRebar.getRebarCasesListOfSpan(spanId).size();
            maxNumOfCases = Math.max(rebarCases, maxNumOfCases);
        }

        for (int caseNum = 1; caseNum < maxNumOfCases+1; caseNum++){
            Label caseLabel = new Label(getBundleText("label.case") + " " + caseNum);
            spanRebarSelectionGridPane.add(caseLabel, 0, caseNum);
        }

        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++) {
            Label spanIdLabel = new Label(getBundleText("label.span") + " " + spanId);
            spanIdLabel.setStyle("-fx-font-weight: bold;");

            double calculatedArea = mReinforcement.getSpanReinforceParam().get(spanId).get(j_A_S);
            Label calculatedAreaLabel = new Label(
                    j_A_S.getSymbol() + " = " + TWODECIMALS.getDecimalFormat().format(calculatedArea) + " " + getBundleText("unit.area.cm2")
            );
            calculatedAreaLabel.setStyle("-fx-font-style: italic; -fx-font-weight: bold;");

            VBox spanVBox = new VBox(spanIdLabel, calculatedAreaLabel);

            int columnNum = spanId;
            spanRebarSelectionGridPane.add(spanVBox, columnNum, 0);

            List<Map<Integer, RebarType_Number>> rebarCasesList = mRebar.getRebarCasesListOfSpan(spanId);
            int caseVariable;

            if (rebarCasesList.size() == 0){
                Button rebarCaseButton = new Button("Exceed limit!");
                VBox vBox = new VBox(rebarCaseButton);
                spanRebarSelectionGridPane.add(vBox, columnNum, 1);
                rebarCaseButton.setOnAction(event -> {
                    // switch the cross section
                    setCurrentSpanSectionLabel(columnNum);
                    if (InputPageController.isOnTSection()) {
                        flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(columnNum),100));
                        flangeCompHeight.bind(Bindings.multiply(Reinforcement.getFlangeCompressionsHeightMap().get(columnNum),100));
                    }
                    webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(columnNum),100));
                    webCompWidth.bind(webWidth);
                    crossSectionRebarVBox.getChildren().clear();
                });
            } else {
                double minRebarArea = Collections.min(mRebar .getTotalRebarAreaListOfSpan(spanId));

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
                                    + " " + getBundleText("unit.area.cm2")
                    );
                    rebarAreaLabel.setStyle("-fx-font-style: italic");
                    if (rebarArea == minRebarArea){
                        rebarAreaLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-style: italic;");
                    }

                    //add button and area label to the span_function rebar selection grid pane
                    VBox vBox = new VBox(rebarCaseButton, rebarAreaLabel);
                    spanRebarSelectionGridPane.add(vBox, columnNum, caseNum + 1);

                    //add click action to the rebar case button
                    rebarCaseButton.setOnAction(event -> {

                        mRebarCutChart = new RebarCutChart(getRebar(), columnNum, caseNum);

                        prepareRebarCutCalculateDetails();

                        double spanLength_cm = Geometry.spansLengthMap().get(columnNum) * 100;
                        double leftSupportWidth_cm = Geometry.supportWidthMap().get(columnNum) * 100;
                        double rightSupportWidth_cm = Geometry.supportWidthMap().get(columnNum + 1) * 100;

                        mElevationViewLengthRatio = totalLength.get() / (spanLength_cm + leftSupportWidth_cm + rightSupportWidth_cm);

                        displayedLeftSupportWidth.set(leftSupportWidth_cm * mElevationViewLengthRatio);
                        displayedRightSupportWidth.set(rightSupportWidth_cm * mElevationViewLengthRatio);
                        displayedSpanLength.set(spanLength_cm * mElevationViewLengthRatio);

                        leftSupportWidthString.set(ZERODECIMAL.getDecimalFormat().format(leftSupportWidth_cm));
                        rightSupportWidthString.set(ZERODECIMAL.getDecimalFormat().format(rightSupportWidth_cm));
                        spanLengthString.set(ZERODECIMAL.getDecimalFormat().format(spanLength_cm));

                        rebarDimensionAnnoGridPane.setPadding(
                                new Insets(0,displayedRightSupportWidth.get()/2,0,displayedLeftSupportWidth.get()/2)
                        );

                        elevationRebarVBox.setPadding(
                                new Insets(0,0,mCoverThickness_cm * mElevationViewHeightRatio, displayedLeftSupportWidth.get()/2)
                        );
                        elevationRebarVBox.setSpacing(2 * mElevationViewHeightRatio);

                        if (rebarCasesList.get(caseNum).size() != 1){
                            rebarLeftIndentHBox.setVisible(true);
                            rebarRightIndentHBox.setVisible(true);
                            rebarLengthHBox.setVisible(true);
                        } else {
                            rebarLeftIndentHBox.setVisible(false);
                            rebarRightIndentHBox.setVisible(false);
                            rebarLengthHBox.setVisible(false);
                        }

                        elevationRebarVBox.getChildren().clear();
                        for (int layerNum = rebarCasesList.get(caseNum).size(); layerNum > 0; layerNum--) {
                            RebarType_Number rebarType_number = rebarCasesList.get(caseNum).get(layerNum);
                            double rebarDiameter_cm = rebarType_number.getRebarType().getDiameter_mm()/10;
                            double rebarLength_cm = (mRebarCutChart.getSecondLayerRebarEnd() - mRebarCutChart.getSecondLayerRebarStart()) * 100;
                            double rebarLeftIndent_cm = mRebarCutChart.getSecondLayerRebarStart() * 100;
                            double rebarRightIndent_cm = spanLength_cm + leftSupportWidth_cm/2 + rightSupportWidth_cm/2 - rebarLength_cm - rebarLeftIndent_cm;

                            VBox thisLayerRebarVBox = new VBox();
                            if (layerNum == 1){
                                Line rebarLine = new Line();
                                rebarLine.setEndX(
                                        (spanLength_cm + leftSupportWidth_cm/2 + rightSupportWidth_cm/2) * mElevationViewLengthRatio
                                );
                                rebarLine.setStroke(Paint.valueOf("red"));
                                rebarLine.setStrokeWidth(rebarDiameter_cm * mElevationViewHeightRatio);
                                thisLayerRebarVBox.getChildren().add(rebarLine);

                            } else {
                                thisLayerRebarVBox.setPadding(
                                        new Insets(0,0,0,rebarLeftIndent_cm * mElevationViewLengthRatio)
                                );
                                Line rebarLine = new Line();
                                rebarLine.setEndX(rebarLength_cm * mElevationViewLengthRatio);
                                rebarLine.setStroke(Paint.valueOf("red"));
                                rebarLine.setStrokeWidth(rebarDiameter_cm * mElevationViewHeightRatio);
                                thisLayerRebarVBox.getChildren().add(rebarLine);
                            }
                            elevationRebarVBox.getChildren().add(thisLayerRebarVBox);

                            displayedRebarLeftIndent.set(rebarLeftIndent_cm * mElevationViewLengthRatio);
                            displayedRebarRightIndent.set(rebarRightIndent_cm * mElevationViewLengthRatio);
                            displayedRebarLength.set(rebarLength_cm * mElevationViewLengthRatio);

                            rebarLeftIndentString.set(ZERODECIMAL.getDecimalFormat().format(rebarLeftIndent_cm));
                            rebarRightIndentString.set(ZERODECIMAL.getDecimalFormat().format(rebarRightIndent_cm));
                            rebarLengthString.set(ZERODECIMAL.getDecimalFormat().format(rebarLength_cm));
                        }

                        // switch the cross section
                        setCurrentSpanSectionLabel(columnNum);
                        currentRebarCase.setText(rebarCaseButton.getText());

                        if (InputPageController.isOnTSection()) {
                            flangeWidth.bind(Bindings.multiply(Reinforcement.getEffectiveWidthPropertyMap().get(columnNum),100));
                            flangeCompHeight.bind(Bindings.multiply(Reinforcement.getFlangeCompressionsHeightMap().get(columnNum),100));
                        }
                        webCompHeight.bind(Bindings.multiply(Reinforcement.getWebCompressionHeightMap().get(columnNum),100));
                        webCompWidth.bind(webWidth);

                        // add rebar to the cross section figure
                        crossSectionRebarVBox.getChildren().clear();
                        for (int layerNum = rebarCasesList.get(caseNum).size(); layerNum > 0; layerNum--){
                            RebarType_Number rebarType_number = rebarCasesList.get(caseNum).get(layerNum);
                            int numberOfRebar = rebarType_number.getNumberOfRebar();
                            double rebarDiameter_cm = rebarType_number.getRebarType().getDiameter_mm()/10;
                            double rebarSpacing_cm = (webCompWidth.get() - mCoverThickness_cm * 2 - numberOfRebar * rebarDiameter_cm) / (numberOfRebar - 1);

                            HBox thisLayerRebarHBox = new HBox();
                            if (layerNum == 1){
                                thisLayerRebarHBox.setPadding(
                                        new Insets(0,mCoverThickness_cm * mSectionViewRatio,mCoverThickness_cm * mSectionViewRatio,mCoverThickness_cm * mSectionViewRatio)
                                );
                            } else {
                                thisLayerRebarHBox.setPadding(
                                        new Insets(1 * mSectionViewRatio,mCoverThickness_cm * mSectionViewRatio,1 * mSectionViewRatio,mCoverThickness_cm * mSectionViewRatio)
                                );
                            }
                            thisLayerRebarHBox.setAlignment(Pos.BOTTOM_CENTER);
                            thisLayerRebarHBox.setSpacing(rebarSpacing_cm * mSectionViewRatio);
                            for (int i=0; i<numberOfRebar; i++){
                                Circle rebar = new Circle();
                                rebar.setFill(Paint.valueOf("red"));
                                rebar.setRadius(rebarDiameter_cm * mSectionViewRatio / 2);
                                thisLayerRebarHBox.getChildren().add(rebar);
                            }

                            crossSectionRebarVBox.getChildren().add(thisLayerRebarHBox);
                        }

                    });
                }
            }

        }
    }

    private void prepareRebarCutCalculateDetails() {
        mRebarChartStage.setTitle(getBundleText("window.title.rebarCut"));
        mRebarChartStage.getIcons().add(new Image("image/chart.png"));
        mRebarChartStage.setScene(mRebarCutChart.getScene());
        elevationDetailButton.disableProperty().bind(mRebarChartStage.showingProperty());
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
                    .append(getBundleText("label.steelRebarLayer"))
                    .append(" : ").append(numberOfRebar).append(rebarTypeName);
        }
        return buttonString.toString();
    }

    private void setCurrentSpanSectionLabel(int spanId) {
        currentSectionNameLabel.setText(
                getBundleText("title.crossSection") +
                        " (" + getBundleText("unit.length.cm") + ")" +
                        " - " + getBundleText("label.span") + " " + spanId
        );

        currentSpanNameLabel.setText(
                getBundleText("title.sectionalElevation") +
                        " (" + getBundleText("unit.length.cm") + ")" +
                        " - " + getBundleText("label.span") + " " + spanId
        );
    }

    @FXML
    public void showCalculateDetail(ActionEvent actionEvent) {
        ReinforcementResultTable reinforcementResultTable = new ReinforcementResultTable(mReinforcement);
        rebarAreaDetailButton.disableProperty().bind(reinforcementResultTable.getStage().showingProperty());
        reinforcementResultTable.getStage().show();
    }

    @FXML
    public void showElevationDetail(ActionEvent actionEvent) {
        mRebarChartStage.show();
    }

    public double getTotalHeight() {
        return totalHeight.get();
    }

    public DoubleProperty totalHeightProperty() {
        return totalHeight;
    }

    public double getDisplayedElevationTotalHeight() {
        return displayedElevationTotalHeight.get();
    }

    public DoubleProperty displayedElevationTotalHeightProperty() {
        return displayedElevationTotalHeight;
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

    public String getRebarLeftIndentString() {
        return rebarLeftIndentString.get();
    }

    public StringProperty rebarLeftIndentStringProperty() {
        return rebarLeftIndentString;
    }

    public String getRebarLengthString() {
        return rebarLengthString.get();
    }

    public StringProperty rebarLengthStringProperty() {
        return rebarLengthString;
    }

    public String getRebarRightIndentString() {
        return rebarRightIndentString.get();
    }

    public StringProperty rebarRightIndentStringProperty() {
        return rebarRightIndentString;
    }

    public String getLeftSupportWidthString() {
        return leftSupportWidthString.get();
    }

    public StringProperty leftSupportWidthStringProperty() {
        return leftSupportWidthString;
    }

    public String getRightSupportWidthString() {
        return rightSupportWidthString.get();
    }

    public StringProperty rightSupportWidthStringProperty() {
        return rightSupportWidthString;
    }

    public String getSpanLengthString() {
        return spanLengthString.get();
    }

    public StringProperty spanLengthStringProperty() {
        return spanLengthString;
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

    public double getTotalLength() {
        return totalLength.get();
    }

    public DoubleProperty totalLengthProperty() {
        return totalLength;
    }

    public double getDisplayedLeftSupportWidth() {
        return displayedLeftSupportWidth.get();
    }

    public DoubleProperty displayedLeftSupportWidthProperty() {
        return displayedLeftSupportWidth;
    }

    public double getDisplayedRightSupportWidth() {
        return displayedRightSupportWidth.get();
    }

    public DoubleProperty displayedRightSupportWidthProperty() {
        return displayedRightSupportWidth;
    }

    public double getDisplayedSpanLength() {
        return displayedSpanLength.get();
    }

    public DoubleProperty displayedSpanLengthProperty() {
        return displayedSpanLength;
    }

    public double getDisplayedRebarLeftIndent() {
        return displayedRebarLeftIndent.get();
    }

    public DoubleProperty displayedRebarLeftIndentProperty() {
        return displayedRebarLeftIndent;
    }

    public double getDisplayedRebarRightIndent() {
        return displayedRebarRightIndent.get();
    }

    public DoubleProperty displayedRebarRightIndentProperty() {
        return displayedRebarRightIndent;
    }

    public double getDisplayedRebarLength() {
        return displayedRebarLength.get();
    }

    public DoubleProperty displayedRebarLengthProperty() {
        return displayedRebarLength;
    }

    public Rebar getRebar() {
        return mRebar;
    }

    public void setRebar(Rebar rebar) {
        mRebar = rebar;
    }
}