package com.beamcalculate.model.calculator;

import  com.beamcalculate.enums.DeflectionParam;
import com.beamcalculate.model.RebarCase;
import com.beamcalculate.model.RebarType_Amount;
import com.beamcalculate.model.entites.Inputs;
import javafx.beans.property.ObjectProperty;

import java.util.*;

import static com.beamcalculate.enums.DeflectionParam.*;

/**
 * Created by Ruolin on 19/11/2017 for BeamCalculator.
 */
public class Deflection {
    private List<RebarCase> mRebarSelectionList = new ArrayList<>();
    private double mSectionWidth;
    private double mEffectiveHeight;
    private Rebar mRebar;
    private Inputs mInputs;

    private double mFck;
    private double mNumOfSpans;
    private double mRefPercentage;

    Map<Integer, Map<DeflectionParam, Double>> mSpanDeflectionParam = new HashMap<>();

    public Deflection(Rebar rebar, List<ObjectProperty<RebarCase>> rebarSelectionList) {
        mRebar = rebar;
        mInputs = rebar.getInputs();
        mFck = mInputs.getMaterial().getFck();
        mRefPercentage = 0.001 * Math.sqrt(mFck);
        mNumOfSpans = mInputs.getGeometry().getNumSpan();
        mSectionWidth = mInputs.getGeometry().getSectionWidth();
        mEffectiveHeight = mInputs.getGeometry().getEffectiveHeight();
        rebarSelectionList.forEach(rebarCaseObjectProperty -> mRebarSelectionList.add(rebarCaseObjectProperty.get()));

        for (int spanNo = 1; spanNo <= mNumOfSpans; spanNo++){
            Map<DeflectionParam, Double> paramValueMap = new TreeMap<>();
            double typeCoef = getStructuralTypeCoef(spanNo);
            RebarCase selectedRebarCase = rebarSelectionList.get(spanNo-1).get();
            double totalRebarArea_cm2 = selectedRebarCase.totalRebarArea_cm2();
            paramValueMap.put(a_A_S, totalRebarArea_cm2);
            double bottomPercentage = totalRebarArea_cm2 / (mSectionWidth * mEffectiveHeight * 10000);
            paramValueMap.put(b_RHO, bottomPercentage * 100);
            double limitSpanHeightRatio = spanHeightRatioFormula(typeCoef, 0, bottomPercentage);
            paramValueMap.put(c_LIMIT_L_D, limitSpanHeightRatio);

            int layerAmount = selectedRebarCase.layerAmount();
            double rebarDiameterSum_mm = 0;
            for (Map.Entry<Integer, RebarType_Amount> entry : selectedRebarCase.entrySet()){
                rebarDiameterSum_mm += entry.getValue().getRebarType().getDiameter_mm();
            }
            double spanLength = mInputs.getGeometry().getEffectiveSpansLengthMap().get(spanNo);
            double sectionEffectiveHeight = mInputs.getGeometry().getSectionHeight() - mInputs.getGeometry().getCoverThickness_cm() / 100 -
                    (rebarDiameterSum_mm  + selectedRebarCase.getMinSpacingBetweenRebar_mm() * (layerAmount -1)) / 2 / 1000;

            paramValueMap.put(d_L_D, spanLength/sectionEffectiveHeight);

            mSpanDeflectionParam.put(spanNo, paramValueMap);

            System.out.printf("Span %d : rebar percentage = %.2f%%, limit L/d = %.2f, d = %.2fm, actual L/d = %.2f%n",
                    spanNo,
                    bottomPercentage*100,
                    limitSpanHeightRatio,
                    sectionEffectiveHeight,
                    spanLength/sectionEffectiveHeight
            );
        }
    }

    private double spanHeightRatioFormula(double typeCoef, double topPercentage, double bottomPercentage){
        double spanHeightRatio;
        if (bottomPercentage <= mRefPercentage){
            spanHeightRatio = typeCoef *
                    (11 + 1.5 * Math.sqrt(mFck) * mRefPercentage / bottomPercentage +
                            3.2 * Math.sqrt(mFck) * Math.pow((mRefPercentage / bottomPercentage - 1), 1.5));
        } else {
            spanHeightRatio = typeCoef *
                    (11 + 1.5 * Math.sqrt(mFck) * mRefPercentage / (bottomPercentage - topPercentage) +
                            1/12 * Math.sqrt(mFck) * Math.sqrt(topPercentage / mRefPercentage));
        }
        return spanHeightRatio;
    }

    private double getStructuralTypeCoef(int currentSpanNo){
        double typeCoef;

        if (mNumOfSpans == 1){
            typeCoef = 1.0;
        } else if (currentSpanNo == 1 || currentSpanNo == mNumOfSpans){
            typeCoef = 1.3;
        } else {
            typeCoef = 1.5;
        }

        return typeCoef;
    }

    public Map<Integer, Map<DeflectionParam, Double>> getSpanDeflectionParam() {
        return mSpanDeflectionParam;
    }

    public List<RebarCase> getRebarSelectionList() {
        return mRebarSelectionList;
    }
}
