package com.beamcalculate.model.entites;

import com.beamcalculate.Main;
import com.beamcalculate.model.NamedDoubleProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.HashMap;
import java.util.Map;

public class Geometry {
    private final static IntegerProperty mNumSpan = new SimpleIntegerProperty();
    private final static IntegerProperty mNumSupport = new SimpleIntegerProperty();
    private final static NamedDoubleProperty mSectionWidth = new NamedDoubleProperty();
    private final static NamedDoubleProperty mSectionHeight = new NamedDoubleProperty();
    private final static NamedDoubleProperty mSlabThickness = new NamedDoubleProperty();
    private final static NamedDoubleProperty mPerpendicularSpacing = new NamedDoubleProperty();
    private final static NamedDoubleProperty mEffectiveHeight = new NamedDoubleProperty();
    private static double mTotalLength = 0.0;

    private static Map<Integer, Double> mSpansLengthMap = new HashMap<>();        // Not be able to use MapProperty, cause not be able to set (k,v) to it
    private static Map<Integer, Double> mEffectiveSpansLengthMap = new HashMap<>();
    private static Map<Integer, Double> mSupportWidthMap = new HashMap<>();


    public Geometry() {
        mNumSupport.bind(Bindings.add(mNumSpan,1));
        mEffectiveHeight.bind(Bindings.multiply(mSectionHeight, 0.9));
    }

    public static NamedDoubleProperty effectiveHeightProperty() {
        return mEffectiveHeight;
    }

    public static double getEffectiveHeight() {
        return mEffectiveHeight.get();
    }

    public static int getNumSpan() {
        return mNumSpan.get();
    }

    public IntegerProperty numSpanProperty() {
        return mNumSpan;
    }

    public static int getNumSupport() {
        return mNumSupport.get();
    }

    public IntegerProperty numSupportProperty() {
        return mNumSupport;
    }

    public static Map<Integer,Double> spansLengthMap() {
        return mSpansLengthMap;
    }

    public static Map<Integer,Double> supportWidthMap() {
        return mSupportWidthMap;
    }

    public static double getSectionWidth() {
        return mSectionWidth.get();
    }

    public static NamedDoubleProperty sectionWidthProperty() {
        return mSectionWidth;
    }

    public static double getSectionHeight() {
        return mSectionHeight.get();
    }

    public static NamedDoubleProperty sectionHeightProperty() {
        return mSectionHeight;
    }

    public static double getSlabThickness() {
        return mSlabThickness.get();
    }

    public static NamedDoubleProperty slabThicknessProperty() {
        return mSlabThickness;
    }

    public static double getPerpendicularSpacing() {
        return mPerpendicularSpacing.get();
    }

    public static NamedDoubleProperty perpendicularSpacingProperty() {
        return mPerpendicularSpacing;
    }

    public static double getTotalLength() {
        mTotalLength = 0;
        if (mSpansLengthMap.size()==0){
            return 0.0;
        }else {
            mSpansLengthMap.forEach((k, v)-> mTotalLength += v);
            mSupportWidthMap.forEach((k, v)-> mTotalLength += v);
            return mTotalLength;
        }
    }

    public static Map<Integer, Double> getEffectiveSpansLengthMap() {
        spansLengthMap().forEach((spanId, spanLength)-> mEffectiveSpansLengthMap.put(
                spanId,
                spanLength + supportWidthMap().get(spanId) /2 + supportWidthMap().get(spanId + 1) /2
        ));
        return mEffectiveSpansLengthMap;
    }
}
