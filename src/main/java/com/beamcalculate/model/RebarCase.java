package com.beamcalculate.model;

import com.beamcalculate.enums.RebarType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.beamcalculate.model.page_manager.LanguageManager.getBundleText;

/**
 * Created by Ruolin on 20/11/2017 for BeamCalculator.
 */
public final class RebarCase{
    private Map<Integer, RebarType_Amount> layerMap = new HashMap<>();

    public void put(int layerNum, RebarType_Amount rebarTypeNumber){
        layerMap.put(layerNum, rebarTypeNumber);
    }

    public RebarType_Amount getRebarOfLayer(int layerNum){
        return layerMap.get(layerNum);
    }

    public double getRebarDiamOfLayer_mm(int layerNum) {
        return layerMap.get(layerNum).getRebarType().getDiameter_mm();
    }

    public int layerAmount(){
        return layerMap.size();
    }

    public String toString() {
        StringBuilder buttonString = new StringBuilder();
        int lastLayer = layerAmount();
        for (int layerNum = lastLayer; layerNum > 0; layerNum--){
            RebarType_Amount rebarType_amount = getRebarOfLayer(layerNum);

            if (layerNum != lastLayer){
                buttonString.append("\n");
            }
            String rebarTypeName = rebarType_amount.getRebarType().name();
            int numberOfRebar = rebarType_amount.getNumberOfRebar();
            buttonString.append(MyMethods.getOrdinalNumber(layerNum))
                    .append(getBundleText("label.steelRebarLayer"))
                    .append(" : ").append(numberOfRebar).append(rebarTypeName);
        }
        return buttonString.toString();
    }


    public void forEach(BiConsumer<Integer, RebarType_Amount> action) {
        layerMap.forEach(action);
    }

    public Map<Integer, Double> getRebarAreaForEachLayerOfSpan_cm2(){
        Map<Integer, Double> rebarAreaMap = new HashMap<>();
        layerMap.forEach((layerNum, rebarType_amount) -> {
            RebarType rebarType = rebarType_amount.getRebarType();
            int numOfRebar = rebarType_amount.getNumberOfRebar();
            double rebarArea = rebarType.getSectionalArea_cm2(numOfRebar);
            rebarAreaMap.put(layerNum, rebarArea);
        });
        return rebarAreaMap;
    }

    public double getTotalRebarArea_cm2(){
        double totalRebarArea = 0;
        for (int layerNum = 1; layerNum < layerMap.size()+1; layerNum++) {
            totalRebarArea += getRebarAreaForEachLayerOfSpan_cm2().get(layerNum);
        }
        return totalRebarArea;
    }

}
