package com.mobstar.home.split.position_variants;

/**
 * Created by vasia on 07.08.15.
 */
public class VideoPositionVariantsItem {

    private boolean isWorkingVariant;
    private PositionVariant positionVariant;

    public VideoPositionVariantsItem(PositionVariant positionVariant, boolean isWorkingVariant) {
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
