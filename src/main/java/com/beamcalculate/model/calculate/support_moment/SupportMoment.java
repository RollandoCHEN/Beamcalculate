package com.beamcalculate.model.calculate.support_moment;

import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Inputs;
import com.beamcalculate.model.entites.Load;

import java.util.HashMap;
import java.util.Map;

public abstract class SupportMoment {
    Map<Integer, Map<Integer, Double>> mSupportMomentMap = new HashMap<>();
    Inputs mInputs;
    Geometry mGeometry;
    Load mLoad;

    public abstract Map<Integer, Map<Integer, Double>> getSupportMomentMap();

    public abstract double getMomentValueOfSupport(Integer supportId, Integer loadCase);

    public abstract String getMethod();

    public abstract Map<Integer, Double> getCalculateSpanLengthMap();

    public Inputs getInputs() {
        return mInputs;
    }
}
