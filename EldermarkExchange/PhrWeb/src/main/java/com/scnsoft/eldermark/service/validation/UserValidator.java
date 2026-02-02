package com.scnsoft.eldermark.service.validation;

import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.phr.Physician;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.apache.commons.lang.StringUtils;

/**
 * @author phomal
 * Created on 6/23/2017.
 */
public class UserValidator {

    public static void validatePhysicianNotExists(Physician physician) throws PhrException {
        if (physician != null)
            throw new PhrException(PhrExceptionType.USER_ALREADY_EXISTS);
    }

    public static void validateSecondaryPhone(User user, String secondaryPhone) {
        if (StringUtils.equals(user.getPhoneNormalized(), Normalizer.normalizePhone(secondaryPhone))) {
            throw new PhrException(PhrExceptionType.SECONDARY_PHONE_IN_USE);
        }
    }

    public static void validateSecondaryEmail(User user, String secondaryEmail) {
        if (StringUtils.equals(user.getEmailNormalized(), Normalizer.normalizeEmail(secondaryEmail))) {
            throw new PhrException(PhrExceptionType.SECONDARY_EMAIL_IN_USE);
        }
    }

    public static void validatePhoneAndEmailNotEmpty(String phone, String email) {
        if (StringUtils.isBlank(email) && StringUtils.isBlank(phone)) {
            throw new PhrException(PhrExceptionType.NO_PHONE_AND_EMAIL_FOR_REGISTRATION);
        }
        if (StringUtils.isBlank(email)) {
            throw new PhrException(PhrExceptionType.NO_EMAIL_FOR_REGISTRATION);
        }
        if (StringUtils.isBlank(phone)) {
            throw new PhrException(PhrExceptionType.NO_PHONE_FOR_REGISTRATION);
        }
    }

    public static void validatePhoneAndEmailNotEmpty(Person person) {
        final String email = PersonService.getPersonTelecomValue(person, PersonTelecomCode.EMAIL);
        final String phone = PersonService.getPersonTelecomValue(person, PersonTelecomCode.WP);
        validatePhoneAndEmailNotEmpty(phone, email);
    }

}
