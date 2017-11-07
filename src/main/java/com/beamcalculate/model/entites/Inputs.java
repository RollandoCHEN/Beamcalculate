package com.beamcalculate.model.entites;

/**
 * Created by Ruolin on 04/11/2017 for BeamCalculator.
 */
public class Inputs {
    private final Geometry mGeometry;
    private final Load mLoad;
    private final Material mMaterial;

    public Inputs(Geometry geometry, Load load, Material material) {
        mGeometry = geometry;
        mLoad = load;
        mMaterial = material;
    }

    public Geometry getGeometry() {
        return mGeometry;
    }

    public Load getLoad() {
        return mLoad;
    }

    public Material getMaterial() {
        return mMaterial;
    }
}
