package com.scnsoft.eldermark.api.shared.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author phomal
 * Created on 6/12/2017.
 */
public class RegistrationUtils {

    public static Long generateConfirmationCode() {
        return ThreadLocalRandom.current().nextLong(1, 9999);
    }

}
