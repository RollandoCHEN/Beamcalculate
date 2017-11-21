package com.beamcalculate.model;

import java.util.HashMap;
import java.util.Map;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

/**
 * Created by Ruolin on 20/11/2017 for BeamCalculator.
 */
public final class RebarCase {
    private Map<Integer, RebarType_Number> layerMap = new HashMap<>();

    public void put(int layerNum, RebarType_Number rebarTypeNumber){
        layerMap.put(layerNum, rebarTypeNumber);
    }

    public RebarType_Number getRebarTypeNum(int layerNum){
        return layerMap.get(layerNum);
    }

    public int numOfLayers(){
        return layerMap.size();
    }

    public String toString() {
        StringBuilder buttonString = new StringBuilder();
        int lastLayer = numOfLayers();
        for (int layerNum = lastLayer; layerNum > 0; layerNum--){
            RebarType_Number rebarType_number = getRebarTypeNum(layerNum);

            if (layerNum != lastLayer){
                buttonString.append("\n");
            }
            String rebarTypeName = rebarType_number.getRebarType().name();
            int numberOfRebar = rebarType_number.getNumberOfRebar();
            buttonString.append(MyMethods.getOrdinalNumber(layerNum))
                    .append(getBundleText("label.steelRebarLayer"))
                    .append(" : ").append(numberOfRebar).append(rebarTypeName);
        }
        return buttonString.toString();
    }
}
