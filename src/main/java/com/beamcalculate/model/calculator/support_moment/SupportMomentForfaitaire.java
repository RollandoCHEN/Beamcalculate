package com.beamcalculate.model.calculator.support_moment;

import com.beamcalculate.enums.CombinCoef;
import com.beamcalculate.model.entites.*;

import java.util.*;

import static com.beamcalculate.enums.CombinCoef.G_UNFAVORABLE_COEF;
import static com.beamcalculate.enums.CombinCoef.Q_UNFAVORABLE_COEF;
import static com.beamcalculate.enums.CalculateMethod.FORFAITAIRE;

public class SupportMomentForfaitaire extends SupportMoment{
    private Map<Integer, Map<CombinCoef, Double>> mSpanRefMomentMap = new HashMap<>();
    private Map<Integer, Double> mSpanELURefMomentMap = new HashMap<>();
    private Map<Integer, Double> mSupportELUMomentMap = new HashMap<>();
    private Map<Integer, Double> mCalculateSpanLengthMap = new HashMap<>();

    public SupportMomentForfaitaire(Inputs inputs) {
        mInputs = inputs;
        mGeometry = inputs.getGeometry();
        mLoad = inputs.getLoad();
        mCalculateSpanLengthMap = mGeometry.spansLengthMap();

        // add spanId and Map to mSpanRefMomentMap
        for(int i = 0; i<mGeometry.getNumSpan(); i++){
            Map<CombinCoef, Double> loadCaseMomentMap = new HashMap();
            loadCaseMomentMap.put(G_UNFAVORABLE_COEF, 0.0);
            loadCaseMomentMap.put(Q_UNFAVORABLE_COEF, 0.0);
            mSpanRefMomentMap.put(i+1, loadCaseMomentMap);
        }

        // add supportId and Map to mSupportMomentMap
        for(int i = 0; i<mGeometry.getNumSupport(); i++){
            Map<Integer, Double> loadCaseMomentMap = new HashMap();
            for (int j = 0; j<mGeometry.getNumSpan()+1; j++){
                loadCaseMomentMap.put(j,0.0);
            }
            mSupportMomentMap.put(i+1, loadCaseMomentMap);
        }

        // add spanId and value to mSpanELURefMomentMap
        for(int i = 0; i<mGeometry.getNumSpan(); i++){
            mSpanELURefMomentMap.put(i+1, 0.0);
        }

        // add supportId and value to mSupportELUMomentMap
        for(int i = 0; i<mGeometry.getNumSupport(); i++){
            mSupportELUMomentMap.put(i+1, 0.0);
        }


        double p = G_UNFAVORABLE_COEF.getValue() * mLoad.getGMNm() + Q_UNFAVORABLE_COEF.getValue() * mLoad.getQMNm();
        mSpanELURefMomentMap.forEach((spanId, refMoment)->{
            refMoment = p * Math.pow(mCalculateSpanLengthMap.get(spanId),2) / 8.0;
            mSpanELURefMomentMap.put(spanId, refMoment);
        });

        if (mGeometry.getNumSpan()==2){
            mSupportELUMomentMap.put(2, - 0.6 * mSpanELURefMomentMap.get(1));
        } else {
            mSupportELUMomentMap.forEach((supportId, moment)->{
                if (supportId == 1||supportId == mGeometry.getNumSupport()){
                    moment = 0.0;
                    mSupportELUMomentMap.put(supportId, moment);
                } else if (supportId == 2||supportId == mGeometry.getNumSupport()-1){
                    moment = - 0.5 * Math.max(mSpanELURefMomentMap.get(supportId-1), mSpanELURefMomentMap.get(supportId));
                    mSupportELUMomentMap.put(supportId, moment);
                } else {
                    moment = - 0.4 * Math.max(mSpanELURefMomentMap.get(supportId-1), mSpanELURefMomentMap.get(supportId));
                    mSupportELUMomentMap.put(supportId, moment);
                }
            });
        }

        mSpanRefMomentMap.forEach((spanId, loadCaseMomentMap)->
                loadCaseMomentMap.forEach((loadCase, moment)->{
                    moment = loadCase.getValue() * mLoad.getGMNm() * Math.pow(mCalculateSpanLengthMap.get(spanId),2) / 8;
                    loadCaseMomentMap.put(loadCase, moment);
                })
        );

        mSupportMomentMap.forEach((supportId, loadCaseMomentMap)->{
            if (supportId!=1 && supportId!=mGeometry.getNumSupport()){
                double g1 = mSpanRefMomentMap.get(supportId-1).get(G_UNFAVORABLE_COEF);
                double q1 = mSpanRefMomentMap.get(supportId-1).get(Q_UNFAVORABLE_COEF);
                double g2 = mSpanRefMomentMap.get(supportId).get(G_UNFAVORABLE_COEF);
                double q2 = mSpanRefMomentMap.get(supportId).get(Q_UNFAVORABLE_COEF);
                double coef = mSupportELUMomentMap.get(supportId) / ( g1 + q1 + g2 + q2 );
                loadCaseMomentMap.forEach((loaCase, moment)-> {
                    if (loaCase == 0){
                        moment = (g1 + g2) * coef;
                        loadCaseMomentMap.put(loaCase, moment);
                    } else if (loaCase == supportId-1){
                        moment = q1 * coef;
                        loadCaseMomentMap.put(loaCase, moment);
                    } else if (loaCase == supportId){
                        moment = q2 * coef;
                        loadCaseMomentMap.put(loaCase, moment);
                    } else {
                        loadCaseMomentMap.put(loaCase, 0.0);
                    }
                });
            }
        });
    }

    @Override
    public Map<Integer, Map<Integer, Double>> getSupportMomentMap() {
        return mSupportMomentMap;
    }

    @Override
    public double getMomentValueOfSupport(Integer supportId, Integer loadCase) {
        Map<Integer, Double> loadCaseMomentMap;
        loadCaseMomentMap = this.getSupportMomentMap().get(supportId);
        return loadCaseMomentMap.get(loadCase);
    }

    @Override
    public String getMethod() {
        return FORFAITAIRE.getMethodName();
    }

    @Override
    public Map<Integer, Double> getCalculateSpanLengthMap() {
        return mCalculateSpanLengthMap;
    }
}
