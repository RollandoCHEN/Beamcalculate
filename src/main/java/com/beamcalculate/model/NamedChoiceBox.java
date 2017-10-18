package com.beamcalculate.model;

import javafx.scene.control.ChoiceBox;

public class NamedChoiceBox extends ChoiceBox{
    private String mParameterName;

    public String getParameterName() {
        return mParameterName;
    }

    public void setParameterName(String parameterName) {
        mParameterName = parameterName;
    }
}
