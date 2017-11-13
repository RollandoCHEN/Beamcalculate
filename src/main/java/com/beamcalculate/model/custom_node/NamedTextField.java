package com.beamcalculate.model.custom_node;

import com.jfoenix.controls.JFXTextField;

public class NamedTextField extends JFXTextField {
    private String mParameterName;

    public String getParameterName() {
        return mParameterName;
    }

    public void setParameterName(String parameterName) {
        mParameterName = parameterName;
    }
}
