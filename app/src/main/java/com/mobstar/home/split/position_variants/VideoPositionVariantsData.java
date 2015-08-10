package com.mobstar.home.split.position_variants;

/**
 * Created by vasia on 07.08.15.
 */
public class VideoPositionVariantsData {

    private boolean isWorkingVariant;
    private PositionVariant positionVariant;

    public VideoPositionVariantsData(PositionVariant positionVariant, boolean isWorkingVariant) {
        this.isWorkingVariant = isWorkingVariant;
        this.positionVariant = positionVariant;
    }

    public boolean isWorkingPositionVariant() {
        return isWorkingVariant;
    }

    public PositionVariant getVariant() {
        return positionVariant;
    }
}
