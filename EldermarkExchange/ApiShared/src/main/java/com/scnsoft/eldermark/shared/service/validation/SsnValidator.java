package com.scnsoft.eldermark.shared.service.validation;

import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;

import java.util.regex.Pattern;

/**
 * Validate that the character sequence (e.g. string) is a valid SSN using a regular expression.
 *
 * @author phomal
 * Created on 6/12/2017.
 */
public class SsnValidator {

    public static boolean isValidSsn(CharSequence ssn) {
        // Some Social Security Numbers are invalid and will never be issued.
        // SSNs beginning with 000, 666 or numbers with Area number greater than 899 will never be issued.
        // See https://en.wikipedia.org/wiki/Social_Security_number#Valid_SSNs
        Pattern pattern = Pattern.compile("^(?!(000|666|9))\\d{3}(?!00)\\d{2}(?!0000)\\d{4}$");
        return pattern.matcher(ssn).matches();
    }

    public static void validateSsnOrThrow(CharSequence ssn) {
        if (!isValidSsn(ssn)) {
            throw new PhrException(PhrExceptionType.INVALID_SSN);
        }
    }

}
