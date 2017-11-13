package com.beamcalculate.model.custom_node;

import com.jfoenix.controls.JFXComboBox;

public class NamedComboBox<T extends Object> extends JFXComboBox<T> {
    private String mParameterName;

    public String getParameterName() {
        return mParameterName;
    }

    public void setParameterName(String parameterName) {
        mParameterName = parameterName;
    }
}
