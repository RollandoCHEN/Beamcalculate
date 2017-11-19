package com.beamcalculate.model.calculator.support_moment;

import com.beamcalculate.model.entites.Inputs;

import java.util.HashMap;
import java.util.Map;

import static com.beamcalculate.enums.CalculateMethod.CAQUOT;
import static com.beamcalculate.enums.CalculateMethod.CAQUOT_MINOREE;


public class SupportMomentCaquot extends SupportMoment {
    private Map<Integer, Double> mReducedSpansLengthMap = new HashMap();
    private double mReduceCoef;
    private CaquotReductionConditionVerifier conditionVerifier;

    public SupportMomentCaquot(Inputs inputs){
        mInputs = inputs;
        mGeometry = inputs.getGeometry();
        mLoad = inputs.getLoad();

        conditionVerifier = new CaquotReductionConditionVerifier(mLoad);

        if (conditionVerifier.isVerified()){
            mReduceCoef = 2.0/3.0;
        }else {
            mReduceCoef = 1.0;
        }

        // add supportId and Map to mSupportMomentMap

        for(int i = 0; i<mGeometry.getNumSupport(); i++){
            Map<Integer, Double> mLoadCaseMomentMap = new HashMap();
            for (int j = 0; j<mGeometry.getNumSpan()+1; j++){
                mLoadCaseMomentMap.put(j,0.0);
            }
            mSupportMomentMap.put(i+1, mLoadCaseMomentMap);
        }

        // calculator reduced length for each span_function

        mGeometry.spansLengthMap().forEach((k, v)->{
            if (k ==1 || k ==mGeometry.getNumSpan()) {
                mReducedSpansLengthMap.put(k, v);
            }else {
                mReducedSpansLengthMap.put(k, 0.8* v);
            }
        });

        // calculator moment of support_moment

        mSupportMomentMap.forEach((supportId, mLoadCaseMomentMap)->{
            if (supportId==1||supportId== mSupportMomentMap.size()){
                for (int mLoadCase=0;mLoadCase<mLoadCaseMomentMap.size();mLoadCase++){
                    mLoadCaseMomentMap.put(mLoadCase, 0.0);
                }
            }else {
                double caseGMoment = mReduceCoef * caquotFormula(
                        mLoad.getGMNm(), mLoad.getGMNm(),
                        getLeftSpanReducedLength(supportId),
                        getRightSpanReducedLength(supportId)
                );
                mLoadCaseMomentMap.put(0, caseGMoment);

                for (int mLoadCase=1;mLoadCase<mLoadCaseMomentMap.size();mLoadCase++){
                    double mLoadQL, mLoadQR;
                    if(mLoadCase==supportId-1){
                        mLoadQL = mLoad.getQMNm();
                        mLoadQR = 0;
                    }else if(mLoadCase==supportId){
                        mLoadQL = 0;
                        mLoadQR = mLoad.getQMNm();
                    }else{
                        mLoadQL = mLoadQR = 0;
                    }
                    double caseQiMoment = caquotFormula(
                            mLoadQL, mLoadQR,
                            getLeftSpanReducedLength(supportId),
                            getRightSpanReducedLength(supportId));

                    mLoadCaseMomentMap.put(mLoadCase, caseQiMoment);
                }
            }
        });

    }

    private Double caquotFormula(Double mLoadL, Double mLoadR, Double lengthL, Double lengthR){
        return -(mLoadL * Math.pow(lengthL, 3) + mLoadR * Math.pow(lengthR, 3)) / (8.5 * (lengthL + lengthR));
    }

    private Double getLeftSpanReducedLength(Integer supportId){
        return mReducedSpansLengthMap.getOrDefault(supportId-1, 0.0);
    }

    private Double getRightSpanReducedLength(Integer supportId){
        return mReducedSpansLengthMap.getOrDefault(supportId, 0.0);
    }

    @Override
    public Map<Integer, Map<Integer, Double>> getSupportMomentMap() {
        return mSupportMomentMap;
    }

    @Override
    public double getMomentValueOfSupport(Integer supportId, Integer mLoadCase){
        Map<Integer, Double> mLoadCaseMomentMap;
        mLoadCaseMomentMap = this.getSupportMomentMap().get(supportId);
        return mLoadCaseMomentMap.get(mLoadCase);
    }

    @Override
    public String getMethod() {
        if (conditionVerifier.isVerified()){
            return CAQUOT_MINOREE.getMethodName();
        } else {
            return CAQUOT.getMethodName();
        }
    }

    @Override
    public Map<Integer, Double> getCalculateSpanLengthMap() {
        return mGeometry.spansLengthMap();
    }
}
