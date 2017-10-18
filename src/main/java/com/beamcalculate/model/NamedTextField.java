package com.beamcalculate.model;

import javafx.scene.control.TextField;

public class NamedTextField extends TextField{
    private String mParameterName;

    public String getParameterName() {
        return mParameterName;
    }

    public void setParameterName(String parameterName) {
        mParameterName = parameterName;
    }
}
