package com.beamcalculate.model.calculator.span_function;

import com.beamcalculate.controllers.InputPageController;
import com.beamcalculate.enums.UltimateCase;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Inputs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.CombinationCoef.G_ELU_UNFAVORABLE_COEF;
import static com.beamcalculate.enums.CombinationCoef.Q_ELU_UNFAVORABLE_COEF;
import static com.beamcalculate.enums.UltimateCase.MAX_MOMENT_TAG;
import static com.beamcalculate.enums.UltimateCase.MIN_MOMENT_TAG;
import static com.beamcalculate.model.MyMethods.round;

public class SpanMomentFunction_SpecialLoadCase extends AbstractSpanMoment {

    public SpanMomentFunction_SpecialLoadCase(Map<Integer, Map<Integer, Double>> specialLoadCaseSupportMomentMap, Inputs inputs) {
        mInputs = inputs;
        mGeometry = inputs.getGeometry();
        mLoad = inputs.getLoad();
        // add spanId and Map to spanMomentMap

        for (int spanId = 1; spanId < mGeometry.getNumSpan() + 1; spanId++) {
            Map<Integer, Function<Double, Double>> loadCaseMomentFunctionMap = new HashMap();
            loadCaseMomentFunctionMap.put(1, null);
            loadCaseMomentFunctionMap.put(2, null);
            for (int loadCase = 2; loadCase < mGeometry.getNumSupport(); loadCase++) {
                loadCaseMomentFunctionMap.put(loadCase + 10, null);
            }
            mSpanMomentFunctionMap.put(spanId, loadCaseMomentFunctionMap);
        }

        mSpanMomentFunctionMap.forEach((spanId, loadCaseMomentFunctionMap) ->
                loadCaseMomentFunctionMap.forEach((loadCase, momentFunction) -> {

                    double leftSupportMoment = specialLoadCaseSupportMomentMap.get(spanId).get(loadCase);
                    double rightSupportMoment = specialLoadCaseSupportMomentMap.get(spanId + 1).get(loadCase);
                    double thisSpanLength = mGeometry.getEffectiveSpansLengthMap().get(spanId);
                    double thisSpanLoad;

                    if (loadCase < 10) {                 // loadCase == 1, loadCase == 2
                        if (spanId % 2 == loadCase % 2) {
                            thisSpanLoad = G_ELU_UNFAVORABLE_COEF.getValue() * mLoad.getGMNm() + Q_ELU_UNFAVORABLE_COEF.getValue() * mLoad.getQMNm();
                        } else {
                            thisSpanLoad = G_ELU_UNFAVORABLE_COEF.getValue() * mLoad.getGMNm();
                        }
                    } else {
                        if (spanId == loadCase - 10 - 1 || spanId == loadCase - 10) {
                            thisSpanLoad = G_ELU_UNFAVORABLE_COEF.getValue() * mLoad.getGMNm() + Q_ELU_UNFAVORABLE_COEF.getValue() * mLoad.getQMNm();
                        } else {
                            thisSpanLoad = G_ELU_UNFAVORABLE_COEF.getValue() * mLoad.getGMNm();
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
        if (spanId != 0) {
            double maxX = round(mGeometry.getEffectiveSpansLengthMap().get(spanId), 2);
            double roundedX = round(x, 2);
            if (roundedX <= maxX) {
                loadCaseMomentFunctionMap = mSpanMomentFunctionMap.get(spanId);
                for (Map.Entry<Integer, Function<Double, Double>> entry : loadCaseMomentFunctionMap.entrySet()) {
                    if (ultimateCase == MAX_MOMENT_TAG) {
                        finalMoment = Math.max(finalMoment, entry.getValue().apply(x));
                    } else {
                        finalMoment = Math.min(finalMoment, entry.getValue().apply(x));
                    }
                }
            }
        }

        return finalMoment;
    }

    public double getUltimateMomentValue(
            UltimateCase ultimateCase
    ) {
        double ultimateMoment = 0;
        boolean compareCondition = true;

        for (int spanId = 1; spanId < mGeometry.getNumSpan() + 1; spanId++) {
            double moment = getUltimateMomentValueOfSpan(spanId, ultimateCase);
            switch (ultimateCase) {
                case MAX_MOMENT_TAG:
                    compareCondition = moment > ultimateMoment;
                    break;
                case MIN_MOMENT_TAG:
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
                case MAX_MOMENT_TAG:
                    compareCondition = moment > ultimateMoment;
                    break;
                case MIN_MOMENT_TAG:
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
        if (supportId == mGeometry.getNumSupport()) {
            spanId = supportId - 1;
            xOfSupport = this.getCalculateSpanLengthMap().get(spanId);
        } else {
            spanId = supportId;
            xOfSupport = 0;
        }
        return this.getUltimateMomentForSpecialLoadCaseAtXOfSpan(xOfSupport, spanId, MIN_MOMENT_TAG);
    }

    @Override
    public String getMethod() {
        //TODO To generate getMethod function
        return TROIS_MOMENT_R.getMethodName();
    }

    @Override
    public Map<Integer, Double> getCalculateSpanLengthMap() {
        return InputPageController.mSupportMoment3Moment.getCalculateSpanLengthMap();
    }

    public Geometry getGeometry() {
        return mGeometry;
    }
}
