package com.beamcalculate.model;

import javafx.beans.property.SimpleDoubleProperty;

public class NamedDoubleProperty extends SimpleDoubleProperty {
    private String mParameterName;

    public String getParameterName() {
        return mParameterName;
    }

    public void setParameterName(String parameterName) {
        mParameterName = parameterName;
    }
}
