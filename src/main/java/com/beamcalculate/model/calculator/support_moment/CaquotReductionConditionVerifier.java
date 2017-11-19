package com.beamcalculate.model.calculator.support_moment;

import com.beamcalculate.model.entites.Load;

/**
 * Created by Ruolin on 29/10/2017 for Beamcalculate.
 */
public class CaquotReductionConditionVerifier {
    private boolean mReductionConditionVerified;

    public CaquotReductionConditionVerifier(Load load){
        // TODO To add verification for the live load on sol q < 5kN/m²
        boolean liveLoadCond = true;
//        boolean liveLoadCond = load.getQMNm() / 6 < 0.005;
        boolean live_deadLoadCond = load.getQMNm() < 2*load.getGMNm();
        mReductionConditionVerified = liveLoadCond && live_deadLoadCond;
    }

    public boolean isVerified() {
        return mReductionConditionVerified;
    }
}
