package com.beamcalculate.model;

import javafx.beans.property.SimpleStringProperty;

public class NamedStringProperty extends SimpleStringProperty {
    private String mParameterName;

    public String getParameterName() {
        return mParameterName;
    }

    public void setParameterName(String parameterName) {
        mParameterName = parameterName;
    }
}
