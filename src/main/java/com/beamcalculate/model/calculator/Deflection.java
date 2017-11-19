package com.beamcalculate.model.calculator;

import com.beamcalculate.model.entites.Inputs;

import java.util.List;

/**
 * Created by Ruolin on 19/11/2017 for BeamCalculator.
 */
public class Deflection {
    private double mSectionWidth;
    private double mEffectiveHeight;
    private Rebar mRebar;
    private Inputs mInputs;

    private double mFck;
    private double mNumOfSpans;
    private double mRefPercentage;

    public Deflection(Rebar rebar) {
        mRebar = rebar;
        mInputs = rebar.getInputs();
        mFck = mInputs.getMaterial().getFck();
        mRefPercentage = 0.001 * Math.sqrt(mFck);
        mNumOfSpans = mInputs.getGeometry().getNumSpan();
        mSectionWidth = mInputs.getGeometry().getSectionWidth();
        mEffectiveHeight = mInputs.getGeometry().getEffectiveHeight();


        for (int spanNo = 1; spanNo <= mNumOfSpans; spanNo++){
            double typeCoef = getStructuralTypeCoef(spanNo);
            List<Double> totalRebarAreaListOfSpan_cm2 = mRebar.getTotalRebarAreaListOfSpan_cm2(spanNo);
            double spanLength = mInputs.getGeometry().getEffectiveSpansLengthMap().get(spanNo);
            System.out.printf("Span %d :%n", spanNo);
            for (int caseNo = 0; caseNo < totalRebarAreaListOfSpan_cm2.size(); caseNo++){
                double lowerPercentage = totalRebarAreaListOfSpan_cm2.get(caseNo) / (mSectionWidth * mEffectiveHeight * 10000);
                double spanHeightRatio = spanHeightRatioFormula(typeCoef, 0, lowerPercentage);
                System.out.printf("case %d, rebar percentage = %.3f%%, L/d = %.3f, Min section height = %.2f%n", caseNo,
                        lowerPercentage*100,
                        spanHeightRatio,
                        spanLength/spanHeightRatio/0.9
                );
            }
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
}
