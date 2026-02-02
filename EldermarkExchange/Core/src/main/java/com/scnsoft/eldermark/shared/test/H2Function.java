package com.scnsoft.eldermark.shared.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;

/**
 * H2 functions for tests.
 *
 * @author phomal
 * Created on 9/25/2017.
 */
@SuppressWarnings("unused")
public class H2Function {

    /**
     * Calculates the SHA-256 digest of {@code text}, divides it by {@code divider} and returns the remainder int.
     *
     * @param text text to digest. Date conversion should be configured to convert dates the same way as in MS SQL Server database (at the moment of writing it's 'yyyy-mm-dd').
     * @param divider divider. defaults to 5000
     */
    public static int sha256(String text, Integer divider) {
        if (divider == null) {
            divider = 5000;
        }

        byte bytes[] = DigestUtils.sha256(StringUtils.lowerCase(text));
        int length = bytes.length;
        final int DATA_TYPE_SIZE = 4;   // 32-bit INT (SQL) = 4 bytes
        int result = ByteBuffer.wrap(ArrayUtils.subarray(bytes, length - DATA_TYPE_SIZE, length)).getInt() % divider;
        return result;
    }

}
