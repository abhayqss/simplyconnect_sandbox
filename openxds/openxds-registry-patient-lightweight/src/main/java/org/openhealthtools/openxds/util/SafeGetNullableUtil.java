package org.openhealthtools.openxds.util;

public class SafeGetNullableUtil {

    public static <R> R safeNpeGet(Supplier<R> supplier) {
        try {
            return supplier.supply();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
