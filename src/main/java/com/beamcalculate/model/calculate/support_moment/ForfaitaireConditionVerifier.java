package com.beamcalculate.model.calculate.support_moment;

import static com.beamcalculate.model.LanguageManager.getBundleText;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Inputs;
import com.beamcalculate.model.entites.Load;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ruolin on 29/10/2017 for Beamcalculate.
 */
public class ForfaitaireConditionVerifier {

    private boolean mConditionsVerified;
    private Set<String> mInvalidateConditions = new HashSet<>();

    public ForfaitaireConditionVerifier(Inputs inputs){
        // TODO To add verification for the live load on sol q < 5kN/m²
        boolean liveLoadCond = true;
//        boolean liveLoadCond = load.getQMNm() / 6 < 0.005;
//        if (!liveLoadCond){
//            mInvalidateConditions.add(BeamCalculatorApp.getBundleText("text.conditionA"));
//        }
        boolean live_deadLoadCond = inputs.getLoad().getQMNm() <= 2*inputs.getLoad().getGMNm();
        if(!live_deadLoadCond){
            mInvalidateConditions.add(getBundleText("text.conditionA"));
        }
        boolean spanLengthCond = getSpanLengthCondition(inputs.getGeometry());
        if (!spanLengthCond){
            mInvalidateConditions.add(getBundleText("text.conditionC"));
        }
        mConditionsVerified = liveLoadCond && live_deadLoadCond && spanLengthCond;
    }

    private boolean getSpanLengthCondition(Geometry geometry){
        Map<Integer, Double> spanLengthMap = geometry.spansLengthMap();
        boolean b = false;
        for(int spanId = 1; spanId < geometry.getNumSpan(); spanId++){
            boolean b1 = spanLengthMap.get(spanId) / spanLengthMap.get(spanId + 1) < 1.25;
            boolean b2 = spanLengthMap.get(spanId) / spanLengthMap.get(spanId + 1) > 0.8;
            b = b1 && b2;
        }
        return b;
    }

    public boolean isVerified() {
        return mConditionsVerified;
    }

    public Set<String> getInvalidatedConditions() {
        return mInvalidateConditions;
    }
}
