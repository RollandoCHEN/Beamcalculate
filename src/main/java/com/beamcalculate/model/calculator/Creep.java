package com.beamcalculate.model.calculator;

import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Inputs;
import com.beamcalculate.model.entites.Material;

/**
 * Created by Ruolin on 24/11/2017 for BeamCalculator.
 */
public class Creep {
    private double mRH_percent = 70;
    private String mCementClass = "N";
    private int mLoadDay = 30;
    private int mCalculateDay = 100000;
    private double mBetaH;
    private int mAlpha;
    private double mCorrectiveDay;
    private double mBeta_t0;
    private double mPhiRH;
    private double mBeta_fcm;
    private double mPhi0;
    private double mBetac_t_t0;
    private double mPhi_t_t0;
    private double mEc_eff;
    private double mFinalEquivalentCoef;
    private double mInstantEquivalentCoef;

    private Inputs mInputs;
    private Geometry mGeometry;
    private Material mMaterial;

    public Creep(Inputs inputs, double concreteCompStress) {
        mInputs = inputs;
        mGeometry = mInputs.getGeometry();
        mMaterial = mInputs.getMaterial();
        double fcm_MPa = mMaterial.getFcm_MPa();
        double ecm_GPa = mMaterial.getEcm_GPa();
        double es_GPa = mMaterial.getEs_GPa();
        double averageRadius_mm = mGeometry.getAverageRadius() * 1000;
//        System.out.printf("h0 = %.3f%n", averageRadius_mm);

        double alpha3 = fcm_MPa > 35 ? Math.pow((35 / fcm_MPa), 0.5) : 1;
        mBetaH = 1.5 * (1 + Math.pow(0.012 * mRH_percent, 18)) * averageRadius_mm + 250 * alpha3;
//        System.out.printf("BetaH = %.3f%n", mBetaH);

        if (mCementClass.equals("R")){
            mAlpha = 1;
        } else if (mCementClass.equals("S")){
            mAlpha = -1;
        } else if (mCementClass.equals("N")){
            mAlpha = 0;
        }
        mCorrectiveDay = mLoadDay * Math.pow((9 / (2 + Math.pow(mLoadDay, 1.2)) + 1), mAlpha);
//        System.out.printf("t0c = %.1f jours%n", mCorrectiveDay);
        mBeta_t0 = 1 / (0.1 + Math.pow(mCorrectiveDay, 0.2));
//        System.out.printf("Beta(t0) = %.4f%n", mBeta_t0);

        double alpha1 = fcm_MPa > 35 ? Math.pow((35 / fcm_MPa), 0.7) : 1;
//        System.out.printf("alpha1 = %.2f%n", alpha1);
        double alpha2 = fcm_MPa > 35 ? Math.pow((35 / fcm_MPa), 0.2) : 1;
//        System.out.printf("alpha2 = %.2f%n", alpha2);
        mPhiRH = (1 +
                (1 - mRH_percent / 100) / (0.1 * Math.pow(averageRadius_mm, 1.0/3)) * alpha1
        ) * alpha2;
//        System.out.printf("PhiRH = %.3f%n", mPhiRH);

        mBeta_fcm = 16.8 / Math.sqrt(fcm_MPa);
//        System.out.printf("Beta(fcm) = %.4f%n", mBeta_fcm);
        mPhi0 = mPhiRH * mBeta_fcm * mBeta_t0;
//        System.out.printf("Phi0 = %.4f%n", mPhi0);

        mBetac_t_t0 = Math.pow((mCalculateDay - mLoadDay) / (mBetaH + mCalculateDay - mLoadDay), 0.3);
//        System.out.printf("Betac(t,t0) = %.4f%n", mBetac_t_t0);
        mPhi_t_t0 = mPhi0 * mBetac_t_t0;
//        System.out.printf("Phi(t, t0) = %.4f%n", mPhi_t_t0);

        mEc_eff = ecm_GPa / (1 + mPhi_t_t0);
//        System.out.printf("Ec,eff = %.2f%n", mEc_eff);

        mFinalEquivalentCoef = es_GPa / mEc_eff;
//        System.out.printf("Final equivalent Coef = %.2f%n", mFinalEquivalentCoef);
        mInstantEquivalentCoef = es_GPa / ecm_GPa;
//        System.out.printf("Instant equivalent Coef = %.2f%n", mInstantEquivalentCoef);
    }
}
