package com.beamcalculate.model.calculate.span;

import com.beamcalculate.controllers.MainController;
import com.beamcalculate.enums.UltimateCase;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Load;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.CombinCoef.G_UNFAVORABLE_COEF;
import static com.beamcalculate.enums.CombinCoef.Q_UNFAVORABLE_COEF;
import static com.beamcalculate.enums.UltimateCase.MAX;
import static com.beamcalculate.enums.UltimateCase.MIN;

public class SpanMomentFunction_SpecialLoadCase extends AbstractSpanMoment {

    public SpanMomentFunction_SpecialLoadCase(Map<Integer, Map<Integer, Double>> specialLoadCaseSupportMomentMap) {

        // add spanId and Map to spanMomentMap

        for (int spanId = 1; spanId < Geometry.getNumSpan() + 1; spanId++) {
            Map<Integer, Function<Double, Double>> loadCaseMomentFunctionMap = new HashMap();
            loadCaseMomentFunctionMap.put(1, null);
            loadCaseMomentFunctionMap.put(2, null);
            for (int loadCase = 2; loadCase < Geometry.getNumSupport(); loadCase++) {
                loadCaseMomentFunctionMap.put(loadCase + 10, null);
            }
            mSpanMomentFunctionMap.put(spanId, loadCaseMomentFunctionMap);
        }

        mSpanMomentFunctionMap.forEach((spanId, loadCaseMomentFunctionMap) ->
                loadCaseMomentFunctionMap.forEach((loadCase, momentFunction) -> {

                    double leftSupportMoment = specialLoadCaseSupportMomentMap.get(spanId).get(loadCase);
                    double rightSupportMoment = specialLoadCaseSupportMomentMap.get(spanId + 1).get(loadCase);
                    double thisSpanLength = Geometry.getEffectiveSpansLengthMap().get(spanId);
                    double thisSpanLoad;

                    if (loadCase < 10) {                 // loadCase == 1, loadCase == 2
                        if (spanId % 2 == loadCase % 2) {
                            thisSpanLoad = G_UNFAVORABLE_COEF.getValue() * Load.getGMNm() + Q_UNFAVORABLE_COEF.getValue() * Load.getQMNm();
                        } else {
                            thisSpanLoad = G_UNFAVORABLE_COEF.getValue() * Load.getGMNm();
                        }
                    } else {
                        if (spanId == loadCase - 10 - 1 || spanId == loadCase - 10) {
                            thisSpanLoad = G_UNFAVORABLE_COEF.getValue() * Load.getGMNm() + Q_UNFAVORABLE_COEF.getValue() * Load.getQMNm();
                        } else {
                            thisSpanLoad = G_UNFAVORABLE_COEF.getValue() * Load.getGMNm();
                        }
                    }

                    momentFunction = (x -> thisSpanLoad * x * (thisSpanLength - x) / 2 +
                            leftSupportMoment * (1 - x / thisSpanLength) +
                            rightSupportMoment * x / thisSpanLength);

                    loadCaseMomentFunctionMap.put(loadCase, momentFunction);
                }));

    }

    public double getUltimateMomentForSpecialLoadCaseAtXOfSpan(
            double x, int spanId, UltimateCase ultimateCase
    ){
        Map<Integer, Function<Double, Double>> loadCaseMomentFunctionMap;
        double finalMoment = 0;
        loadCaseMomentFunctionMap = mSpanMomentFunctionMap.get(spanId);

        for(Map.Entry<Integer, Function<Double, Double>> entry : loadCaseMomentFunctionMap.entrySet()){
            if (ultimateCase == MAX) {
                finalMoment = Math.max(finalMoment, entry.getValue().apply(x));
            } else {
                finalMoment = Math.min(finalMoment, entry.getValue().apply(x));
            }
        }

        return finalMoment;
    }

    public double getUltimateMomentValue(
            UltimateCase ultimateCase
    ) {
        double ultimateMoment = 0;
        boolean compareCondition = true;

        for (int spanId = 1; spanId < Geometry.getNumSpan() + 1; spanId++) {
            double moment = getUltimateMomentValueOfSpan(spanId, ultimateCase);
            switch (ultimateCase) {
                case MAX:
                    compareCondition = moment > ultimateMoment;
                    break;
                case MIN:
                    compareCondition = moment < ultimateMoment;
                    break;
            }
            if (compareCondition) {
                ultimateMoment = moment;
            }

        }
        return ultimateMoment;
    }

    public double getUltimateMomentValueOfSpan(
            int spanId, UltimateCase ultimateCase
    ) {
        double ultimateMoment = 0;
        boolean compareCondition = true;

        double spanLength = this.getCalculateSpanLengthMap().get(spanId);
        double spanLocalX = 0;


        for (int i = 0; i < 101; i++) {             // Number of data (moment value) is numSection+1
            double moment = this.getUltimateMomentForSpecialLoadCaseAtXOfSpan(spanLocalX, spanId, ultimateCase);

            switch (ultimateCase) {
                case MAX:
                    compareCondition = moment > ultimateMoment;
                    break;
                case MIN:
                    compareCondition = moment < ultimateMoment;
                    break;
            }

            if (compareCondition) {
                ultimateMoment = moment;
            }
            spanLocalX += spanLength / 100;
        }

        return ultimateMoment;
    }

    public double getMinMomentValueOfSupport(
            int supportId
    ) {
        int spanId;
        double xOfSupport;
        if (supportId == Geometry.getNumSupport()) {
            spanId = supportId - 1;
            xOfSupport = this.getCalculateSpanLengthMap().get(spanId);
        } else {
            spanId = supportId;
            xOfSupport = 0;
        }
        return this.getUltimateMomentForSpecialLoadCaseAtXOfSpan(xOfSupport, spanId, MIN);
    }

    @Override
    public String getMethod() {
        //TODO To generate getMethod function
        return TROIS_MOMENT_R.getBundleText();
    }

    @Override
    public Map<Integer, Double> getCalculateSpanLengthMap() {
        return MainController.mSupportMoment3Moment.getCalculateSpanLengthMap();
    }
}
