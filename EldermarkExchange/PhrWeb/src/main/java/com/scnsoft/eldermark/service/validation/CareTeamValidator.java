package com.scnsoft.eldermark.service.validation;

import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.NotificationSettingsDto;
import org.apache.commons.lang3.StringUtils;

/**
 * @author phomal
 * Created on 7/20/2017.
 */
public class CareTeamValidator {

    public static void validateCareReceiverAssociationOrThrow(Long employeeId, ResidentCareTeamMember careTeamMember) {
        if (careTeamMember == null || !employeeId.equals(careTeamMember.getEmployee().getId())) {
            throw new PhrException(PhrExceptionType.CR_NOT_ASSOCIATED);
        }
    }

    public static void validateNotificationChannelsAvailabilityOrThrow(ResidentCareTeamMember careReceiver, NotificationSettingsDto body) {
        final Person person = careReceiver.getEmployee().getPerson();
        for (NotificationType notificationType : body.getNotificationChannels().keySet()) {
            if (body.getNotificationChannels().get(notificationType)) {
                switch (notificationType) {
                    case FAX:
                        String fax = PersonService.getPersonTelecomValue(person, PersonTelecomCode.FAX);
                        if (StringUtils.isBlank(fax)) {
                            throw new PhrException(PhrExceptionType.NO_FAX_FOR_NOTIFICATION);
                        }
                        break;
                    case SMS:
                        String phone = PersonService.getPersonTelecomValue(person, PersonTelecomCode.WP);
                        if (StringUtils.isBlank(phone)) {
                            throw new PhrException(PhrExceptionType.NO_PHONE_FOR_NOTIFICATION);
                        }
                        break;
                    case EMAIL:
                        String email = PersonService.getPersonTelecomValue(person, PersonTelecomCode.EMAIL);
                        if (StringUtils.isBlank(email)) {
                            throw new PhrException(PhrExceptionType.NO_EMAIL_FOR_NOTIFICATION);
                        }
                        break;
                    case SECURITY_MESSAGE:
                        if (StringUtils.isBlank(careReceiver.getEmployee().getSecureMessaging())) {
                            throw new PhrException(PhrExceptionType.NO_SECURE_EMAIL_FOR_NOTIFICATION);
                        }
                        break;
                }
            }
        }
    }
}
