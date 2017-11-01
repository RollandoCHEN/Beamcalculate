package com.beamcalculate.controllers;

import com.beamcalculate.custom.alert.WarningMessage;
import com.beamcalculate.custom.input_manager.InputControllerAdder;
import com.beamcalculate.custom.input_manager.InputTextFieldsTreater;
import com.beamcalculate.custom.input_manager.InputValueGetter;
import com.beamcalculate.custom.node.NamedChoiceBox;
import com.beamcalculate.custom.node.NamedTextField;
import com.beamcalculate.model.calculate.span_function.SpanMomentFunction;
import com.beamcalculate.model.calculate.support_moment.ForfaitaireConditionVerifier;
import com.beamcalculate.model.calculate.support_moment.SupportMomentCaquot;
import com.beamcalculate.model.calculate.support_moment.SupportMoment3Moment;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Load;
import com.beamcalculate.model.entites.Material;
import com.beamcalculate.model.result.MomentLineChart;
import com.beamcalculate.model.calculate.support_moment.SupportMomentForfaitaire;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;


public class InputPageController implements Initializable {
    @FXML private AnchorPane anchorPane;
    @FXML private CheckBox onTSection_chkb;
    @FXML private ChoiceBox numSpans_chcb;
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
    @FXML private NamedChoiceBox ductibilityClass_chcb;
    @FXML private Button diagramGenerate_button;


    private Geometry mGeometry = new Geometry();
    private Load mLoad = new Load();
    private Material mMaterial = new Material();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set the parameter name for the text fields and choice box in order to show the correct parameter name in the missing param warning message
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
                        permanentLoad_tf, variableLoad_tf, fck_tf, fyk_tf
                )
        );

        // T-shaped section treatment
        notOnTSection.bind(Bindings.not(onTSection_chkb.selectedProperty()));
        onTSection.bind(onTSection_chkb.selectedProperty());

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
        mInputValueGetter.getInputValue(spansLength_gp, Geometry.spansLengthMap());
        mInputValueGetter.getInputValue(supportsWidth_gp, Geometry.supportWidthMap());
        mInputValueGetter.getInputValue(sectionHeight_tf, Geometry.sectionHeightProperty());
        mInputValueGetter.getInputValue(sectionWidth_tf, Geometry.sectionWidthProperty());
        mInputValueGetter.getInputValue(permanentLoad_tf, mLoad.gTmProperty());
        mInputValueGetter.getInputValue(variableLoad_tf, mLoad.qTmProperty());
        mInputValueGetter.getInputValue(fck_tf, mMaterial.fckProperty());
        mInputValueGetter.getInputValue(fyk_tf, mMaterial.fykProperty());
        mInputValueGetter.getInputValue(ductibilityClass_chcb, mMaterial.ductibilityClassProperty());
        if (isOnTSection()) {
            mInputValueGetter.getInputValue(slabThickness_tf, Geometry.slabThicknessProperty());
            mInputValueGetter.getInputValue(perpendicularSpacing_tf, Geometry.perpendicularSpacingProperty());
        }
    }

    private void calculateMoments(){
        mSupportMomentCaquot = new SupportMomentCaquot(mGeometry, mLoad);
        mSpanMomentFunctionCaquot = new SpanMomentFunction(mSupportMomentCaquot);
        mSupportMoment3Moment = new SupportMoment3Moment(mGeometry, mLoad);
        mSpanMomentFunction3Moment = new SpanMomentFunction(mSupportMoment3Moment);
        if (mConditionVerifier.isVerified()) {
            mSupportMomentForfaitaire = new SupportMomentForfaitaire(mGeometry, mLoad);
            mSpanMomentFunctionForfaitaire = new SpanMomentFunction(mSupportMomentForfaitaire);
        }
    }

    @FXML
    private void generateGeometryDiagram(ActionEvent actionEvent) {

        mInputValueGetter.getInputValue(numSpans_chcb, mGeometry.numSpanProperty());
        double hGapValue = (880- mGeometry.getNumSpan()*69)/ mGeometry.getNumSpan();

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

    @FXML
    private void GenerateDiagram(ActionEvent actionEvent) {
        getInputs();
        mInputValueGetter.showInputWarning();
        mConditionVerifier = new ForfaitaireConditionVerifier(mGeometry, mLoad);
        calculateMoments();
        MomentLineChart lineChart;
        if (mConditionVerifier.isVerified()) {
            lineChart = new MomentLineChart(
                    getSpanNumSpinner(), getMethodsCheckHBox(), getConditionInfoLabel(), getRedistributionCheck(),
                    getConfigurationButton(), getMethodsChoiceBox(), getRebarCalculateButton(), getSpanChoiceBox(),
                    getAbscissaField(), getMomentCalculateButton(), getMaxCaseMomentValue(),getMinCaseMomentValue(),
                    getBorderPaneContainer(),
                    mSpanMomentFunctionCaquot, mSpanMomentFunctionForfaitaire, mSpanMomentFunction3Moment
            );
        } else {
            lineChart = new MomentLineChart(
                    getSpanNumSpinner(), getMethodsCheckHBox(), getConditionInfoLabel(), getRedistributionCheck(),
                    getConfigurationButton(), getMethodsChoiceBox(), getRebarCalculateButton(), getSpanChoiceBox(),
                    getAbscissaField(), getMomentCalculateButton(), getMaxCaseMomentValue(),getMinCaseMomentValue(),
                    getBorderPaneContainer(),
                    mSpanMomentFunctionCaquot, mSpanMomentFunction3Moment
            );
            Set<String> messageInputSet = mConditionVerifier.getInvalidatedConditions();
            new WarningMessage(messageInputSet, "warning.content.conditionWarning");
        }

        mMainAccessController.getMomentPageButton().setDisable(false);
        mMainAccessController.getMomentPageButton().setSelected(true);
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

    public AnchorPane getAnchorPane() { return anchorPane; }

    //Get main controller
    public void injectMainController(MainAccessController mainAccessController) {
        mMainAccessController = mainAccessController;
    }

    //Get nodes from moment page controller through the main controller
    public Spinner<Integer> getSpanNumSpinner(){
        return mMainAccessController.getSpanNumSpinner();
    }

    public HBox getMethodsCheckHBox() {
        return mMainAccessController.getMethodsCheckHBox();
    }

    public Label getConditionInfoLabel() { return mMainAccessController.getConditionInfoLabel(); }

    public CheckBox getRedistributionCheck() {
        return mMainAccessController.getRedistributionCheck();
    }

    public Button getConfigurationButton() {
        return mMainAccessController.getConfigurationButton();
    }

    public ChoiceBox<String> getMethodsChoiceBox() {
        return mMainAccessController.getMethodsChoiceBox();
    }

    public Button getRebarCalculateButton() {
        return mMainAccessController.getRebarCalculateButton();
    }

    public ChoiceBox<Integer> getSpanChoiceBox() {
        return mMainAccessController.getSpanChoiceBox();
    }

    public TextField getAbscissaField() {
        return mMainAccessController.getAbscissaField();
    }

    public Button getMomentCalculateButton() {
        return mMainAccessController.getMomentCalculateButton();
    }

    public Label getMaxCaseMomentValue() {
        return mMainAccessController.getMaxCaseMomentValue();
    }

    public Label getMinCaseMomentValue() {
        return mMainAccessController.getMinCaseMomentValue();
    }

    public BorderPane getBorderPaneContainer(){return mMainAccessController.getBorderPaneContainer();}
}
