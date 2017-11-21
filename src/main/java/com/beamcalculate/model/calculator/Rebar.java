package com.beamcalculate.model.calculator;

import static com.beamcalculate.enums.RebarType.*;

import com.beamcalculate.enums.RebarType;
import com.beamcalculate.model.RebarCase;
import com.beamcalculate.model.RebarType_Amount;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Inputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.beamcalculate.enums.ReinforcementParam.j_A_S;

public class Rebar {
    // TODO mMaxDiameter should not be fixed
    private double mMaxDiameter = HA25.getDiameter_mm();
    private Map<Integer, List<RebarCase>> mRebarCasesMap = new HashMap<>();
    private Reinforcement mReinforcement;
    private int mMaxNumOfRebarPerLayer;
    private int mMaxNumLayers;
    private Inputs mInputs;
    private Geometry mGeometry;

    public Rebar(Reinforcement reinforcement) {
        mReinforcement = reinforcement;
        mInputs = mReinforcement.getInputs();
        mGeometry = mInputs.getGeometry();

        double sectionWidth_cm = mGeometry.getSectionWidth() * 100;
        // TODO the estimation method of maxRebarPerLayer is not confirmed
        mMaxNumOfRebarPerLayer = getMaxNumOfRebarPerLayer(sectionWidth_cm);

        // TODO maxNumLayers should not be fixed
        mMaxNumLayers = 2;

        for (int spanId = 1; spanId < mGeometry.getNumSpan()+1; spanId++){
            calculateRebarCasesOfSpan(spanId);
//            printRebarOfSpan(spanId);
        }
    }

    private void calculateRebarCasesOfSpan(int spanId) {
        double rebarAreaAs = getRebarAreaOfSpan(spanId);
        List<RebarCase> rebarCasesList = new ArrayList<>();

        // put steel bars with different diameters for different layers, but on one layer, put same bars
        for (int numOfLayers = 1; numOfLayers < mMaxNumLayers + 1; numOfLayers++) {
            switch (numOfLayers){
                case 1: {
                    for (RebarType rebarType : RebarType.values()) {
                        RebarCase layer_rebarMap = new RebarCase();

                        if (rebarType.getSectionalArea_cm2(mMaxNumOfRebarPerLayer) > rebarAreaAs
                                && rebarType.getDiameter_mm() <= mMaxDiameter) {

                            RebarType_Amount type_numberMap = new RebarType_Amount(rebarType, mMaxNumOfRebarPerLayer);

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
                        if (firstLayerRebarType.getDiameter_mm() <= mMaxDiameter &&
                                firstLayerRebarType.getSectionalArea_cm2(mMaxNumOfRebarPerLayer) < rebarAreaAs)
                        {
                            // when we have the same first layer rebar, we need only the min rebar diameter case for the second layer
                            // for example, if we have 5HA16 + 5HA8, we don't need to loop till 5HA16 + 5HA10,  5HA16 + 5HA12 etc.
                            double secondLayerMinRebarDiameter = firstLayerRebarType.getDiameter_mm();
                            for (RebarType secondLayerRebarType : RebarType.values()) {
                                if (secondLayerRebarType.getDiameter_mm() <= secondLayerMinRebarDiameter &&
                                        firstLayerRebarType.getSectionalArea_cm2(mMaxNumOfRebarPerLayer) + secondLayerRebarType.getSectionalArea_cm2(mMaxNumOfRebarPerLayer) > rebarAreaAs &&
                                        firstLayerRebarType.getInnerNumber() - secondLayerRebarType.getInnerNumber() <= 2)
                                {
                                    secondLayerMinRebarDiameter = secondLayerRebarType.getDiameter_mm();
                                    RebarType_Amount first_type_numberMap = new RebarType_Amount(firstLayerRebarType, mMaxNumOfRebarPerLayer);
                                    RebarType_Amount second_type_numberMap = new RebarType_Amount(secondLayerRebarType, mMaxNumOfRebarPerLayer);

                                    RebarCase layer_rebarMap = new RebarCase();
                                    layer_rebarMap.put(1, first_type_numberMap);
                                    layer_rebarMap.put(2, second_type_numberMap);

                                    rebarCasesList.add(layer_rebarMap);
                                }
                            }
                        }
                    }
                    break;
                }
                default: break;
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

    public List<Map<Integer, Double>> getRebarAreaListForEachLayerOfSpan_cm2(int spanId){
        List<Map<Integer, Double>> rebarAreaList = new ArrayList<>();

        for (int caseNum = 0; caseNum < getRebarCasesListOfSpan(spanId).size(); caseNum++) {
            Map<Integer, Double> rebarAreaMap = new HashMap<>();

            RebarCase layerRebarMap = getRebarCasesListOfSpan(spanId).get(caseNum);
            layerRebarMap.forEach((layerNum, rebarType_amount) -> {
                RebarType rebarType = rebarType_amount.getRebarType();
                int numOfRebar = rebarType_amount.getNumberOfRebar();
                double rebarArea = rebarType.getSectionalArea_cm2(numOfRebar);
                rebarAreaMap.put(layerNum, rebarArea);
            });
            rebarAreaList.add(rebarAreaMap);
        }

        return rebarAreaList;
    }

    public List<Double> getTotalRebarAreaListOfSpan_cm2(int spanId){
        List<Double> totalRebarAreaList = new ArrayList<>();

        for (int caseNum = 0; caseNum < getRebarCasesListOfSpan(spanId).size(); caseNum++) {
            double totalRebarArea = 0;
            for (int layerNum = 1; layerNum < getRebarAreaListForEachLayerOfSpan_cm2(spanId).get(caseNum).size()+1; layerNum++) {
                totalRebarArea += getRebarAreaListForEachLayerOfSpan_cm2(spanId).get(caseNum).get(layerNum);
            }
            totalRebarAreaList.add(totalRebarArea);
        }
        return totalRebarAreaList;
    }

    private void printRebarOfSpan(int spanId) {
        for (int i = 0; i< getRebarCasesListOfSpan(spanId).size(); i++) {
            System.out.printf("%nSpan %d : %n", spanId);
            RebarCase layerRebarMap = getRebarCasesListOfSpan(spanId).get(i);
            System.out.printf("- Case %d : %n", i+1);

            layerRebarMap.forEach((layerNum, rebarType_amount) -> System.out.printf("Layer %d, %d%s%n", layerNum, rebarType_amount.getNumberOfRebar(), rebarType_amount.getRebarType().name())
            );

        }
    }

    public Reinforcement getReinforcement() {
        return mReinforcement;
    }

    public Inputs getInputs() {
        return mInputs;
    }

    // RebarList is a List of LayerNumber_(RebarType_NumberOfRebar_Map) Map
    // The RebarList represent the rebar cases who match the needed steel rebar
    public List<RebarCase> getRebarCasesListOfSpan(int spanId) {
        return mRebarCasesMap.get(spanId);
    }

    private int getMaxNumOfRebarPerLayer(double sectionWidth_cm) {
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
