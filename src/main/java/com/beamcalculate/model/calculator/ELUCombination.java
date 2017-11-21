package com.beamcalculate.model.calculator;

import com.beamcalculate.enums.SpecialLoadCase;
import com.beamcalculate.enums.UltimateCase;
import com.beamcalculate.model.calculator.span_function.AbstractSpanMoment;
import com.beamcalculate.model.entites.Geometry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.beamcalculate.enums.CombinCoef.*;
import static com.beamcalculate.enums.SpecialLoadCase.SPAN_MAX;
import static com.beamcalculate.enums.SpecialLoadCase.SUPPORT_MIN;
import static com.beamcalculate.enums.UltimateCase.MIN_MOMENT_TAG;
import static com.beamcalculate.model.MyMethods.round;


public class ELUCombination {
    private AbstractSpanMoment mSpanMomentFunction;
    private Geometry mGeometry;
    private double mMomentBeforeCombination;
    private double mMomentAfterCombination;
    private boolean mUnfavorableCondition;
    private double mMomentUnderLoadCase;

    public ELUCombination(AbstractSpanMoment spanMomentFunction) {
        mSpanMomentFunction = spanMomentFunction;
        mGeometry = spanMomentFunction.getInputs().getGeometry();
    }

    public double getCombinedUltimateMomentAtXOfSpan(
            double x, int spanId, UltimateCase ultimateCase
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
                switch (ultimateCase) {
                    case MAX_MOMENT_TAG:
                        mUnfavorableCondition = mMomentBeforeCombination > 0;
                        break;
                    case MIN_MOMENT_TAG:
                        mUnfavorableCondition = mMomentBeforeCombination < 0;
                        break;
                }
                if (loadCase == 0) {
                    if (mUnfavorableCondition) {
                        mMomentAfterCombination += G_UNFAVORABLE_COEF.getValue() * mMomentBeforeCombination;
                    } else {
                        mMomentAfterCombination += G_FAVORABLE_COEF.getValue() * mMomentBeforeCombination;
                    }
                } else {
                    if (mUnfavorableCondition) {
                        mMomentAfterCombination += Q_UNFAVORABLE_COEF.getValue() * mMomentBeforeCombination;
                    } else {
                        mMomentAfterCombination += Q_FAVORABLE_COEF.getValue() * mMomentBeforeCombination;
                    }
                }
            });
        }
        return mMomentAfterCombination;
    }


    public double getCombinedSpecialLoadCaseMomentAtXOfSpan(
            double x, int spanId,
            SpecialLoadCase specialLoadCase, int loadCaseObject
    ) {
        Map<Integer, Function<Double, Double>> xPositionMomentFunctionMap;
        Map<Integer, Function<Double, Double>> ObjectPositionMomentFunctionMap;
        mMomentUnderLoadCase = 0;

        xPositionMomentFunctionMap = mSpanMomentFunction.getSpanMomentFunctionMap().get(spanId);

        switch (specialLoadCase) {
            case SPAN_MAX:
                ObjectPositionMomentFunctionMap = mSpanMomentFunction.getSpanMomentFunctionMap().get(loadCaseObject);
                break;
            case SUPPORT_MIN:
                if (loadCaseObject == mGeometry.getNumSupport()) {
                    ObjectPositionMomentFunctionMap = mSpanMomentFunction.getSpanMomentFunctionMap().get(loadCaseObject - 1);
                } else {
                    ObjectPositionMomentFunctionMap = mSpanMomentFunction.getSpanMomentFunctionMap().get(loadCaseObject);
                }
                break;
            default:
                ObjectPositionMomentFunctionMap = mSpanMomentFunction.getSpanMomentFunctionMap().get(loadCaseObject);
                break;
        }

        // TODO When span_function numb is 1, there is NullPointerException
        xPositionMomentFunctionMap.forEach((loadCase, momentFunction) -> {
            double loadCaseObjectMoment = 0.0;
            mMomentBeforeCombination = momentFunction.apply(x);
            switch (specialLoadCase) {
                case SPAN_MAX:
                    loadCaseObjectMoment = ObjectPositionMomentFunctionMap.get(loadCase).apply(mGeometry.spansLengthMap().get(loadCaseObject) / 2);
                    mUnfavorableCondition = loadCaseObjectMoment > 0;
                    break;
                case SUPPORT_MIN:
                    if (loadCaseObject != 1 && loadCaseObject != mGeometry.getNumSupport()) {
                        loadCaseObjectMoment = ObjectPositionMomentFunctionMap.get(loadCase).apply(0.0);
                    }
                    mUnfavorableCondition = loadCaseObjectMoment < 0;
                    break;
            }
            if (loadCase == 0) {
                if (mUnfavorableCondition) {
                    mMomentUnderLoadCase += G_UNFAVORABLE_COEF.getValue() * mMomentBeforeCombination;
                } else {
                    mMomentUnderLoadCase += G_FAVORABLE_COEF.getValue() * mMomentBeforeCombination;
                }
            } else {
                if (mUnfavorableCondition) {
                    mMomentUnderLoadCase += Q_UNFAVORABLE_COEF.getValue() * mMomentBeforeCombination;
                } else {
                    mMomentUnderLoadCase += Q_FAVORABLE_COEF.getValue() * mMomentBeforeCombination;
                }
            }
        });
        return mMomentUnderLoadCase;
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

        double spanLength = mSpanMomentFunction.getCalculateSpanLengthMap().get(spanId);
        double spanLocalX = 0;


        for (int i = 0; i < 101; i++) {             // Number of data (moment value) is numSection+1
            double moment = this.getCombinedUltimateMomentAtXOfSpan(spanLocalX, spanId, ultimateCase);

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
            xOfSupport = mSpanMomentFunction.getCalculateSpanLengthMap().get(spanId);
        } else {
            spanId = supportId;
            xOfSupport = 0;
        }
        return this.getCombinedUltimateMomentAtXOfSpan(xOfSupport, spanId, MIN_MOMENT_TAG);
    }

    public double getSupportMomentWhenSpanMomentMax(
            int supportId, int spanId
    ) {
        double xInRefSpan;
        int refSpanId;
        if (supportId == mGeometry.getNumSupport()) {
            refSpanId = supportId - 1;
            xInRefSpan = mSpanMomentFunction.getCalculateSpanLengthMap().get(refSpanId);
        } else {
            refSpanId = supportId;
            xInRefSpan = 0;
        }

        return getCombinedSpecialLoadCaseMomentAtXOfSpan(
                xInRefSpan, refSpanId,
                SPAN_MAX, spanId
        );
    }

    public double getSupportMomentWhenSupportMomentMin(
            int objectSupportId, int refSupportId
    ) {
        double xInRefSpan;
        int refSpanId;
        if (objectSupportId == mGeometry.getNumSupport()) {
            refSpanId = objectSupportId - 1;
            xInRefSpan = mSpanMomentFunction.getCalculateSpanLengthMap().get(refSpanId);
        } else {
            refSpanId = objectSupportId;
            xInRefSpan = 0;
        }
        return getCombinedSpecialLoadCaseMomentAtXOfSpan(
                xInRefSpan, refSpanId,
                SUPPORT_MIN, refSupportId
        );
    }

    public double getSpanMaxMomentWhenSupportMomentMin(
            int spanId, int supportId
    ) {
        double ultimateMoment = 0;

        double spanLength = mSpanMomentFunction.getCalculateSpanLengthMap().get(spanId);
        double spanLocalX = 0;

        for (int i = 0; i < 101; i++) {             // Number of data (moment value) is numSection+1
            double moment = this.getCombinedSpecialLoadCaseMomentAtXOfSpan(
                    spanLocalX, spanId,
                    SUPPORT_MIN, supportId
            );

            if (moment > ultimateMoment) {
                ultimateMoment = moment;
            }
            spanLocalX += spanLength / 100;
        }

        return ultimateMoment;
    }

    public double getSpanMaxMomentWhenSpanMomentMax(
            int objectSpanId, int refSpanId
    ) {
        double ultimateMoment = 0;

        double spanLength = mSpanMomentFunction.getCalculateSpanLengthMap().get(objectSpanId);
        double spanLocalX = 0;

        for (int i = 0; i < 101; i++) {             // Number of data (moment value) is numSection+1
            double moment = this.getCombinedSpecialLoadCaseMomentAtXOfSpan(
                    spanLocalX, objectSpanId,
                    SPAN_MAX, refSpanId
            );

            if (moment > ultimateMoment) {
                ultimateMoment = moment;
            }
            spanLocalX += spanLength / 100;
        }

        return ultimateMoment;

    }

    public AbstractSpanMoment getSpanMomentFunction() {
        return mSpanMomentFunction;
    }

    public Map<Integer, Map<Integer, Double>> getSpecialLoadCaseSupportMomentMap(){

        Map<Integer, Map<Integer, Double>> specialLoadCaseSupportMomentMap = new HashMap<>();

        for(int supportId = 1; supportId < mGeometry.getNumSupport() + 1; supportId++){
            Map<Integer, Double> loadCaseMoment = new HashMap();

            loadCaseMoment.put(01, this.getSupportMomentWhenSpanMomentMax(supportId, 1));

            loadCaseMoment.put(02, this.getSupportMomentWhenSpanMomentMax(supportId, 2));

            for (int loadCase = 2; loadCase < mGeometry.getNumSupport(); loadCase++){

                loadCaseMoment.put(loadCase + 10, this.getSupportMomentWhenSupportMomentMin(supportId, loadCase));
            }

            specialLoadCaseSupportMomentMap.put(supportId, loadCaseMoment);
        }

        return specialLoadCaseSupportMomentMap;

    }
}
