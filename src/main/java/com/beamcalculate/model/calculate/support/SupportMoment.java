package com.beamcalculate.model.calculate.support;

import com.beamcalculate.Main;

import java.util.HashMap;
import java.util.Map;

public abstract class SupportMoment {
    public enum MethodName{
        CAQUOT("enum.method.caquot"),
        CAQUOT_MINOREE("enum.method.caquot.reduced"),
        FORFAITAIRE("enum.method.forfaitaire"),
        TROIS_MOMENT("enum.method.threeMoment"),
        TROIS_MOMENT_R("enum.method.threeMoment.redistribution");

        private String mBundleTextKey;

        MethodName(String bundleTextKey) {
            mBundleTextKey = bundleTextKey;
        }

        public String getBundleTextKey() {
            return Main.getBundleText(mBundleTextKey);
        }
    }

    protected Map<Integer, Map<Integer, Double>> mSupportMomentMap = new HashMap<>();

    public abstract Map<Integer, Map<Integer, Double>> getSupportMomentMap();

    public abstract double getMomentValueOfSupport(Integer supportId, Integer loadCase);

    public abstract String getMethod();

    public abstract Map<Integer, Double> getCalculateSpanLengthMap();
}
