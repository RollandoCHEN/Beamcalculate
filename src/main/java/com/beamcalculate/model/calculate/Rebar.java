package com.beamcalculate.model.calculate;

import static com.beamcalculate.enums.RebarType.*;

import com.beamcalculate.enums.RebarType;
import com.beamcalculate.model.RebarType_Number;
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
    private Map<Integer, List<Map<Integer, RebarType_Number>>> mRebarCasesMap = new HashMap<>();
    private Reinforcement mReinforcement;
    private int mMaxNumOfRebarPerLayer;
    private int mMaxNumLayers;

    public Rebar(Reinforcement reinforcement) {
        setReinforcement(reinforcement);

        // TODO the estimation method of maxRebarPerLayer is not confirmed
        mMaxNumOfRebarPerLayer = getMaxNumOfRebarPerLayer(mSectionWidth_cm);

        // TODO maxNumLayers should not be fixed
        mMaxNumLayers = 2;

        for (int spanId = 1; spanId < Geometry.getNumSpan()+1; spanId++){
            calculateRebarCasesOfSpan(spanId);
            printRebarOfSpan(spanId);
        }
    }

    private void calculateRebarCasesOfSpan(int spanId) {

        double rebarAreaAs = getRebarAreaOfSpan(spanId);
        List<Map<Integer, RebarType_Number>> rebarCasesList = new ArrayList<>();

//        // put same steel bars for all layers
//        for (int numOfLayers = 1; numOfLayers < maxNumLayers + 1; numOfLayers++) {
//            for (RebarType rebarType : RebarType.values()) {
//                Map<RebarType, Integer> type_numberMap = new HashMap<>();
//                Map<Integer, Map<RebarType, Integer>> layer_rebarMap = new HashMap<>();
//
//                if (rebarType.getSectionalArea_cm2(maxRebarPerLayer * numOfLayers) > rebarAreaAs
//                        && rebarType.getDiameter_mm() < mMaxDiameter) {
//
//                    type_numberMap.put(rebarType, maxRebarPerLayer);
//
//                    for (int layerNum = 1; layerNum < numOfLayers + 1; layerNum++) {
//                        layer_rebarMap.put(layerNum, type_numberMap);
//                    }
//
//                    rebarCasesList.add(layer_rebarMap);
//
//                    // Because the for each loop will loop for the rebar type enum in order, from 6 to 40
//                    // as we want to take the min size of rebar, so when it matches the condition we break the loop
//                    break;
//                }
//            }
//        }

        // put steel bars with different diameters for different layers, but on one layer, put same bars
        for (int numOfLayers = 1; numOfLayers < mMaxNumLayers + 1; numOfLayers++) {
            switch (numOfLayers){
                case 1: {
                    for (RebarType rebarType : RebarType.values()) {
                        Map<Integer, RebarType_Number> layer_rebarMap = new HashMap<>();

                        if (rebarType.getSectionalArea_cm2(mMaxNumOfRebarPerLayer) > rebarAreaAs
                                && rebarType.getDiameter_mm() < mMaxDiameter) {

                            RebarType_Number type_numberMap = new RebarType_Number(rebarType, mMaxNumOfRebarPerLayer);

                            layer_rebarMap.put(1, type_numberMap);

                            rebarCasesList.add(layer_rebarMap);

                            // Because the for each loop will loop for the rebar type enum in order, from 6 to 40
                            // as we want to take the min size of rebar, so when it matches the condition we break the loop
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    for (RebarType firstLayerRebarType : RebarType.values()) {
                        if (firstLayerRebarType.getDiameter_mm() < mMaxDiameter &&
                                firstLayerRebarType.getSectionalArea_cm2(mMaxNumOfRebarPerLayer) < rebarAreaAs)
                        {
                            // when we have the same first layer rebar, we need only the min rebar diameter case for the second layer
                            // for example, if we have 5HA16 + 5HA8, we don't need to loop till 5HA16 + 5HA10,  5HA16 + 5HA12 etc.
                            double secondLayerMinRebarDiameter = firstLayerRebarType.getDiameter_mm();
                            for (RebarType secondLayerRebarType : RebarType.values()) {
                                if (secondLayerRebarType.getDiameter_mm() <= secondLayerMinRebarDiameter &&
                                        firstLayerRebarType.getSectionalArea_cm2(mMaxNumOfRebarPerLayer) + secondLayerRebarType.getSectionalArea_cm2(mMaxNumOfRebarPerLayer) > rebarAreaAs)
                                {
                                    secondLayerMinRebarDiameter = secondLayerRebarType.getDiameter_mm();
                                    RebarType_Number first_type_numberMap = new RebarType_Number(firstLayerRebarType, mMaxNumOfRebarPerLayer);
                                    RebarType_Number second_type_numberMap = new RebarType_Number(secondLayerRebarType, mMaxNumOfRebarPerLayer);

                                    Map<Integer, RebarType_Number> layer_rebarMap = new HashMap<>();
                                    layer_rebarMap.put(1, first_type_numberMap);
                                    layer_rebarMap.put(2, second_type_numberMap);

                                    rebarCasesList.add(layer_rebarMap);
                                }
                            }
                        }
                    }
//                    keepMinRebarAreaCaseForSameFirstLayer(rebarCasesList);
                    break;
                }
                default: break;
            }
        }
        mRebarCasesMap.put(spanId, rebarCasesList);
    }

    private void keepMinRebarAreaCaseForSameFirstLayer(List<Map<Integer, RebarType_Number>> rebarCasesList) {

        double secondLayerRebarminDiameter;
        for (int i = 0; i < rebarCasesList.size(); i++){
            for (int j = i+1; j < rebarCasesList.size(); j++){
                if(rebarCasesList.get(i).get(1).getRebarType() == rebarCasesList.get(j).get(1).getRebarType()){
                    double secondLayerRebarDiameter_i = rebarCasesList.get(i).get(2).getRebarType().getDiameter_mm();
                    double secondLayerRebarDiameter_j = rebarCasesList.get(j).get(2).getRebarType().getDiameter_mm();
                    secondLayerRebarminDiameter = Math.min(secondLayerRebarDiameter_i, secondLayerRebarDiameter_j);
                }
            }
        }


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

            Map<Integer, RebarType_Number> layerRebarMap = getRebarCasesListOfSpan(spanId).get(caseNum);
            layerRebarMap.forEach((layerNum, rebarType_number) -> {
                RebarType rebarType = rebarType_number.getRebarType();
                int numOfRebar = rebarType_number.getNumberOfRebar();
                double rebarArea = rebarType.getSectionalArea_cm2(numOfRebar);
                rebarAreaMap.put(layerNum, rebarArea);
            });
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
            Map<Integer, RebarType_Number> layerRebarMap = getRebarCasesListOfSpan(spanId).get(i);
            System.out.printf("- Case %d : %n", i+1);

            layerRebarMap.forEach((layerNum, rebarType_number) -> System.out.printf("Layer %d, %d%s%n", layerNum, rebarType_number.getNumberOfRebar(), rebarType_number.getRebarType().name())
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
    public List<Map<Integer, RebarType_Number>> getRebarCasesListOfSpan(int spanId) {
        return mRebarCasesMap.get(spanId);
    }

    public int getMaxNumOfRebarPerLayer(double sectionWidth_cm) {
        if (0 <= sectionWidth_cm && sectionWidth_cm < 17){
            return 2;
        } else if (17 <= sectionWidth_cm && sectionWidth_cm < 27){
            return 3;
        } else if (27 <= sectionWidth_cm && sectionWidth_cm < 37){
            return 4;
        }else if (37 <= sectionWidth_cm && sectionWidth_cm < 47){
            return 5;
        }else if (47 <= sectionWidth_cm && sectionWidth_cm < 57){
            return 6;
        }else if (57 <= sectionWidth_cm && sectionWidth_cm < 67){
            return 7;
        }else if (67 <= sectionWidth_cm && sectionWidth_cm < 77){
            return 8;
        }else if (77 <= sectionWidth_cm && sectionWidth_cm < 87){
            return 9;
        }else if (87 <= sectionWidth_cm && sectionWidth_cm < 100){
            return 10;
        }else  {
            return  11;
        }
//        return (int) Math.round(mSectionWidth_cm / 10);
    }
}
