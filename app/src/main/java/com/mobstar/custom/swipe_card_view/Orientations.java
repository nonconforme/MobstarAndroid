package com.mobstar.custom.swipe_card_view;

/**
 * Created by lipcha on 14.09.15.
 */
public class Orientations {
    public Orientations() {
    }

    public static enum Orientation {
        Ordered,
        Disordered;

        private Orientation() {
        }

        public static Orientations.Orientation fromIndex(int index) {
            Orientations.Orientation[] values = values();
            if(index >= 0 && index < values.length) {
                return values[index];
            } else {
                throw new IndexOutOfBoundsException();
            }
        }
    }
}
