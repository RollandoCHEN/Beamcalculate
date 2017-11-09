package com.beamcalculate.controllers;

import com.beamcalculate.model.custom_alert.WarningMessage;
import com.beamcalculate.model.page_manager.InputControllerAdder;
import com.beamcalculate.model.page_manager.InputTextFieldsTreater;
import com.beamcalculate.model.page_manager.InputValueGetter;
import com.beamcalculate.model.custom_node.NamedChoiceBox;
import com.beamcalculate.model.custom_node.NamedTextField;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction;
import com.beamcalculate.model.calculate.support_moment.ForfaitaireConditionVerifier;
import com.beamcalculate.model.calculate.support_moment.SupportMomentCaquot;
import com.beamcalculate.model.calculate.support_moment.SupportMoment3Moment;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Inputs;
import com.beamcalculate.model.entites.Load;
import com.beamcalculate.model.entites.Material;
import com.beamcalculate.model.calculate.support_moment.SupportMomentForfaitaire;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;

import static com.beamcalculate.model.custom_alert.WarningMessage.WarningMessageOption.WITHOUT_CONFIRM;


public class InputPageController implements Initializable {
    @FXML private AnchorPane inputPageAnchorPane;
    @FXML private CheckBox onTSection_chkb;
    @FXML private CheckBox sampleInputs_chkb;
    @FXML private ChoiceBox<Integer> numSpans_chcb;
    @FXML private CheckBox equalSupport_chkb;
    @FXML private CheckBox equalSpan_chkb;
    @FXML private GridPane spansLength_gp;
    @FXML private ImageView beamDiagram;
    @FXML private GridPane supportsWidth_gp;
    @FXML private NamedTextField equalSupportWidth_tf;
    @FXML private NamedTextField equalSpanLength_tf;
    @FXML private NamedTextField sectionWidth_tf;
    @FXML private NamedTextField sectionHeight_tf;
    @FXML private NamedTextField perpendicularSpacing_tf;
    @FXML private NamedTextField slabThickness_tf;
    @FXML private NamedTextField permanentLoad_tf;
    @FXML private NamedTextField variableLoad_tf;
    @FXML private NamedTextField fck_tf;
    @FXML private NamedTextField fyk_tf;
    @FXML private NamedChoiceBox<String> ductibilityClass_chcb;
    @FXML private Button diagramGenerate_button;


    private Inputs mInputs;
    private Geometry mGeometry = new Geometry();

    private SupportMomentCaquot mSupportMomentCaquot;
    public static SupportMoment3Moment mSupportMoment3Moment;
    private SupportMomentForfaitaire mSupportMomentForfaitaire;

    private SpanMomentFunction mSpanMomentFunctionCaquot;
    private SpanMomentFunction mSpanMomentFunction3Moment;
    private SpanMomentFunction mSpanMomentFunctionForfaitaire;

    private BooleanProperty notEqualSpan = new SimpleBooleanProperty(true);
    private BooleanProperty notEqualSupport = new SimpleBooleanProperty(true);

    private BooleanProperty notOnTSection = new SimpleBooleanProperty(true);
    private static BooleanProperty onTSection = new SimpleBooleanProperty(true);

    private static BooleanProperty isDisabledRebarCalculate = new SimpleBooleanProperty(true);

    private InputControllerAdder mRealNumberControllerAdder = new InputControllerAdder();
    private InputTextFieldsTreater mInputTextFieldsTreater = new InputTextFieldsTreater();
    private InputValueGetter mInputValueGetter = new InputValueGetter();
    private ForfaitaireConditionVerifier mConditionVerifier;

    private MainAccessController mMainAccessController;
    private BooleanProperty mNewInput = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addChangingStatusListenerTo(onTSection_chkb, sampleInputs_chkb, equalSupport_chkb, equalSpan_chkb);
        addChangingStatusListenerTo(numSpans_chcb, ductibilityClass_chcb);
        addChangingStatusListenerTo(
                equalSupportWidth_tf, equalSpanLength_tf, sectionWidth_tf, sectionHeight_tf,
                perpendicularSpacing_tf, slabThickness_tf,
                fck_tf, fyk_tf, permanentLoad_tf, variableLoad_tf
        );
        addChangingStatusListenerTo(spansLength_gp, supportsWidth_gp);

         /* set the parameter name for the text fields and choice box
          * in order to show the correct parameter name in the missing param warning message*/
        ductibilityClass_chcb.setParameterName(resources.getString("parameter.ductilityClass"));
        sectionWidth_tf.setParameterName(resources.getString("parameter.sectionWidth"));
        sectionHeight_tf.setParameterName(resources.getString("parameter.sectionHeight"));
        perpendicularSpacing_tf.setParameterName(resources.getString("parameter.perpendicularSpacing"));
        slabThickness_tf.setParameterName(resources.getString("parameter.slabThickness"));
        permanentLoad_tf.setParameterName(resources.getString("parameter.deadLoad"));
        variableLoad_tf.setParameterName(resources.getString("parameter.liveLoad"));
        fck_tf.setParameterName(resources.getString("parameter.fck"));
        fyk_tf.setParameterName(resources.getString("parameter.fyk"));

        mRealNumberControllerAdder.addRealNumberControllerTo(
                Arrays.asList(
                        equalSupportWidth_tf, equalSpanLength_tf,
                        sectionWidth_tf, sectionHeight_tf, perpendicularSpacing_tf, slabThickness_tf,
                        fck_tf, fyk_tf
                )
        );
        mRealNumberControllerAdder.addRealNumberControllerTo(true, permanentLoad_tf, variableLoad_tf);

        // T-shaped section treatment
        notOnTSection.bind(Bindings.not(onTSection_chkb.selectedProperty()));
        onTSection.bind(onTSection_chkb.selectedProperty());

        // Sample data check box treatment
        sampleInputs_chkb.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue){
                onTSection_chkb.setSelected(true);
                numSpans_chcb.getSelectionModel().select(2);
                equalSpan_chkb.setSelected(false);
                equalSupport_chkb.setSelected(false);
                sectionWidth_tf.setText("0.4");
                sectionHeight_tf.setText("0.6");
                slabThickness_tf.setText("0.18");
                perpendicularSpacing_tf.setText("3");
                permanentLoad_tf.setText("4.2");
                variableLoad_tf.setText("5.4");
                fck_tf.setText("25");
                fyk_tf.setText("500");
                ductibilityClass_chcb.getSelectionModel().select("B");
                double[] spanLengths = {5.2, 4.5, 6.1};
                double[] supportWidths = {0.25, 0.20, 0.20, 0.3};
                for (int i=0; i<numSpans_chcb.getValue();i++){
                    TextField textField = (TextField)spansLength_gp.getChildren().get(i);
                    textField.setText(String.valueOf(spanLengths[i]));
                }
                for (int i=0; i<numSpans_chcb.getValue()+1;i++){
                    TextField textField = (TextField)supportsWidth_gp.getChildren().get(i);
                    textField.setText(String.valueOf(supportWidths[i]));
                }
            } else {
                onTSection_chkb.setSelected(false);
                numSpans_chcb.getSelectionModel().clearSelection();
                sectionWidth_tf.clear();
                sectionHeight_tf.clear();
                slabThickness_tf.clear();
                perpendicularSpacing_tf.clear();
                permanentLoad_tf.clear();
                variableLoad_tf.clear();
                fck_tf.clear();
                fyk_tf.clear();
                spansLength_gp.getChildren().forEach(node -> ((TextField) node).clear());
                supportsWidth_gp.getChildren().forEach(node -> ((TextField) node).clear());
            }
        }));

        // equal span_function or equal support_moment treatment
        notEqualSpan.bind(Bindings.not(equalSpan_chkb.selectedProperty()));
        notEqualSupport.bind(Bindings.not(equalSupport_chkb.selectedProperty()));

        equalSpan_chkb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                mInputTextFieldsTreater.bindTextProperty(equalSpanLength_tf, spansLength_gp);
            } else {
                mInputTextFieldsTreater.unbindTextProperty(spansLength_gp);
            }
        });

        equalSupport_chkb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                mInputTextFieldsTreater.bindTextProperty(equalSupportWidth_tf, supportsWidth_gp);
            } else {
                mInputTextFieldsTreater.unbindTextProperty(supportsWidth_gp);
            }
        });
    }

    private void addChangingStatusListenerTo(CheckBox...checkBoxes){
        for (CheckBox checkBox : checkBoxes) {
            addListener(checkBox.selectedProperty());
        }
    }
    private void addChangingStatusListenerTo(ChoiceBox...choiceBoxes){
        for (ChoiceBox choiceBox : choiceBoxes) {
            addListener(choiceBox.valueProperty());
        }
    }
    private void addChangingStatusListenerTo(TextField...textFields){
        for (TextField textField : textFields) {
            addListener(textField.textProperty());
        }
    }
    private void addChangingStatusListenerTo(GridPane... gridPanes){
        for (GridPane gridPane : gridPanes){
            gridPane.getChildren().forEach(node -> {
                TextInputControl textField = (TextInputControl) node;
                addListener(textField.textProperty());
            });
        }
    }

    private void addListener(ObservableValue goalProperty){
        goalProperty.addListener((observable -> {
            mNewInput.set(true);
            mMainAccessController.getShowRebarPageProperty().setValue(false);
        }));
    }

    private Image getBeamDiagram(int numSpan){
        StringBuilder url = new StringBuilder();
        switch (numSpan){
            case 1: url.append("image/1span.png");
                break;
            case 2: url.append("image/2spans.png");
                break;
            case 3: url.append("image/3spans.png");
                break;
            case 4: url.append("image/4spans.png");
                break;
            case 5: url.append("image/5spans.png");
                break;
            case 6: url.append("image/6spans.png");
                break;
        }
        return new Image(url.toString());
    }

    private void getInputs(){
        final Load load = new Load();
        final Material material = new Material();
        mInputValueGetter.getInputValue(spansLength_gp, mGeometry.spansLengthMap());
        mInputValueGetter.getInputValue(supportsWidth_gp, mGeometry.supportWidthMap());
        mInputValueGetter.getInputValue(sectionHeight_tf, mGeometry.sectionHeightProperty());
        mInputValueGetter.getInputValue(sectionWidth_tf, mGeometry.sectionWidthProperty());
        mInputValueGetter.getInputValue(permanentLoad_tf, load.gTmProperty());
        mInputValueGetter.getInputValue(variableLoad_tf, load.qTmProperty());
        mInputValueGetter.getInputValue(fck_tf, material.fckProperty());
        mInputValueGetter.getInputValue(fyk_tf, material.fykProperty());
        mInputValueGetter.getInputValue(ductibilityClass_chcb, material.ductibilityClassProperty());
        mInputValueGetter.getInputValue(onTSection_chkb, mGeometry.onTSectionProperty());
        if (isOnTSection()) {
            mInputValueGetter.getInputValue(slabThickness_tf, mGeometry.slabThicknessProperty());
            mInputValueGetter.getInputValue(perpendicularSpacing_tf, mGeometry.perpendicularSpacingProperty());
        }
        mInputs = new Inputs(mGeometry, load, material);
    }

    private void calculateMoments(){
        mSupportMomentCaquot = new SupportMomentCaquot(mInputs);
        mSpanMomentFunctionCaquot = new SpanMomentFunction(mSupportMomentCaquot);
        mSupportMoment3Moment = new SupportMoment3Moment(mInputs);
        mSpanMomentFunction3Moment = new SpanMomentFunction(mSupportMoment3Moment);
        if (mConditionVerifier.isVerified()) {
            mSupportMomentForfaitaire = new SupportMomentForfaitaire(mInputs);
            mSpanMomentFunctionForfaitaire = new SpanMomentFunction(mSupportMomentForfaitaire);
        }
    }

    @FXML
    private void generateGeometryDiagram(ActionEvent actionEvent) {
        if (!numSpans_chcb.getSelectionModel().isEmpty()) {
            mInputValueGetter.getInputValue(numSpans_chcb, mGeometry.numSpanProperty());
            double hGapValue = (880 - mGeometry.getNumSpan() * 69) / mGeometry.getNumSpan();

            spansLength_gp.getChildren().clear();
            supportsWidth_gp.getChildren().clear();
            mGeometry.spansLengthMap().clear();
            mGeometry.supportWidthMap().clear();

            mInputTextFieldsTreater.addTextFieldToGrid(
                    mGeometry.getNumSpan(), hGapValue,
                    equalSpan_chkb, equalSpanLength_tf,
                    spansLength_gp
            );

            beamDiagram.setImage(getBeamDiagram(mGeometry.getNumSpan()));

            mInputTextFieldsTreater.addTextFieldToGrid(
                    mGeometry.getNumSupport(), hGapValue,
                    equalSupport_chkb, equalSupportWidth_tf,
                    supportsWidth_gp
            );

//        bind graph generating button to the text fields
            diagramGenerate_button.disableProperty().bind(
                    mInputTextFieldsTreater.bindIsEmptyPropertyWithOr(permanentLoad_tf, variableLoad_tf)
                            .or(mInputTextFieldsTreater.bindIsEmptyPropertyWithOr(spansLength_gp))
                            .or(mInputTextFieldsTreater.bindIsEmptyPropertyWithOr(supportsWidth_gp))
            );
//        bind rebar calculate button to the text fields
            isDisabledRebarCalculate.bind(
                    mInputTextFieldsTreater.bindIsEmptyPropertyWithOr(sectionWidth_tf, sectionHeight_tf, fck_tf, fyk_tf)
                            .or(Bindings.isNull(ductibilityClass_chcb.valueProperty()))
            );
        }
    }

    @FXML
    private void getMomentChartPage(ActionEvent actionEvent) {
        getInputs();
        if (mInputValueGetter.continueAfterShowingWarning()) {
            mConditionVerifier = new ForfaitaireConditionVerifier(mInputs);
            calculateMoments();
            if (mConditionVerifier.isVerified()) {
                mMainAccessController.createMomentLineChart(
                        mSpanMomentFunctionCaquot,
                        mSpanMomentFunctionForfaitaire,
                        mSpanMomentFunction3Moment
                );
            } else {
                mMainAccessController.createMomentLineChart(
                        mSpanMomentFunctionCaquot,
                        mSpanMomentFunction3Moment
                );
                Set<String> messageInputSet = mConditionVerifier.getInvalidatedConditions();
                new WarningMessage(messageInputSet, "warning.content.conditionWarning", WITHOUT_CONFIRM);
            }

            mNewInput.setValue(false);
            mMainAccessController.getMomentPageButton().setSelected(true);
        }
    }

    public boolean isNotEqualSpan() {
        return notEqualSpan.get();
    }

    public BooleanProperty notEqualSpanProperty() {
        return notEqualSpan;
    }

    public boolean isNotEqualSupport() {
        return notEqualSupport.get();
    }

    public BooleanProperty notEqualSupportProperty() {
        return notEqualSupport;
    }

    public static boolean isDisabledRebarCalculate() {
        return isDisabledRebarCalculate.get();
    }

    public static BooleanProperty isDisabledRebarCalculateProperty() {
        return isDisabledRebarCalculate;
    }

    public boolean isNotOnTSection() {
        return notOnTSection.get();
    }

    public BooleanProperty notOnTSectionProperty() {
        return notOnTSection;
    }

    public static boolean isOnTSection() {
        return onTSection.get();
    }

    public BooleanProperty onTSectionProperty() {
        return onTSection;
    }

    public AnchorPane getAnchorPane() { return inputPageAnchorPane; }

    public boolean hasNewInput() {
        return mNewInput.get();
    }

    public BooleanProperty newInputProperty() {
        return mNewInput;
    }

    //Get main controller
    public void injectMainController(MainAccessController mainAccessController) {
        mMainAccessController = mainAccessController;
    }

    //Get nodes from moment page controller through the main controller
}
