package com.beamcalculate.custom.node;

import javafx.scene.control.ChoiceBox;

public class NamedChoiceBox <T extends Object> extends ChoiceBox <T> {
    private String mParameterName;

    public String getParameterName() {
        return mParameterName;
    }

    public void setParameterName(String parameterName) {
        mParameterName = parameterName;
    }
}
