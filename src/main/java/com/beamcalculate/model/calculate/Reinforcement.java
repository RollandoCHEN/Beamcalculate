package com.beamcalculate.model.calculate;

import com.beamcalculate.enums.Pivots;
import com.beamcalculate.enums.ReinforcementParam;
import com.beamcalculate.model.calculate.span.AbstractSpanMoment;
import com.beamcalculate.model.calculate.span.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.beamcalculate.enums.CalculateMethod.TROIS_MOMENT_R;
import static com.beamcalculate.enums.UltimateCase.MAX;
import static com.beamcalculate.enums.Pivots.PIVOTA;
import static com.beamcalculate.enums.Pivots.PIVOTB;
import static com.beamcalculate.enums.Pivots.PIVOTC;
import static com.beamcalculate.enums.ReinforcementParam.*;

public class Reinforcement {
    private double mReducedMomentMu;
    private double mNeutralAxisAlpha;
    private double mNeutralAxisX;
    private double mLeverArmBeta;
    private double mLeverArmZ;
    private double mStrainEpsilonS;
    private double mStressSigmaS;
    private double mRebarAreaAs;
    private Pivots mPivot;
    private double mWidth;
    private double mEffectiveHeight;
    private double mFcd;
    private double mFyd;
    private double mSteelUltimateStrain;
    private double mPerpendicularSpacing;
    private double mSlabThickness;
    private AbstractSpanMoment mSpanMomentFunction;

    private Map<Integer, Map<ReinforcementParam, Double>> mSpanReinforceParam = new HashMap<>();
    private Map<Integer, Map<ReinforcementParam, Double>> mSupportReinforceParam = new HashMap<>();
    private Map<Integer, Pivots> mPivotMap = new HashMap<>();

    private void prepare(){
        mWidth = Geometry.getSectionWidth();
        mEffectiveHeight = Geometry.getEffectiveHeight();
        mFcd = Material.getFcd();
        mFyd = Material.getFyd();
        mSteelUltimateStrain = Material.getSteelUltimateExtension();
        mPerpendicularSpacing = Geometry.getPerpendicularSpacing();
        mSlabThickness = Geometry.getSlabThickness();
    }

    private void calculateReinforcementOfSupport(int supportId){

        Map<ReinforcementParam, Double> paramValueMap = new TreeMap<>();

        double maxMoment = getMaxMomentOfSupport(supportId);
        paramValueMap.put(a_M, maxMoment);

        calculateReinforcementParam(supportId, paramValueMap, maxMoment);

        mSupportReinforceParam.put(supportId, paramValueMap);

    }

    private double getMaxMomentOfSupport(int supportId) {
        double maxMoment;
        ELUCombination combination = new ELUCombination(mSpanMomentFunction);
        if(mSpanMomentFunction.getMethod().equals(TROIS_MOMENT_R.getBundleText())) {
            SpanMomentFunction_SpecialLoadCase newSpanMomentFunction = (SpanMomentFunction_SpecialLoadCase) mSpanMomentFunction;
            maxMoment = -newSpanMomentFunction.getMinMomentValueOfSupport(supportId);
        }else {
            maxMoment = -combination.getMinMomentValueOfSupport(supportId);
        }
        return maxMoment;
    }

    private void calculateReinforcementOfSpan(int spanId){
        Map<ReinforcementParam, Double> paramValueMap = new TreeMap<>();

        double maxMoment = getMaxMomentOfSpan(spanId);
        paramValueMap.put(a_M, maxMoment);

        calculateReinforcementParam(spanId, paramValueMap, maxMoment);

        mSpanReinforceParam.put(spanId, paramValueMap);
    }

    private double getMaxMomentOfSpan(int spanId) {
        double maxMoment;
        ELUCombination combination = new ELUCombination(mSpanMomentFunction);
        if(mSpanMomentFunction.getMethod().equals(TROIS_MOMENT_R.getBundleText())) {
            SpanMomentFunction_SpecialLoadCase newSpanMomentFunction = (SpanMomentFunction_SpecialLoadCase) mSpanMomentFunction;
            maxMoment = newSpanMomentFunction.getUltimateMomentValueOfSpan(spanId, MAX);
        }else {
            maxMoment = combination.getUltimateMomentValueOfSpan(spanId, MAX);
        }
        return maxMoment;
    }

    private void calculateReinforcementParam(int supportOrSpanId, Map<ReinforcementParam, Double> paramValueMap, double maxMoment){
        mReducedMomentMu = maxMoment / (mWidth * Math.pow(mEffectiveHeight, 2.0) * mFcd);
        paramValueMap.put(b_MU, mReducedMomentMu);

        if (mReducedMomentMu < 0.056){
            mPivot = PIVOTA;
        }else if (mReducedMomentMu < 0.48){
            mPivot = PIVOTB;
        }else {
            mPivot = PIVOTC;
        }
        mPivotMap.put(supportOrSpanId, mPivot);

        mNeutralAxisAlpha = 1.25 * (1 - Math.sqrt(1 - 2 * mReducedMomentMu));
        paramValueMap.put(c_ALPHA, mNeutralAxisAlpha);

        mNeutralAxisX = mNeutralAxisAlpha * mEffectiveHeight;
        paramValueMap.put(d_X, mNeutralAxisX);

        mLeverArmBeta = 1 - 0.4 * mNeutralAxisAlpha;
        paramValueMap.put(e_BETA, mLeverArmBeta);

        mLeverArmZ = mLeverArmBeta * mEffectiveHeight;
        paramValueMap.put(f_Z, mLeverArmZ);

        switch (mPivot){
            case PIVOTA: mStrainEpsilonS = 0.9 * mSteelUltimateStrain * 100;
                break;
            case PIVOTB: mStrainEpsilonS = 0.0035 * (1 - mNeutralAxisAlpha) / mNeutralAxisAlpha * 100;
                break;
            case PIVOTC: break;
        }
        paramValueMap.put(g_EPSILON_S, mStrainEpsilonS);

        mStressSigmaS = mFyd *(1 + 0.08 * (mStrainEpsilonS - 0.217) / (mSteelUltimateStrain * 100 - 0.217));
        paramValueMap.put(i_SIGMA_S, mStressSigmaS);

        mRebarAreaAs = maxMoment/(mLeverArmZ * mStressSigmaS) * 10000;
        paramValueMap.put(j_A_S, mRebarAreaAs);
    }

    private void calculateReinforcementParamWithTSection(int spanId, Map<ReinforcementParam, Double> paramValueMap, double maxMoment){
        Map<Integer, Double> conventionalLengthMap = new HashMap<>();
        Map<Integer, Double> effectiveWidthMap = new HashMap<>();

        mSpanMomentFunction.getCalculateSpanLengthMap().forEach((span, spanLength)->{
            if(span == 1 || span == Geometry.getNumSpan()) {
                conventionalLengthMap.put(span, 0.85 * spanLength);
            } else {
                conventionalLengthMap.put(span, 0.7 * spanLength);
            }
        });

        conventionalLengthMap.forEach((span, conventionalLength)->{
            double b = Math.min(0.2 * 0.5 * (mPerpendicularSpacing - mWidth) + 0.1 * conventionalLength, 0.2 * conventionalLength);
            double effectiveWidth = Math.min(mWidth + 2 * b, mPerpendicularSpacing);
            effectiveWidthMap.put(span, effectiveWidth);
        });

        double ultimateMomentByFlange = mSlabThickness * effectiveWidthMap.get(spanId) * mFcd * (mEffectiveHeight - mSlabThickness/2) ;

        // TODO Calculation of reinforcement with T-shaped cross section

    }

    public Reinforcement(AbstractSpanMoment spanMomentFunction) {
        prepare();

        mSpanMomentFunction = spanMomentFunction;

        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++){
            calculateReinforcementOfSpan(spanId);
        }

        for (int supportId = 1; supportId < Geometry.getNumSupport()+1; supportId++){
            calculateReinforcementOfSupport(supportId);
        }

    }

    public Map<Integer, Map<ReinforcementParam, Double>> getSpanReinforceParam() {
        return mSpanReinforceParam;
    }

    public Map<Integer, Map<ReinforcementParam, Double>> getSupportReinforceParam() {
        return mSupportReinforceParam;
    }

    public Map<Integer, Pivots> getPivotMap() {
        return mPivotMap;
    }

    public AbstractSpanMoment getSpanMomentFunction() {
        return mSpanMomentFunction;
    }
}
