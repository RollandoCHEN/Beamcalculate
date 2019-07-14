package com.beamcalculate.model.calculator;

import com.beamcalculate.model.calculator.span_function.AbstractSpanMoment;

import java.util.Map;
import java.util.function.Function;

import static com.beamcalculate.enums.CombinationCoef.*;
import static com.beamcalculate.model.MyMethods.round;


public class ELSCombination {
    private AbstractSpanMoment mSpanMomentFunction;
    private double mMomentBeforeCombination;
    private double mMomentAfterCombination;

    public ELSCombination(AbstractSpanMoment spanMomentFunction) {
        mSpanMomentFunction = spanMomentFunction;
    }

    public double getUltMmtOnSpanUnderQuasiPermanentLoad(
            double x, int spanId
    ) {
        Map<Integer, Function<Double, Double>> loadCaseMomentFunctionMap;
        mMomentAfterCombination = 0;
        if (spanId != 0) {
            double maxX = mSpanMomentFunction.getCalculateSpanLengthMap().get(spanId);
            double roundedX = round(x, 2);
            if (roundedX > maxX) { x = maxX; }
            loadCaseMomentFunctionMap = mSpanMomentFunction.getSpanMomentFunctionMap().get(spanId);
            double finalX = x;
            loadCaseMomentFunctionMap.forEach((loadCase, momentFunction) -> {
                mMomentBeforeCombination = momentFunction.apply(finalX);
                if (loadCase == 0) {
                    mMomentAfterCombination += G_ELS_COEF.getValue() * mMomentBeforeCombination;
                } else {
                    if (mMomentBeforeCombination > 0) {
                        mMomentAfterCombination += Q_ELU_UNFAVORABLE_COEF.getValue() * mMomentBeforeCombination;
                    } else {
                        mMomentAfterCombination += 0;
                    }
                }
            });
        }
        return mMomentAfterCombination;
    }


    public AbstractSpanMoment getSpanMomentFunction() {
        return mSpanMomentFunction;
    }

}
