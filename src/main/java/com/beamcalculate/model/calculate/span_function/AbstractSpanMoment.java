package com.beamcalculate.model.calculate.span_function;

import com.beamcalculate.model.calculate.support_moment.SupportMoment;
import com.beamcalculate.model.entites.Geometry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractSpanMoment {
    Geometry mGeometry;
    SupportMoment mSupportMoment;
    Map<Integer, Map<Integer, Function<Double, Double>>> mSpanMomentFunctionMap = new HashMap<>();

    public Map<Integer, Map<Integer, Function<Double, Double>>> getSpanMomentFunctionMap() {
        return mSpanMomentFunctionMap;
    }

    public String getMethod() {
        return mSupportMoment.getMethod();
    }

    public Geometry getGeometry() {
        return mGeometry;
    }

    public Map<Integer, Double> getCalculateSpanLengthMap() {
        return mSupportMoment.getCalculateSpanLengthMap();
    }

    @Override
    public String toString() {
        return getMethod();
    }
}
