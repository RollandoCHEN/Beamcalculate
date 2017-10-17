package com.beamcalculate.model.calculate;

import static com.beamcalculate.enums.RebarType.*;

import com.beamcalculate.enums.RebarType;
import com.beamcalculate.model.entites.Geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.beamcalculate.enums.ReinforcementParam.j_A_S;

public class Rebar {
    // TODO mMaxDiameter should not be fixed
    private double mMaxDiameter = HA25.getDiameter_mm();
    private double mSectionWidth_cm = Geometry.getSectionWidth() * 100;
    private Map<Integer, List<Map<Integer, Map<RebarType, Integer>>>> mRebarCasesMap = new HashMap<>();
    private Reinforcement mReinforcement;

    public Rebar(Reinforcement reinforcement) {
        setReinforcement(reinforcement);

        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++){
            calculateRebarCasesOfSpan(spanId);
            printRebarOfSpan(spanId);
        }
    }

    private void calculateRebarCasesOfSpan(int spanId) {
        double rebarAreaAs = getRebarAreaOfSpan(spanId);
        List<Map<Integer, Map<RebarType, Integer>>> rebarCasesList = new ArrayList<>();

        // TODO the estimation methode of maxRebarPerLayer is not confirmed
        int maxRebarPerLayer = (int) Math.round(mSectionWidth_cm / 10);

        // TODO maxNumLayers should not be fixed
        int maxNumLayers = 2;
        for (int numOfLayers = 1; numOfLayers < maxNumLayers + 1; numOfLayers++) {
            for (RebarType rebarType : RebarType.values()) {
                Map<RebarType, Integer> typeNumberMap = new HashMap<>();
                Map<Integer, Map<RebarType, Integer>> layerRadarsMap = new HashMap<>();
                if (rebarType.getSectionalArea_cm2(maxRebarPerLayer * numOfLayers) > rebarAreaAs
                        && rebarType.getDiameter_mm() < mMaxDiameter) {

                    typeNumberMap.put(rebarType, maxRebarPerLayer);

                    for (int layerNum = 1; layerNum < numOfLayers + 1; layerNum++) {
                        layerRadarsMap.put(layerNum, typeNumberMap);
                    }

                    rebarCasesList.add(layerRadarsMap);

                    // Because the for each loop will loop for the rebar type enum in order, from 6 to 40
                    // as we want to take the min size of rebar, so when it matches the condition we break the loop
                    break;
                }
            }
        }
        mRebarCasesMap.put(spanId, rebarCasesList);
    }

    private double getRebarAreaOfSpan(int spanId) {
        return getReinforcement().getSpanReinforceParam().get(spanId).get(j_A_S);
    }

    private double getRebarAreaOfSupport(int supportId) {
        return getReinforcement().getSupportReinforceParam().get(supportId).get(j_A_S);
    }

    public List<Map<Integer, Double>> getRebarAreaListForEachLayerOfSpan(int spanId){
        List<Map<Integer, Double>> rebarAreaList = new ArrayList();

        for (int caseNum = 0; caseNum < getRebarCasesListOfSpan(spanId).size(); caseNum++) {
            Map<Integer, Double> rebarAreaMap = new HashMap<>();

            Map<Integer, Map<RebarType, Integer>> layerRebarMap = getRebarCasesListOfSpan(spanId).get(caseNum);
            layerRebarMap.forEach(
                    (layerNum, typeNumMap) -> typeNumMap.forEach(
                            (rebarType, numOfRebar) -> {
                                double rebarArea = rebarType.getSectionalArea_cm2(numOfRebar);
                                rebarAreaMap.put(layerNum, rebarArea);
                            }
                    )
            );
            rebarAreaList.add(rebarAreaMap);
        }

        return rebarAreaList;
    }

    public List<Double> getTotalRebarAreaListOfSpan(int spanId){
        List<Double> totalRebarAreaList = new ArrayList();

        for (int caseNum = 0; caseNum < getRebarCasesListOfSpan(spanId).size(); caseNum++) {
            double totalRebarArea = 0;
            for (int layerNum = 1; layerNum < getRebarAreaListForEachLayerOfSpan(spanId).get(caseNum).size()+1; layerNum++) {
                totalRebarArea += getRebarAreaListForEachLayerOfSpan(spanId).get(caseNum).get(layerNum);
            }
            totalRebarAreaList.add(totalRebarArea);
        }
        return totalRebarAreaList;
    }

    private void printRebarOfSpan(int spanId) {
        for (int i = 0; i< getRebarCasesListOfSpan(spanId).size(); i++) {
            System.out.printf("%nSpan %d : %n", spanId);
            Map<Integer, Map<RebarType, Integer>> layerRebarMap = getRebarCasesListOfSpan(spanId).get(i);
            System.out.printf("- Case %d : %n", i+1);

            layerRebarMap.forEach(
                    (layerNum, typeNumMap) -> typeNumMap.forEach(
                            (rebarType, numOfRebar) -> {
                                System.out.printf("Layer %d, %d%s%n", layerNum, numOfRebar, rebarType.name());
                            }
                    )
            );

        }
    }

    public void setReinforcement(Reinforcement reinforcement) {
        mReinforcement = reinforcement;
    }

    public Reinforcement getReinforcement() {
        return mReinforcement;
    }

    // RebarList is a List of LayerNumber_(RebarType_NumberOfRebar_Map) Map
    // The RebarList represent the rebar cases who match the needed steel rebar
    public List<Map<Integer, Map<RebarType, Integer>>> getRebarCasesListOfSpan(int spanId) {
        return mRebarCasesMap.get(spanId);
    }

}
