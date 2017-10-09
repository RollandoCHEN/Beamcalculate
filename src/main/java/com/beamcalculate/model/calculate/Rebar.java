package com.beamcalculate.model.calculate;

import com.beamcalculate.enums.RebarSize;
import com.beamcalculate.model.entites.Geometry;

public class Rebar {
    private int mMaxDiameter = 20;
    private double mSectionWidth_cm = Geometry.getSectionWidth() * 100;

    public Rebar(double neededRebarArea) {
        int maxRebarsPerLayer = (int)Math.round(mSectionWidth_cm / 10);
        int numLayers;
        for (RebarSize rebarSize : RebarSize.values()) {
            if(rebarSize.getSectionalArea_cm2(maxRebarsPerLayer) > neededRebarArea){
                System.out.printf("%d%s%n", maxRebarsPerLayer, rebarSize.name());
                break;
            }
        }
    }
}
