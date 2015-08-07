package com.mobstar.home.split;

/**
 * Created by vasia on 07.08.15.
 */
public class VideoPositionVariants {


    private boolean isWorkingVariant;
    private PositionVariant positionVariant;

    public VideoPositionVariants(PositionVariant positionVariant, boolean isWorkingVariant) {
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
