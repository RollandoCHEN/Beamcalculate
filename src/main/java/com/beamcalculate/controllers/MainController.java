package com.beamcalculate.controllers;

import com.beamcalculate.Main;
import com.beamcalculate.model.NamedChoiceBox;
import com.beamcalculate.model.NamedTextField;
import com.beamcalculate.model.calculate.span.SpanMomentFunction;
import com.beamcalculate.model.calculate.support.SupportMomentCaquot;
import com.beamcalculate.model.calculate.support.SupportMoment3Moment;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Load;
import com.beamcalculate.model.entites.Material;
import com.beamcalculate.model.result.MomentLineChart;
import com.beamcalculate.model.calculate.support.SupportMomentForfaitaire;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

    private Set<String> missingParamWarningSet = new HashSet<>();
    private BooleanProperty notEqualSpan = new SimpleBooleanProperty(true);
    private BooleanProperty notEqualSupport = new SimpleBooleanProperty(true);
    private BooleanProperty notOnTSection = new SimpleBooleanProperty(true);
    private static BooleanProperty onTSection = new SimpleBooleanProperty(true);

    private List<TextField> allTextField = new ArrayList<>();

    private static BooleanProperty isDisabledRebarCalculate = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set parameter name for the text fields and choice box in order to show the correct parameter name in the missing param warning message
        ductibilityClass_chcb.setParameterName(resources.getString("parameter.ductilityClass"));
        sectionWidth_tf.setParameterName(resources.getString("parameter.sectionWidth"));
        sectionHeight_tf.setParameterName(resources.getString("parameter.sectionHeight"));
        perpendicularSpacing_tf.setParameterName(resources.getString("parameter.perpendicularSpacing"));
        slabThickness_tf.setParameterName(resources.getString("parameter.slabThickness"));
        permanentLoad_tf.setParameterName(resources.getString("parameter.liveLoad"));
        variableLoad_tf.setParameterName(resources.getString("parameter.deadLoad"));
        fck_tf.setParameterName(resources.getString("parameter.fck"));
        fyk_tf.setParameterName(resources.getString("parameter.fyk"));

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

        notEqualSpan.bind(Bindings.not(equalSpan_chkb.selectedProperty()));
        notEqualSupport.bind(Bindings.not(equalSupport_chkb.selectedProperty()));

        notOnTSection.bind(Bindings.not(onTSection_chkb.selectedProperty()));
        onTSection.bind(onTSection_chkb.selectedProperty());
    }

    private BooleanBinding turnTextFieldsIsEmptyGridToBooleanBinding(GridPane gridPane){
//    由于foreach的lambda中只能出现final的参数，orConjunction = orConjunction.or(...)不能出现，所以用了for (Node node : gridPane.getChildren())
        BooleanBinding orConjunction = Bindings.isEmpty(permanentLoad_tf.textProperty()).or(Bindings.isEmpty(variableLoad_tf.textProperty()));
        for (Node node : gridPane.getChildren()){
            TextInputControl textInputNode = (TextInputControl)node;
            orConjunction = orConjunction.or(Bindings.isEmpty(textInputNode.textProperty()));
        }
        return orConjunction;
    }

    private void checkIfDisableTextFields(CheckBox checkBox, TextField textField, GridPane gridPane){
        if(checkBox.isSelected()){
            bindTextFieldToTextFieldsGrid(textField, gridPane);
        }else {
            unbindTextFieldToTextFieldsGrid(gridPane);
        }
    }

    private void addRealNumberValidation(TextField textField){
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                if(!textField.getText().matches("\\d+\\.\\d+|\\d+")){
                    //when it not matches the pattern
                    //set the textField empty
                    textField.setText("");
                }
            }
        });
    }

    private void addRealNumberValidation(List<TextField> list){
        list.forEach(this::addRealNumberValidation);
    }

    private void addTextFieldToGrid(
            int numField, double hgapValue,
            CheckBox checkBox, TextField toBindTextField,
            GridPane goalGridPane
    ){
        for (int i=0;i<numField;i++){
            TextField textField = new TextField();
            String textFieldId = Integer.toString(i+1);
            textField.setId(textFieldId);

            textField.disableProperty().bind(checkBox.selectedProperty());
            if (checkBox.isSelected()){
                textField.textProperty().bind(toBindTextField.textProperty());
            }

            addRealNumberValidation(textField);

            goalGridPane.add(textField,i,0);
            goalGridPane.setHgap(hgapValue);
        }
    }


    private void bindTextFieldToTextFieldsGrid(TextField textField, GridPane gridPane){
        gridPane.getChildren().forEach(node -> {
            TextInputControl textInputNode = (TextInputControl)node;
            textInputNode.textProperty().bind(textField.textProperty());
        });
    }

    private void unbindTextFieldToTextFieldsGrid(GridPane gridPane) {
        gridPane.getChildren().forEach(node -> {
            TextInputControl textInputNode = (TextInputControl)node;
            textInputNode.textProperty().unbind();
        });
    }

    private Image getImage(int numSpan){
        StringBuilder url = new StringBuilder("");
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

    private void getInputValue(GridPane sourceGridPane, Map goalMap){
        sourceGridPane.getChildren().forEach(node -> {
            TextInputControl textField = (TextInputControl)node;
            try {
                goalMap.put(Integer.parseInt(textField.getId()),Double.parseDouble(textField.getText()));
            } catch (NumberFormatException e) {
            }
        });
    }

    private void getInputValue(NamedTextField sourceTextField, DoubleProperty goalProperty){
        try {
            goalProperty.set(Double.parseDouble(sourceTextField.getText()));
        } catch (NumberFormatException e) {
            missingParamWarningSet.add(sourceTextField.getParameterName());
        }
    }

    private void getInputValue(NamedChoiceBox sourceChoiceBox, StringProperty goalProperty){
        try {
            goalProperty.set((String)sourceChoiceBox.getValue());
            if(sourceChoiceBox.getValue() == null){
                missingParamWarningSet.add(sourceChoiceBox.getParameterName());
            }
        } catch (Exception e) {
            missingParamWarningSet.add(sourceChoiceBox.getParameterName());
        }
    }

    private void getInputValue(ChoiceBox sourceChoiceBox, IntegerProperty goalProperty){
        try {
            goalProperty.set((Integer)sourceChoiceBox.getValue());
        } catch (Exception e) {
            missingParamWarningSet.add(sourceChoiceBox.getId());
        }
    }

    private void showInputWarning(){
        if(!missingParamWarningSet.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(Main.getBundleText("window.title.warning"));
            alert.setHeaderText(null);
            StringBuffer missingParamNamesStringBuffer = new StringBuffer();
            missingParamWarningSet.forEach (missingParameterName -> missingParamNamesStringBuffer.append("\n- " + missingParameterName));
            String infoMessage = Main.getBundleText("message.inputWarning") + missingParamNamesStringBuffer;
            alert.setContentText(infoMessage);
            alert.showAndWait();
            missingParamWarningSet.clear();
        }
    }

    private void showForfaitaireCondWarning(List<String> invalidedConditions){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(Main.getBundleText("window.title.warning"));
        alert.setHeaderText(null);
        String infoMessage = Main.getBundleText("message.conditionWarning");
        for (String condition : invalidedConditions){
            infoMessage += "- " + condition + "\n";
        }
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    private void getInputs(){
        getInputValue(spansLength_gp, newGeometry.spansLengthMap());
        getInputValue(spansLength_gp, newGeometry.spansLengthMap());
        getInputValue(supportsWidth_gp, newGeometry.supportWidthMap());
        getInputValue(sectionHeight_tf, newGeometry.sectionHeightProperty());
        getInputValue(sectionWidth_tf, newGeometry.sectionWidthProperty());
        getInputValue(permanentLoad_tf, newLoad.gTmProperty());
        getInputValue(variableLoad_tf, newLoad.qTmProperty());
        getInputValue(fck_tf, newMaterial.fckProperty());
        getInputValue(fyk_tf, newMaterial.fykProperty());
        getInputValue(ductibilityClass_chcb, newMaterial.ductibilityClassProperty());
        if (isOnTSection()) {
            getInputValue(slabThickness_tf, newGeometry.slabThicknessProperty());
            getInputValue(perpendicularSpacing_tf, newGeometry.perpendicularSpacingProperty());
        }
    }

    private void calculateMoments(){
        mSupportMomentCaquot = new SupportMomentCaquot(newGeometry, newLoad);
        mSpanMomentFunctionCaquot = new SpanMomentFunction(mSupportMomentCaquot);
        mSupportMoment3Moment = new SupportMoment3Moment(newGeometry, newLoad);
        mSpanMomentFunction3Moment = new SpanMomentFunction(mSupportMoment3Moment);
        mSupportMomentForfaitaire = new SupportMomentForfaitaire(newGeometry, newLoad);
        mSpanMomentFunctionForfaitaire = new SpanMomentFunction(mSupportMomentForfaitaire);
    }

    @FXML
    public void clickOnTSectionCheck(MouseEvent mouseEvent) {
        onTSection_chkb.selectedProperty().setValue(!onTSection_chkb.isSelected());
    }

    @FXML
    private void disableOrEnableSpanLength(ActionEvent actionEvent) {
        checkIfDisableTextFields(equalSpan_chkb, equalSpanLength_tf, spansLength_gp);
    }

    @FXML
    private void disableOrEnableSupportWidth(ActionEvent actionEvent) {
        checkIfDisableTextFields(equalSupport_chkb, equalSupportWidth_tf, supportsWidth_gp);
    }

    @FXML
    private void generateGeometryDiagram(ActionEvent actionEvent) {

        getInputValue(numSpans_chcb, newGeometry.numSpanProperty());
        double hGapValue = (880-newGeometry.getNumSpan()*69)/newGeometry.getNumSpan();

        spansLength_gp.getChildren().clear();
        supportsWidth_gp.getChildren().clear();
        newGeometry.spansLengthMap().clear();
        newGeometry.supportWidthMap().clear();

        addTextFieldToGrid(
                newGeometry.getNumSpan(), hGapValue,
                equalSpan_chkb, equalSpanLength_tf,
                spansLength_gp
        );

        beamDiagram.setImage(getImage(newGeometry.getNumSpan()));

        addTextFieldToGrid(
                newGeometry.getNumSupport(), hGapValue,
                equalSupport_chkb, equalSupportWidth_tf,
                supportsWidth_gp
        );

//        bind graph generating button to the text fields
        graphGenerate_button.disableProperty().bind(
                turnTextFieldsIsEmptyGridToBooleanBinding(spansLength_gp).
                        or(turnTextFieldsIsEmptyGridToBooleanBinding(supportsWidth_gp))
        );
//        bind rabar calculate button to the text fields
        isDisabledRebarCalculate.bind(
                Bindings.isEmpty(sectionWidth_tf.textProperty())
                .or(Bindings.isEmpty(sectionHeight_tf.textProperty()))
                .or(Bindings.isEmpty(fck_tf.textProperty()))
                .or(Bindings.isEmpty(fyk_tf.textProperty()))
                .or(Bindings.isNull(ductibilityClass_chcb.valueProperty()))
        );
    }

    @FXML
    private void GenerateGraph(ActionEvent actionEvent) {
        getInputs();
        showInputWarning();
        calculateMoments();
        MomentLineChart lineChart;
        if(mSupportMomentForfaitaire.isConditionsVerified()){
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
            showForfaitaireCondWarning(mSupportMomentForfaitaire.getInvalidatedConditions());
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
