package com.beamcalculate.controllers;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

import com.beamcalculate.model.RebarCase;
import com.beamcalculate.model.RebarType_Amount;
import com.beamcalculate.model.calculator.Deflection;
import com.beamcalculate.model.calculator.Rebar;
import com.beamcalculate.model.calculator.Reinforcement;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.page_manager.PageScaleHandler;
import com.beamcalculate.model.result.RebarCutChart;
import com.beamcalculate.model.result.ReinforcementResultTable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.beamcalculate.enums.NumericalFormat.TWO_DECIMALS;
import static com.beamcalculate.enums.NumericalFormat.ZERO_DECIMAL;
import static com.beamcalculate.enums.ReinforcementParam.j_A_S;

public class RebarCasesPageController {
    @FXML private ScrollPane scrollContainer;
    @FXML private AnchorPane rebarPageAnchorPane;
    @FXML private Label currentSectionNameLabel;
    @FXML private Label currentSpanNameLabel;
    @FXML private Label currentRebarCase;
    @FXML private Label methodNameText;
    @FXML private GridPane spanRebarSelectionGridPane;
    @FXML private VBox crossSectionRebarVBox;
    @FXML private VBox elevationRebarVBox;
    @FXML private HBox flangeWidthHBox;
    @FXML private VBox flangeHeightVBox;
    @FXML private VBox widthVBox;
    @FXML private HBox heightHBox;
    @FXML private JFXButton elevationDetailButton;
    @FXML private JFXButton rebarAreaDetailButton;
    @FXML private GridPane rebarDimensionAnnoGridPane;
    @FXML private HBox rebarLeftIndentHBox;
    @FXML private HBox rebarRightIndentHBox;
    @FXML private HBox rebarLengthHBox;
    @FXML private JFXButton deflectionButton;

    private MainAccessController mMainAccessController;

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

    private DoubleProperty spanLength_cm = new SimpleDoubleProperty();
    private DoubleProperty leftSupportWidth_cm = new SimpleDoubleProperty();
    private DoubleProperty rightSupportWidth_cm = new SimpleDoubleProperty();

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
    private DoubleProperty mElevationViewLengthRatio = new SimpleDoubleProperty();
    private double mElevationViewHeightRatio;

    private double mCoverThickness_cm;
    private double mRebarVSpacing_cm;

    private double mMinHeight;
    private double mMinWidth;

    private BooleanBinding mDisableDeflectionButton = Bindings.isNotNull(new JFXComboBox<String>().valueProperty());
    private BooleanProperty mShowDeflectionPage = new SimpleBooleanProperty(false);
    private List<ObjectProperty<RebarCase>> mRebarSelectionList = new ArrayList<>();

    public class RebarCasesPageCreator {
        private final Geometry mGeometry;

        public RebarCasesPageCreator(Rebar rebar) {
            mRebar = rebar;
            mReinforcement = rebar.getReinforcement();
            mGeometry = mReinforcement.getSpanMomentFunction().getInputs().getGeometry();

            initializeCrossSectionView();
            setPageMinSize();
            initializeElevationView();
            generateRebarSelectionCasesTable();

            PageScaleHandler scaleHandler = new PageScaleHandler();
            scaleHandler.AddScaleListener(scrollContainer, rebarPageAnchorPane, mMinHeight, mMinWidth);

            deflectionButton.disableProperty().bind(mDisableDeflectionButton);
            deflectionButton.setOnAction(event -> {
                Deflection deflection = new Deflection(mRebar, mRebarSelectionList);
                mMainAccessController.generateDeflectionVerification(deflection);

                mShowDeflectionPage.setValue(true);
                mMainAccessController.getDeflectionPageButton().setSelected(true);
            });
        }

        private void initializeElevationView() {
            // initialize the elevation view length depending on the scene size
            clearRebarDisplaying();

            spanLength_cm.setValue(mGeometry.spansLengthMap().get(1) * 100);
            leftSupportWidth_cm.setValue(mGeometry.supportWidthMap().get(1) * 100);
            rightSupportWidth_cm.setValue(mGeometry.supportWidthMap().get(2) * 100);

            mElevationViewLengthRatio.bind(
                    Bindings.divide(
                            totalLength,
                            Bindings.add(spanLength_cm, leftSupportWidth_cm).add(rightSupportWidth_cm)
                    )
            );

            displayedSpanLength.bind(Bindings.multiply(spanLength_cm, mElevationViewLengthRatio));
            displayedLeftSupportWidth.bind(Bindings.multiply(leftSupportWidth_cm ,mElevationViewLengthRatio));
            displayedRightSupportWidth.bind(Bindings.multiply(rightSupportWidth_cm, mElevationViewLengthRatio));

            leftSupportWidthString.bind(ZERO_DECIMAL.format(leftSupportWidth_cm));
            rightSupportWidthString.bind(ZERO_DECIMAL.format(rightSupportWidth_cm));
            spanLengthString.bind(ZERO_DECIMAL.format(spanLength_cm));

            rebarDimensionAnnoGridPane.setPadding(
                    new Insets(0,displayedRightSupportWidth.get()/2,0,displayedLeftSupportWidth.get()/2)
            );
        }

        private void clearRebarDisplaying() {
            crossSectionRebarVBox.getChildren().clear();    //remove the rebar on section view
            currentRebarCase.setText(" \n ");
            elevationRebarVBox.getChildren().clear();       //remove the rebar symbol
            displayedRebarLeftIndent.set(0);                //reset the rebar length annotation
            displayedRebarRightIndent.set(0);
            displayedRebarLength.set(0);
            rebarLeftIndentHBox.setVisible(false);          //set invisible the rebar length annotation
            rebarRightIndentHBox.setVisible(false);
            rebarLengthHBox.setVisible(false);
        }

        private void initializeCrossSectionView() {
            // height of cross section diagram is fixed at 300px
            mCoverThickness_cm = mGeometry.getCoverThickness_cm();
            double fixedDisplayedCrossSectionHeight = 250;
            double fixedDisplayedElevationHeight = 150;
            mSectionViewRatio = fixedDisplayedCrossSectionHeight / (mGeometry.getSectionHeight() * 100);
            mElevationViewHeightRatio = fixedDisplayedElevationHeight / (mGeometry.getSectionHeight() * 100);

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

            totalHeight.set(mGeometry.sectionHeightProperty().get() * 100);
            webWidth.set(mGeometry.sectionWidthProperty().get() * 100);
            flangeHeight.set(totalHeight.get());
            flangeWidth.set(webWidth.get());
            webCompHeight.set(mReinforcement.getWebCompressionHeightMap().get(1).get() * 100);
            webCompWidth.set(webWidth.get());

            setCurrentSpanSectionLabel(1);

            // cross section schema
            if (mGeometry.isOnTSection()) {
                flangeHeight.set(mGeometry.slabThicknessProperty().get() * 100);
                flangeWidth.set(mReinforcement.getEffectiveWidthPropertyMap().get(1).get() * 100);
                flangeCompHeight.set(mReinforcement.getFlangeCompressionsHeightMap().get(1).get() * 100);
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

            webCompWidthString.bind(ZERO_DECIMAL.format(webCompWidth));
            compRegionHeightString.bind(TWO_DECIMALS.format(compRegionHeight));
            flangeWidthString.bind(ZERO_DECIMAL.format(flangeWidth));
            flangeHeightString.bind(ZERO_DECIMAL.format(flangeHeight));
            totalHeightString.bind(ZERO_DECIMAL.format(totalHeight));
        }

        private void setPageMinSize() {
            double maxSchemaWidth;
            if(mGeometry.isOnTSection()){
                // scene size depends on the max flange width
                DoubleProperty maxDisplayedFlangeWidth = new SimpleDoubleProperty();
                mReinforcement.getEffectiveWidthPropertyMap().forEach((spanId, effectiveWidthProperty) -> {
                    if(effectiveWidthProperty.get() > maxDisplayedFlangeWidth.get()){
                        maxDisplayedFlangeWidth.set(effectiveWidthProperty.get() * 100 * mSectionViewRatio);
                    }
                });
                maxSchemaWidth = maxDisplayedFlangeWidth.get();
            } else {
                maxSchemaWidth = displayedWebWidth.get();
            }

            leftGridPaneWidth.set(maxSchemaWidth + 280);
            rightGridPaneWidth.set(mGeometry.getNumSpan() * 175 + 120);

            totalLength.set(0.9 * (leftGridPaneWidth.get() + rightGridPaneWidth.get()));

            // 80 is the padding in the grid pane, around the left and right grid pane
            mMinWidth = getLeftGridPaneWidth() + getRightGridPaneWidth() + 40;

            rebarPageAnchorPane.setMinWidth(mMinWidth);
            int maxNumOfCases = 1;
            for (int spanId = 1; spanId < mGeometry.getNumSpan() + 1; spanId++) {
                int rebarCases = mRebar.getRebarCasesListOfSpan(spanId).size();
                maxNumOfCases = Math.max(rebarCases, maxNumOfCases);
            }
            mMinHeight = Math.max(maxNumOfCases * 110 + 100, 950);
            rebarPageAnchorPane.setMinHeight(mMinHeight);
        }

        private void generateRebarSelectionCasesTable() {
            spanRebarSelectionGridPane.getChildren().clear();
            StringBuilder tableTitle = new StringBuilder();
            tableTitle.append(getBundleText("label.momentCalculateMethod"))
                    .append(" : ")
                    .append(mReinforcement.getSpanMomentFunction().getMethod());
            if (mGeometry.isOnTSection()){
                tableTitle.append(" (")
                        .append(getBundleText("title.onTSection"))
                        .append(")");
            }

            methodNameText.setText(tableTitle.toString());

            int maxNumOfCases = 1;
            for (int spanId = 1; spanId < mGeometry.getNumSpan() + 1; spanId++) {
                int rebarCases = mRebar.getRebarCasesListOfSpan(spanId).size();
                maxNumOfCases = Math.max(rebarCases, maxNumOfCases);
            }

            for (int caseNum = 1; caseNum < maxNumOfCases + 1; caseNum++){
                Label caseLabel = new Label(getBundleText("label.case") + " " + caseNum);
                spanRebarSelectionGridPane.add(caseLabel, 0, caseNum);
            }

            // add rebar cas selection combo box at the last row of the grid pane
            spanRebarSelectionGridPane.add(
                    new Label(getBundleText("label.selected_case")), 0, maxNumOfCases + 1
            );

            for (int spanId = 1; spanId < mGeometry.getNumSpan()+1; spanId++) {
                // add combo box to the last row of the grid pane
                JFXComboBox<RebarCase> rebarSelectionBox = new JFXComboBox();
                spanRebarSelectionGridPane.add(rebarSelectionBox, spanId, maxNumOfCases + 1);
                rebarSelectionBox.getItems().addAll(FXCollections.observableArrayList(mRebar.getRebarCasesListOfSpan(spanId)));
                mDisableDeflectionButton = mDisableDeflectionButton.or(Bindings.isNull(rebarSelectionBox.valueProperty()));

                ObjectProperty<RebarCase> rebarCaseObjectProperty = new SimpleObjectProperty<>();
                rebarCaseObjectProperty.bind(rebarSelectionBox.valueProperty());
                mRebarSelectionList.add(rebarCaseObjectProperty);

                Label spanIdLabel = new Label(getBundleText("label.span") + " " + spanId);
                spanIdLabel.getStyleClass().add("header");

                double calculatedArea = mReinforcement.getSpanReinforceParam().get(spanId).get(j_A_S);
                Label calculatedAreaLabel = new Label(
                        j_A_S.getSymbol() + " = " + TWO_DECIMALS.format(calculatedArea) + " " + getBundleText("unit.area.cm2")
                );
                calculatedAreaLabel.getStyleClass().add("header");

                VBox spanVBox = new VBox(spanIdLabel, calculatedAreaLabel);

                int spanNum = spanId;
                spanRebarSelectionGridPane.add(spanVBox, spanNum, 0);

                List<RebarCase> rebarCasesList = mRebar.getRebarCasesListOfSpan(spanId);
                int caseVariable;

                if (rebarCasesList.size() == 0){
                    Button rebarCaseButton = new Button("Exceed limit!");
                    VBox vBox = new VBox(rebarCaseButton);
                    spanRebarSelectionGridPane.add(vBox, spanNum, 1);
                    rebarCaseButton.setOnAction(event -> {
                        // switch the cross section
                        setCurrentSpanSectionLabel(spanNum);
                        if (mGeometry.isOnTSection()) {
                            flangeWidth.set(mReinforcement.getEffectiveWidthPropertyMap().get(spanNum).get() * 100);
                            flangeCompHeight.set(mReinforcement.getFlangeCompressionsHeightMap().get(spanNum).get() * 100);
                        }
                        webCompHeight.set(mReinforcement.getWebCompressionHeightMap().get(spanNum).get() * 100);
                        webCompWidth.set(webWidth.get());

                        clearRebarDisplaying();
                    });
                } else {
                    double minRebarArea = Collections.min(mRebar .getTotalRebarAreaListOfSpan_cm2(spanId));

                    for (caseVariable = 0; caseVariable < rebarCasesList.size(); caseVariable++){
                        int caseNum = caseVariable;

                        //create button for each rebar case
                        JFXButton rebarCaseButton = new JFXButton();
                        rebarCaseButton.getStyleClass().add("button-cases");
                        //set rebar selection for button text
                        rebarCaseButton.setText(rebarCasesList.get(caseNum).toString());

                        //create label to show rebar area for each rebar selection case
                        double rebarArea = mRebar.getTotalRebarAreaListOfSpan_cm2(spanId).get(caseNum);
                        Label rebarAreaLabel = new Label(
                                j_A_S.getSymbol() + " = "
                                        + TWO_DECIMALS.format(rebarArea)
                                        + " " + getBundleText("unit.area.cm2")
                        );
                        if (rebarArea == minRebarArea){
                            rebarAreaLabel.getStyleClass().add("highlight");
                        }

                        //add button and area label to the span_function rebar selection grid pane
                        VBox vBox = new VBox(rebarCaseButton, rebarAreaLabel);
                        spanRebarSelectionGridPane.add(vBox, spanNum, caseNum + 1);

                        //add click action to the rebar case button
                        rebarCaseButton.setOnAction(event -> {
                            mRebarVSpacing_cm = rebarCasesList.get(caseNum).getMinSpacingBetweenRebar_mm() / 10;
                            //select the rebar case
                            rebarSelectionBox.getSelectionModel().select(caseNum);

                            mRebarCutChart = new RebarCutChart(getRebar(), spanNum, caseNum);

                            prepareRebarCutCalculateDetails();

                            spanLength_cm.setValue(mGeometry.spansLengthMap().get(spanNum) * 100);
                            leftSupportWidth_cm.setValue(mGeometry.supportWidthMap().get(spanNum) * 100);
                            rightSupportWidth_cm.setValue(mGeometry.supportWidthMap().get(spanNum + 1) * 100);

                            rebarDimensionAnnoGridPane.setPadding(
                                    new Insets(0,displayedRightSupportWidth.get()/2,0,displayedLeftSupportWidth.get()/2)
                            );

                            elevationRebarVBox.setPadding(
                                    new Insets(0,0,mCoverThickness_cm * mElevationViewHeightRatio, displayedLeftSupportWidth.get()/2)
                            );
                            elevationRebarVBox.setSpacing(mRebarVSpacing_cm * mElevationViewHeightRatio);

                            if (rebarCasesList.get(caseNum).layerAmount() != 1){
                                rebarLeftIndentHBox.setVisible(true);
                                rebarRightIndentHBox.setVisible(true);
                                rebarLengthHBox.setVisible(true);
                            } else {
                                rebarLeftIndentHBox.setVisible(false);
                                rebarRightIndentHBox.setVisible(false);
                                rebarLengthHBox.setVisible(false);
                            }

                            elevationRebarVBox.getChildren().clear();
                            for (int layerNum = rebarCasesList.get(caseNum).layerAmount(); layerNum > 0; layerNum--) {
                                RebarType_Amount rebarType_amount = rebarCasesList.get(caseNum).getRebarOfLayer(layerNum);
                                double rebarDiameter_cm = rebarType_amount.getRebarType().getDiameter_mm()/10;
                                double rebarLength_cm = (mRebarCutChart.getSecondLayerRebarEnd() - mRebarCutChart.getSecondLayerRebarStart()) * 100;
                                double rebarLeftIndent_cm = mRebarCutChart.getSecondLayerRebarStart() * 100;
                                double rebarRightIndent_cm = spanLength_cm.get() + leftSupportWidth_cm.get()/2 + rightSupportWidth_cm.get()/2 -
                                                rebarLength_cm - rebarLeftIndent_cm;

                                VBox thisLayerRebarVBox = new VBox();
                                if (layerNum == 1){
                                    Line rebarLine = new Line();
                                    rebarLine.setEndX(
                                            (spanLength_cm.get() + leftSupportWidth_cm.get()/2 + rightSupportWidth_cm.get()/2) *
                                                    mElevationViewLengthRatio.get()
                                    );
                                    rebarLine.setStroke(Paint.valueOf("red"));
                                    rebarLine.setStrokeWidth(rebarDiameter_cm * mElevationViewHeightRatio);
                                    thisLayerRebarVBox.getChildren().add(rebarLine);

                                } else {
                                    thisLayerRebarVBox.setPadding(
                                            new Insets(0,0,0,rebarLeftIndent_cm * mElevationViewLengthRatio.get())
                                    );
                                    Line rebarLine = new Line();
                                    rebarLine.setEndX(rebarLength_cm * mElevationViewLengthRatio.get());
                                    rebarLine.setStroke(Paint.valueOf("red"));
                                    rebarLine.setStrokeWidth(rebarDiameter_cm * mElevationViewHeightRatio);
                                    thisLayerRebarVBox.getChildren().add(rebarLine);
                                }
                                elevationRebarVBox.getChildren().add(thisLayerRebarVBox);

                                displayedRebarLeftIndent.set(rebarLeftIndent_cm * mElevationViewLengthRatio.get());
                                displayedRebarRightIndent.set(rebarRightIndent_cm * mElevationViewLengthRatio.get());
                                displayedRebarLength.set(rebarLength_cm * mElevationViewLengthRatio.get());

                                rebarLeftIndentString.set(ZERO_DECIMAL.format(rebarLeftIndent_cm));
                                rebarRightIndentString.set(ZERO_DECIMAL.format(rebarRightIndent_cm));
                                rebarLengthString.set(ZERO_DECIMAL.format(rebarLength_cm));
                            }

                            // switch the cross section
                            setCurrentSpanSectionLabel(spanNum);
                            currentRebarCase.setText(rebarCaseButton.getText());

                            if (mGeometry.isOnTSection()) {
                                flangeWidth.set(mReinforcement.getEffectiveWidthPropertyMap().get(spanNum).get() * 100);
                                flangeCompHeight.set(mReinforcement.getFlangeCompressionsHeightMap().get(spanNum).get() * 100);
                            }
                            webCompHeight.set(mReinforcement.getWebCompressionHeightMap().get(spanNum).get() * 100);
                            webCompWidth.set(webWidth.get());

                            // add rebar to the cross section figure
                            crossSectionRebarVBox.getChildren().clear();

                            crossSectionRebarVBox.setSpacing(mRebarVSpacing_cm * mSectionViewRatio);
                            for (int layerNum = rebarCasesList.get(caseNum).layerAmount(); layerNum > 0; layerNum--){
                                RebarType_Amount rebarType_amount = rebarCasesList.get(caseNum).getRebarOfLayer(layerNum);
                                int numberOfRebar = rebarType_amount.getNumberOfRebar();
                                double rebarDiameter_cm = rebarType_amount.getRebarType().getDiameter_mm()/10;
                                double rebarHSpacing_cm = (webCompWidth.get() - mCoverThickness_cm * 2 - numberOfRebar * rebarDiameter_cm) / (numberOfRebar - 1);

                                HBox thisLayerRebarHBox = new HBox();
                                if (layerNum == 1){
                                    thisLayerRebarHBox.setPadding(
                                            new Insets(0,mCoverThickness_cm * mSectionViewRatio,mCoverThickness_cm * mSectionViewRatio,mCoverThickness_cm * mSectionViewRatio)
                                    );
                                } else {
                                    thisLayerRebarHBox.setPadding(
                                            new Insets(0,mCoverThickness_cm * mSectionViewRatio,0,mCoverThickness_cm * mSectionViewRatio)
                                    );
                                }
                                thisLayerRebarHBox.setAlignment(Pos.BOTTOM_CENTER);
                                thisLayerRebarHBox.setSpacing(rebarHSpacing_cm * mSectionViewRatio);
                                for (int i=0; i<numberOfRebar; i++){
                                    Circle rebarCircle = new Circle();
                                    rebarCircle.setFill(Paint.valueOf("red"));
                                    rebarCircle.setRadius(rebarDiameter_cm * mSectionViewRatio / 2);
                                    thisLayerRebarHBox.getChildren().add(rebarCircle);
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
            mRebarChartStage.getIcons().add(new Image("image/section_32x32.png"));
            mRebarChartStage.setScene(mRebarCutChart.getScene());
            mRebarChartStage.setAlwaysOnTop(true);
            elevationDetailButton.disableProperty().bind(mRebarChartStage.showingProperty());
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

    }

    @FXML
    public void showCalculateDetail(ActionEvent actionEvent) {
        ReinforcementResultTable reinforcementResultTable = new ReinforcementResultTable(mReinforcement);
        rebarAreaDetailButton.disableProperty().bind(reinforcementResultTable.getStage().showingProperty());
        reinforcementResultTable.getStage().initModality(Modality.WINDOW_MODAL);
        reinforcementResultTable.getStage().initOwner(rebarAreaDetailButton.getScene().getWindow());
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

    public AnchorPane getAnchorPane() {
        return rebarPageAnchorPane;
    }

    public void createRebarCasesPage(Rebar rebar){
        new RebarCasesPageCreator(rebar);
    }

    public void injectMainController(MainAccessController mainAccessController) {
        mMainAccessController = mainAccessController;
    }

    public boolean isShowDeflectionPage() {
        return mShowDeflectionPage.get();
    }

    public BooleanProperty showDeflectionPageProperty() {
        return mShowDeflectionPage;
    }
}
