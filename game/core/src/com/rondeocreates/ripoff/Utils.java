package com.rondeocreates.ripoff;

public class Utils {
    /**
     * Returns a value closer to a goal amount by the given increment.
     */
    public static float approach(float start, float target, float increment) {
        increment = Math.abs(increment);
        if (start < target) {
            start += increment;
            
            if (start > target) {
                start = target;
            }
        } else {
            start -= increment;
            
            if (start < target) {
                start = target;
            }
        }
        return start;
    }
}
