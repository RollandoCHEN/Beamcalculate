package com.beamcalculate.model.entites;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Map;

import static com.beamcalculate.model.MyMethods.round;

public class Geometry {
    private final IntegerProperty mNumSpan = new SimpleIntegerProperty();
    private final IntegerProperty mNumSupport = new SimpleIntegerProperty();
    private final DoubleProperty mSectionWidth = new SimpleDoubleProperty();
    private final DoubleProperty mSectionHeight = new SimpleDoubleProperty();
    private final DoubleProperty mSlabThickness = new SimpleDoubleProperty();
    private final BooleanProperty mOnTSection = new SimpleBooleanProperty();
    private final DoubleProperty mPerpendicularSpacing = new SimpleDoubleProperty();
    private final DoubleProperty mEffectiveHeight = new SimpleDoubleProperty();
    private final DoubleProperty mCoverThickness_cm = new SimpleDoubleProperty(3);
    private double mTotalLength = 0.0;

    private Map<Integer, Double> mSpansLengthMap = new HashMap<>();        // Not be able to use MapProperty, cause not be able to set (k,v) to it
    private Map<Integer, Double> mEffectiveSpansLengthMap = new HashMap<>();
    private Map<Integer, Double> mSupportWidthMap = new HashMap<>();


    public Geometry() {
        mNumSupport.bind(Bindings.add(mNumSpan,1));
        mEffectiveHeight.bind(Bindings.multiply(mSectionHeight, 0.9));
    }

    public DoubleProperty effectiveHeightProperty() {
        return mEffectiveHeight;
    }

    public double getEffectiveHeight() {
        return mEffectiveHeight.get();
    }

    public int getNumSpan() {
        return mNumSpan.get();
    }

    public IntegerProperty numSpanProperty() {
        return mNumSpan;
    }

    public int getNumSupport() {
        return mNumSupport.get();
    }

    public IntegerProperty numSupportProperty() {
        return mNumSupport;
    }

    public Map<Integer,Double> spansLengthMap() {
        return mSpansLengthMap;
    }

    public Map<Integer,Double> supportWidthMap() {
        return mSupportWidthMap;
    }

    public double getSectionWidth() {
        return mSectionWidth.get();
    }

    public DoubleProperty sectionWidthProperty() {
        return mSectionWidth;
    }

    public double getSectionHeight() {
        return mSectionHeight.get();
    }

    public DoubleProperty sectionHeightProperty() {
        return mSectionHeight;
    }

    public boolean isOnTSection() {
        return mOnTSection.get();
    }

    public BooleanProperty onTSectionProperty() {
        return mOnTSection;
    }

    public double getSlabThickness() {
        return mSlabThickness.get();
    }

    public DoubleProperty slabThicknessProperty() {
        return mSlabThickness;
    }

    public double getPerpendicularSpacing() {
        return mPerpendicularSpacing.get();
    }

    public DoubleProperty perpendicularSpacingProperty() {
        return mPerpendicularSpacing;
    }

    public double getCoverThickness_cm() {
        return mCoverThickness_cm.get();
    }

    public DoubleProperty coverThickness_cmProperty() {
        return mCoverThickness_cm;
    }

    public double getTotalLength() {
        mTotalLength = 0;
        if (mSpansLengthMap.size()==0){
            return 0.0;
        }else {
            mSpansLengthMap.forEach((k, v)-> mTotalLength += v);
            mSupportWidthMap.forEach((k, v)-> mTotalLength += v);
            return mTotalLength;
        }
    }

    public Map<Integer, Double> getEffectiveSpansLengthMap() {
        spansLengthMap().forEach((spanId, spanLength)-> mEffectiveSpansLengthMap.put(
                spanId,
                round(spanLength + supportWidthMap().get(spanId) /2 + supportWidthMap().get(spanId + 1) /2,2)
        ));
        return mEffectiveSpansLengthMap;
    }
}
