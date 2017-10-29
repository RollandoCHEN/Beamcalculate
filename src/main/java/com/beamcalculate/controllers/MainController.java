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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.*;


public class MainController implements Initializable {
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
    @FXML private Button graphGenerate_button;


    private Geometry newGeometry = new Geometry();
    private Load newLoad = new Load();
    private Material newMaterial = new Material();

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
        // set parameter name for double property in order to show the correct parameter name in the exported word document
        Geometry.sectionWidthProperty().setParameterName(resources.getString("parameter.sectionWidth"));
        Geometry.sectionHeightProperty().setParameterName(resources.getString("parameter.sectionHeight"));
        Geometry.slabThicknessProperty().setParameterName(resources.getString("parameter.slabThickness"));
        Geometry.perpendicularSpacingProperty().setParameterName(resources.getString("parameter.perpendicularSpacing"));
        Geometry.effectiveHeightProperty().setParameterName(resources.getString("parameter.effectiveHeight"));

        Load.gTmProperty().setParameterName(resources.getString("parameter.deadLoad"));
        Load.qTmProperty().setParameterName(resources.getString("parameter.liveLoad"));
        Load.gMNmProperty().setParameterName(resources.getString("parameter.deadLoad"));
        Load.qMNmProperty().setParameterName(resources.getString("parameter.liveLoad"));

        Material.fckProperty().setParameterName(resources.getString("parameter.fck"));
        Material.fykProperty().setParameterName(resources.getString("parameter.fyk"));
        Material.fcdProperty().setParameterName(resources.getString("parameter.fcd"));
        Material.fydProperty().setParameterName(resources.getString("parameter.fyd"));
        Material.ductibilityClassProperty().setParameterName(resources.getString("parameter.ductilityClass"));
        Material.steelUltimateStrainProperty().setParameterName(resources.getString("result.moment.paraName.steelUltimateStrain"));

        allTextField.addAll(Arrays.asList(
                equalSupportWidth_tf, equalSpanLength_tf,
                sectionWidth_tf, sectionHeight_tf, perpendicularSpacing_tf, slabThickness_tf,
                permanentLoad_tf, variableLoad_tf, fck_tf, fyk_tf
        ));
        addRealNumberValidation(allTextField);

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
        mInputValueGetter.getInputValue(permanentLoad_tf, newLoad.gTmProperty());
        mInputValueGetter.getInputValue(variableLoad_tf, newLoad.qTmProperty());
        mInputValueGetter.getInputValue(fck_tf, newMaterial.fckProperty());
        mInputValueGetter.getInputValue(fyk_tf, newMaterial.fykProperty());
        mInputValueGetter.getInputValue(ductibilityClass_chcb, newMaterial.ductibilityClassProperty());
        if (isOnTSection()) {
            mInputValueGetter.getInputValue(slabThickness_tf, Geometry.slabThicknessProperty());
            mInputValueGetter.getInputValue(perpendicularSpacing_tf, Geometry.perpendicularSpacingProperty());
        }
    }

    private void calculateMoments(){
        mSupportMomentCaquot = new SupportMomentCaquot(newGeometry, newLoad);
        mSpanMomentFunctionCaquot = new SpanMomentFunction(mSupportMomentCaquot);
        mSupportMoment3Moment = new SupportMoment3Moment(newGeometry, newLoad);
        mSpanMomentFunction3Moment = new SpanMomentFunction(mSupportMoment3Moment);
        if (mConditionVerifier.isVerified()) {
            mSupportMomentForfaitaire = new SupportMomentForfaitaire(newGeometry, newLoad);
            mSpanMomentFunctionForfaitaire = new SpanMomentFunction(mSupportMomentForfaitaire);
        }
    }

    @FXML
    public void clickOnTSectionCheck(MouseEvent mouseEvent) {
        onTSection_chkb.selectedProperty().setValue(!onTSection_chkb.isSelected());
    }

    @FXML
    private void generateGeometryDiagram(ActionEvent actionEvent) {

        mInputValueGetter.getInputValue(numSpans_chcb, newGeometry.numSpanProperty());
        double hGapValue = (880-newGeometry.getNumSpan()*69)/newGeometry.getNumSpan();

        spansLength_gp.getChildren().clear();
        supportsWidth_gp.getChildren().clear();
        newGeometry.spansLengthMap().clear();
        newGeometry.supportWidthMap().clear();

        mInputTextFieldsTreater.addTextFieldToGrid(
                newGeometry.getNumSpan(), hGapValue,
                equalSpan_chkb, equalSpanLength_tf,
                spansLength_gp
        );

        beamDiagram.setImage(getBeamDiagram(newGeometry.getNumSpan()));

        mInputTextFieldsTreater.addTextFieldToGrid(
                newGeometry.getNumSupport(), hGapValue,
                equalSupport_chkb, equalSupportWidth_tf,
                supportsWidth_gp
        );

//        bind graph generating button to the text fields
        graphGenerate_button.disableProperty().bind(
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
    private void GenerateGraph(ActionEvent actionEvent) {
        getInputs();
        mInputValueGetter.showInputWarning();
        mConditionVerifier = new ForfaitaireConditionVerifier(newGeometry, newLoad);
        calculateMoments();
        MomentLineChart lineChart;
        if (mConditionVerifier.isVerified()) {
            lineChart = new MomentLineChart(
                    mSpanMomentFunctionCaquot,
                    mSpanMomentFunctionForfaitaire,
                    mSpanMomentFunction3Moment
            );
        } else {
            lineChart = new MomentLineChart(
                    mSpanMomentFunctionCaquot,
                    mSpanMomentFunction3Moment
            );
            Set<String> messageInputSet = mConditionVerifier.getInvalidatedConditions();
            new WarningMessage(messageInputSet, "warning.content.conditionWarning");
        }
    }

    @FXML
    private void DEBUG(ActionEvent actionEvent) throws Exception {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Document Word (*.docx)", "*.docx"));

        File savedFile = fileChooser.showSaveDialog(graphGenerate_button.getScene().getWindow());

        if(savedFile != null){
            //Blank Document
            XWPFDocument document = new XWPFDocument();
            //Write the Document in file system
            FileOutputStream out = new FileOutputStream(savedFile);

            //create Paragraph
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(sectionHeight_tf.getParameterName() + " : " + Geometry.getSectionHeight());
            document.write(out);

            //Close document
            out.close();
        }else{
            System.out.println("No Directory selected");
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
}
