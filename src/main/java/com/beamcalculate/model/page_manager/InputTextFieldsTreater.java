package com.beamcalculate.model.page_manager;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;

/**
 * Created by Ruolin on 28/10/2017 for Beamcalculate.
 */
public class InputTextFieldsTreater {

    public BooleanBinding bindIsEmptyPropertyWithOr(TextField... textFields) {
        //      foreach lambda doesn't accept non-final parameter，orConjunction = orConjunction.or(...) could not be used
//      so I used for (custom_node custom_node : gridPane.getChildren())
        BooleanBinding orConjunction = Bindings.isEmpty(new TextField("0").textProperty());

        for (TextField textField : textFields){
            orConjunction = orConjunction.or(Bindings.isEmpty(textField.textProperty()));
        }
        return orConjunction;
    }

    public BooleanBinding bindIsEmptyPropertyWithOr(GridPane gridPane){
//      foreach lambda doesn't accept non-final parameter，orConjunction = orConjunction.or(...) could not be used
//      so I used for (custom_node custom_node : gridPane.getChildren())
        BooleanBinding orConjunction = Bindings.isEmpty(new TextField("Not empty").textProperty());

        for (Node node : gridPane.getChildren()){
            TextInputControl textInputNode = (TextInputControl)node;
            orConjunction = orConjunction.or(Bindings.isEmpty(textInputNode.textProperty()));
        }
        return orConjunction;
    }

    public void bindTextProperty(TextField textField, GridPane gridPane){
        gridPane.getChildren().forEach(node -> {
            TextInputControl textInputNode = (TextInputControl)node;
            textInputNode.textProperty().bind(textField.textProperty());
        });
    }

    public void unbindTextProperty(GridPane gridPane) {
        gridPane.getChildren().forEach(node -> {
            TextInputControl textInputNode = (TextInputControl)node;
            textInputNode.textProperty().unbind();
        });
    }

    public void addTextFieldToGrid(
            int numField, double hGapValue,
            CheckBox checkBox, TextField toBindTextField,
            GridPane goalGridPane
    ){
        InputControllerAdder controllerAdder = new InputControllerAdder();
        for (int i=0;i<numField;i++){
            JFXTextField textField = new JFXTextField();
            textField.setMaxWidth(69);
            textField.getStyleClass().add("text-field-grid");
            String textFieldId = Integer.toString(i+1);
            textField.setId(textFieldId);

            textField.disableProperty().bind(checkBox.selectedProperty());
            if (checkBox.isSelected()){
                textField.textProperty().bind(toBindTextField.textProperty());
            }

            controllerAdder.addRealNumberControllerTo(textField);

            goalGridPane.add(textField,i,0);
            goalGridPane.setHgap(hGapValue);
        }
    }
}
