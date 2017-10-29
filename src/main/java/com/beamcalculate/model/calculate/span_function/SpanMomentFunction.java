package com.beamcalculate.model.calculate.span_function;

import com.beamcalculate.model.calculate.support_moment.SupportMoment;
import com.beamcalculate.model.entites.Geometry;
import com.beamcalculate.model.entites.Load;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SpanMomentFunction extends AbstractSpanMoment {

    public SpanMomentFunction(SupportMoment supportMoment) {
        mSupportMoment = supportMoment;

        // add spanId and Map to spanMomentMap

        for (int i = 0; i < Geometry.getNumSpan(); i++) {
            Map<Integer, Function<Double, Double>> loadCaseMomentFunctionMap = new HashMap();
            for (int j = 0; j < Geometry.getNumSpan() + 1; j++) {
                loadCaseMomentFunctionMap.put(j, null);
            }
            mSpanMomentFunctionMap.put(i + 1, loadCaseMomentFunctionMap);
        }

        // add span_function moment function to the mSpanMomentFunctionMap

        mSpanMomentFunctionMap.forEach((spanId, loadCaseMomentFunctionMap) -> {
            for (int loadCase = 0; loadCase < loadCaseMomentFunctionMap.size(); loadCase++) {
                double leftSupportMoment = mSupportMoment.getMomentValueOfSupport(spanId, loadCase);
                double rightSupportMoment = mSupportMoment.getMomentValueOfSupport(spanId + 1, loadCase);
                Function<Double, Double> momentFunction;
                double thisSpanLength;
                double thisSpanLoad;

                thisSpanLength = mSupportMoment.getCalculateSpanLengthMap().get(spanId);

                if (loadCase == 0) {
                    thisSpanLoad = Load.getGMNm();
                } else {
                    if (loadCase == spanId) {
                        thisSpanLoad = Load.getQMNm();
                    } else {
                        thisSpanLoad = 0;
                    }
                }

                momentFunction = (x -> thisSpanLoad * x * (thisSpanLength - x) / 2 +
                        leftSupportMoment * (1 - x / thisSpanLength) +
                        rightSupportMoment * x / thisSpanLength);

                loadCaseMomentFunctionMap.put(loadCase, momentFunction);
            }
        });
    }


}
