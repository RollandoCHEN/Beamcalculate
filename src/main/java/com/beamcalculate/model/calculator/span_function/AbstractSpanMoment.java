package com.beamcalculate.model.calculator.span_function;

import com.beamcalculate.model.calculator.support_moment.SupportMoment;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Inputs;
import com.beamcalculate.model.entites.Load;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractSpanMoment {
    Inputs mInputs;
    Geometry mGeometry;
    Load mLoad;
    SupportMoment mSupportMoment;
    Map<Integer, Map<Integer, Function<Double, Double>>> mSpanMomentFunctionMap = new HashMap<>();

    public Map<Integer, Map<Integer, Function<Double, Double>>> getSpanMomentFunctionMap() {
        return mSpanMomentFunctionMap;
    }

    public String getMethod() {
        return mSupportMoment.getMethod();
    }

    public Inputs getInputs() {
        return mInputs;
    }

    public Map<Integer, Double> getCalculateSpanLengthMap() {
        return mSupportMoment.getCalculateSpanLengthMap();
    }

    @Override
    public String toString() {
        return getMethod();
    }
}
