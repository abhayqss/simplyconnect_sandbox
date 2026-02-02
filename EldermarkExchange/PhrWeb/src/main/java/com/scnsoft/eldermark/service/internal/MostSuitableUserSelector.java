package com.scnsoft.eldermark.service.internal;

import com.google.common.collect.Iterables;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.apache.commons.lang.StringUtils;

/**
 * A class for choosing a most suitable User from a collection of users.
 *
 * @author phomal
 * Created on 9/13/2017.
 */
public abstract class MostSuitableUserSelector {

    /**
     * Select a user that is the most similar to the specified resident
     */
    public static User selectBySimilarity(Iterable<User> users, Resident resident) {
        if (Iterables.isEmpty(users)) {
            return null;
        }

        final Person person = resident.getPerson();
        final String email = PersonService.getPersonTelecomValue(person, PersonTelecomCode.EMAIL);
        final String emailNormalized = Normalizer.normalizeEmail(email);

        // Select by matching resident
        for (User user : users) {
            if (resident.getId().equals(user.getResidentId())) {
                return user;
            }
        }

        // Select by matching email
        if (StringUtils.isNotBlank(emailNormalized)) {
            for (User user : users) {
                if (emailNormalized.equals(user.getEmailNormalized())) {
                    return user;
                }
            }
        }

        // Return any user if no matches
        return Iterables.get(users, 0);
    }

}
