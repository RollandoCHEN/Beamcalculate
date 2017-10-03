package com.beamcalculate.model.calculate;

import com.beamcalculate.controllers.MainController;
import com.beamcalculate.enums.Pivots;
import com.beamcalculate.enums.ReinforcementParam;
import com.beamcalculate.model.calculate.span.AbstractSpanMoment;
import com.beamcalculate.model.calculate.span.SpanMomentFunction_SpecialLoadCase;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Material;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

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
    private static Map<Integer, DoubleProperty> effectiveWidthPropertyMap = new HashMap();
    private static DoubleProperty mFlangeCompressionHeight = new SimpleDoubleProperty();
    private static DoubleProperty mWebCompressionHeight = new SimpleDoubleProperty();

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
        double maxMoment = getMaxMomentOfSupport(supportId);

        Map<ReinforcementParam, Double> paramValueMap = calculateReinforcementParam(maxMoment, mWidth);

        mPivotMap.put(supportId, mPivot);
        mSupportReinforceParam.put(supportId, paramValueMap);
    }

    private void calculateReinforcementOfSpan(int spanId){
        double maxMoment = getMaxMomentOfSpan(spanId);

        Map<ReinforcementParam, Double> paramValueMap = calculateReinforcementParam(maxMoment, mWidth);

        flangeCompressionHightProperty().setValue(0);
        webCompressionHeightProperty().setValue(paramValueMap.get(d_X));

        mPivotMap.put(spanId, mPivot);
        mSpanReinforceParam.put(spanId, paramValueMap);
    }

    private void calculateReinforcementParamWithTSection(int spanId){
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
            // distances between beams are the same
            double b1 = 0.5 * (mPerpendicularSpacing - mWidth);
            double b = Math.min(0.2 * b1 + 0.1 * conventionalLength, 0.2 * conventionalLength);
            b = Math.min(b, b1);
            double effectiveWidth = mWidth + 2 * b;
            effectiveWidthMap.put(span, effectiveWidth);
            DoubleProperty effectiveWidthProperty = new SimpleDoubleProperty();
            effectiveWidthProperty.setValue(effectiveWidth);
            effectiveWidthPropertyMap.put(span, effectiveWidthProperty);
        });

        double ultimateMomentByFlange =
                effectiveWidthMap.get(spanId) * mSlabThickness *  mFcd * (mEffectiveHeight - mSlabThickness/2);

        double maxMoment = getMaxMomentOfSpan(spanId);
        Map<ReinforcementParam, Double> paramValueMap;

        if (maxMoment < ultimateMomentByFlange){
            paramValueMap = calculateReinforcementParam(maxMoment, effectiveWidthMap.get(spanId));
            flangeCompressionHightProperty().setValue(paramValueMap.get(d_X));
            webCompressionHeightProperty().setValue(0);
        } else {
            double forceByFlange = (effectiveWidthMap.get(spanId) - mWidth) * mSlabThickness *  mFcd;
            double momentByFlange =
                    forceByFlange * (mEffectiveHeight - mSlabThickness/2);
            double momentByWeb = maxMoment - momentByFlange;
            paramValueMap = calculateReinforcementParam(momentByWeb, mWidth);
            double rebarAreaByFlangeForce = forceByFlange / paramValueMap.get(g_EPSILON_S) * 10000;
            paramValueMap.put(j_A_S, paramValueMap.get(j_A_S) + rebarAreaByFlangeForce);
            flangeCompressionHightProperty().setValue(mSlabThickness);
            webCompressionHeightProperty().setValue(paramValueMap.get(d_X));
        }

        mPivotMap.put(spanId, mPivot);
        mSpanReinforceParam.put(spanId, paramValueMap);
    }

    private Map<ReinforcementParam, Double> calculateReinforcementParam(double maxMoment, double bw){
        Map<ReinforcementParam, Double> paramValueMap = new TreeMap<>();

        paramValueMap.put(a_M, maxMoment);

        mReducedMomentMu = maxMoment / (bw * Math.pow(mEffectiveHeight, 2.0) * mFcd);
        paramValueMap.put(b_MU, mReducedMomentMu);

        if (mReducedMomentMu < 0.056){
            mPivot = PIVOTA;
        }else if (mReducedMomentMu < 0.48){
            mPivot = PIVOTB;
        }else {
            mPivot = PIVOTC;
        }

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

        return paramValueMap;
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

    public Reinforcement(AbstractSpanMoment spanMomentFunction) {
        prepare();

        mSpanMomentFunction = spanMomentFunction;

        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++){
            if (MainController.isOnTSection()) {
                calculateReinforcementParamWithTSection(spanId);
            } else {
                calculateReinforcementOfSpan(spanId);
            }
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

    public static Map<Integer, DoubleProperty> getEffectiveWidthPropertyMap() {
        return effectiveWidthPropertyMap;
    }

    public static double getFlangeCompressionHight() {
        return mFlangeCompressionHeight.get();
    }

    public static DoubleProperty flangeCompressionHightProperty() {
        return mFlangeCompressionHeight;
    }

    public static double getWebCompressionHeight() {
        return mWebCompressionHeight.get();
    }

    public static DoubleProperty webCompressionHeightProperty() {
        return mWebCompressionHeight;
    }
}
