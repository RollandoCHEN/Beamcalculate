package com.beamcalculate.model.calculate.support;

import com.beamcalculate.Main;

import java.util.HashMap;
import java.util.Map;

public abstract class SupportMoment {
    public enum MethodName{
        CAQUOT(Main.getBundleText("enum.method.caquot")),
        CAQUOT_MINOREE(Main.getBundleText("enum.method.caquot.reduced")),
        FORFAITAIRE(Main.getBundleText("enum.method.forfaitaire")),
        TROIS_MOMENT(Main.getBundleText("enum.method.threeMoment")),
        TROIS_MOMENT_R(Main.getBundleText("enum.method.threeMoment.redistribution"));

        private String mMethodName;

        MethodName(String methodName) {
            mMethodName = methodName;
        }

        public String getMethodName() {
            return mMethodName;
        }
    }

    protected Map<Integer, Map<Integer, Double>> mSupportMomentMap = new HashMap<>();

    public abstract Map<Integer, Map<Integer, Double>> getSupportMomentMap();

    public abstract double getMomentValueOfSupport(Integer supportId, Integer loadCase);

    public abstract String getMethod();

    public abstract Map<Integer, Double> getCalculateSpanLengthMap();
}
