package com.scnsoft.eldermark.shared.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author phomal
 * Created on 7/5/2017.
 */
public final class MathUtils {

    private MathUtils() throws IllegalAccessException {
        throw new IllegalAccessException("MathUtils is non-instantiable.");
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static long ceil(double value) {
        return new Double(Math.ceil(value)).longValue();
    }

}
