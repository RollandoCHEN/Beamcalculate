package com.beamcalculate.model.calculate;

import com.beamcalculate.enums.RebarType;
import com.beamcalculate.model.entites.Geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.beamcalculate.enums.ReinforcementParam.j_A_S;

public class Rebar {
    private int mMaxDiameter = 20;
    private double mSectionWidth_cm = Geometry.getSectionWidth() * 100;
    private List<Map<Integer, Map<RebarType, Integer>>> mRebarList = new ArrayList();
    private Reinforcement mReinforcement;

    public Rebar(Reinforcement reinforcement) {
        setReinforcement(reinforcement);

        int spanId = 1;
        double rebarAreaAs = getRebarAreaOfSpan(spanId);

        int maxRebarPerLayer = (int) Math.round(mSectionWidth_cm / 10);
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

                    mRebarList.add(layerRadarsMap);

                    // Because the for each loop will loop for the rebar type enum in order, from 6 to 40
                    // as we want to take the min size of rebar, so when it matches the condition we break the loop
                    break;
                }

            }
        }
        printRebar();
    }

    private double getRebarAreaOfSpan(int spanId) {
        return getReinforcement().getSpanReinforceParam().get(spanId).get(j_A_S);
    }

    private double getRebarAreaOfSupport(int supportId) {
        return getReinforcement().getSupportReinforceParam().get(supportId).get(j_A_S);
    }

    private void printRebar() {
        for (int i=0; i<mRebarList.size();i++) {
            Map<Integer, Map<RebarType, Integer>> layerRebarMap = mRebarList.get(i);
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
}
