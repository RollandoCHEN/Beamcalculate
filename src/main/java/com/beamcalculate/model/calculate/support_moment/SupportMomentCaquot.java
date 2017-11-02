package com.beamcalculate.model.calculate.support_moment;

import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Load;

import java.util.HashMap;
import java.util.Map;

import static com.beamcalculate.enums.CalculateMethod.CAQUOT;
import static com.beamcalculate.enums.CalculateMethod.CAQUOT_MINOREE;


public class SupportMomentCaquot extends SupportMoment {
    private Map<Integer, Double> mReducedSpansLengthMap = new HashMap();
    private double mReduceCoef;
    private CaquotReductionConditionVerifier conditionVerifier;

    public SupportMomentCaquot(Geometry geometry, Load load){
        mGeometry = geometry;
        conditionVerifier = new CaquotReductionConditionVerifier(load);

        if (conditionVerifier.isVerified()){
            mReduceCoef = 2.0/3.0;
        }else {
            mReduceCoef = 1.0;
        }

        // add supportId and Map to mSupportMomentMap

        for(int i = 0; i<geometry.getNumSupport(); i++){
            Map<Integer, Double> loadCaseMomentMap = new HashMap();
            for (int j = 0; j<geometry.getNumSpan()+1; j++){
                loadCaseMomentMap.put(j,0.0);
            }
            mSupportMomentMap.put(i+1, loadCaseMomentMap);
        }

        // calculate reduced length for each span_function

        geometry.spansLengthMap().forEach((k, v)->{
            if (k ==1 || k ==geometry.getNumSpan()) {
                mReducedSpansLengthMap.put(k, v);
            }else {
                mReducedSpansLengthMap.put(k, 0.8* v);
            }
        });

        // calculate moment of support_moment

        mSupportMomentMap.forEach((supportId, loadCaseMomentMap)->{
            if (supportId==1||supportId== mSupportMomentMap.size()){
                for (int loadCase=0;loadCase<loadCaseMomentMap.size();loadCase++){
                    loadCaseMomentMap.put(loadCase, 0.0);
                }
            }else {
                double caseGMoment = mReduceCoef * caquotFormula(
                        load.getGMNm(), load.getGMNm(),
                        getLeftSpanReducedLength(supportId),
                        getRightSpanReducedLength(supportId)
                );
                loadCaseMomentMap.put(0, caseGMoment);

                for (int loadCase=1;loadCase<loadCaseMomentMap.size();loadCase++){
                    double loadQL, loadQR;
                    if(loadCase==supportId-1){
                        loadQL = load.getQMNm();
                        loadQR = 0;
                    }else if(loadCase==supportId){
                        loadQL = 0;
                        loadQR = load.getQMNm();
                    }else{
                        loadQL = loadQR = 0;
                    }
                    double caseQiMoment = caquotFormula(
                            loadQL, loadQR,
                            getLeftSpanReducedLength(supportId),
                            getRightSpanReducedLength(supportId));

                    loadCaseMomentMap.put(loadCase, caseQiMoment);
                }
            }
        });

    }

    private Double caquotFormula(Double loadL, Double loadR, Double lengthL, Double lengthR){
        return -(loadL * Math.pow(lengthL, 3) + loadR * Math.pow(lengthR, 3)) / (8.5 * (lengthL + lengthR));
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
    public double getMomentValueOfSupport(Integer supportId, Integer loadCase){
        Map<Integer, Double> loadCaseMomentMap;
        loadCaseMomentMap = this.getSupportMomentMap().get(supportId);
        return loadCaseMomentMap.get(loadCase);
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
