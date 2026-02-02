package com.scnsoft.eldermark.api.shared.exception;

import org.apache.http.HttpStatus;

/**
 * @author averazub
 * @author phomal
 * Created on 12/30/2016.
 */
public enum PhrExceptionType {
    UNAUTHORIZED("unauthorized", "Unauthorized", HttpStatus.SC_UNAUTHORIZED),
    INVALID_TOKEN("token.invalid", "Token not valid", HttpStatus.SC_UNAUTHORIZED),
    NO_USER_FOUND("no.user.found", "There is no user with data provided. Please, register user", HttpStatus.SC_OK),
    USER_ALREADY_EXISTS("user.already.exists", "Such user already exists", HttpStatus.SC_OK),
    CTM_ALREADY_EXISTS("ctm.already.exists", "This user is already assigned to the patient as Care Team Member. Please check entered data and try again.", HttpStatus.SC_CONFLICT),
    EMPLOYEE_ALREADY_EXISTS("employee.already.exists", "User already has an associated employee in Web Simply Connect system. Use 'I have an account' link for registration.", HttpStatus.SC_CONFLICT),
    ACTIVITY_NOT_VALID("activity.not.processable", "Activity is not valid. Activity has to be of type either CallActivity or VideoActivity.", HttpStatus.SC_UNPROCESSABLE_ENTITY),
    USER_NOT_ACTIVATED("user.not.activated", "User is not yet activated", HttpStatus.SC_OK),
    USER_ALREADY_ACTIVATED("user.already.activated", "User already activated", HttpStatus.SC_OK),
    NO_CONFIRMATION_NEEDED("no.confirmation.needed", "No confirmation needed", HttpStatus.SC_OK),
    INVALID_REGISTRATION_CODE("invalid.registration.code", "The confirmation code you entered is incorrect or expired.", HttpStatus.SC_OK),
    INVALID_SSN("invalid.ssn", "Invalid Social Security Number", HttpStatus.SC_OK),
    INVALID_PASSWORD("invalid.password", "The entered password is invalid. Please check it and try again.", HttpStatus.SC_UNAUTHORIZED),
    BAD_CREDENTIALS("bad.credentials", "The provided credentials are invalid. Please check them and try again.", HttpStatus.SC_UNAUTHORIZED),
    UNABLE_TO_VALIDATE_PASSWORD("unable.to.validate.password", "Unable to validate password. Please, sign up again.", HttpStatus.SC_UNAUTHORIZED),
    INVALID_PASSWORD_COMPLEXITY("invalid.password.complexity", "The password does not meet the password complexity requirements.", HttpStatus.SC_OK),
    ACCOUNT_IS_LOCKED_OUT("account.is.locked.out", "Your account has been locked out because you have reached the maximum number of invalid logon attempts.", HttpStatus.SC_UNAUTHORIZED),
    ACCOUNT_IS_LOCKED_OUT_DURING_REGISTRATION("account.is.locked.out", "The registration process has been blocked because you have reached the maximum number of attempts to enter valid credentials. Please try again in %s minute(s).", HttpStatus.SC_UNAUTHORIZED),

    ACCESS_FORBIDDEN("access.forbidden", "Access Forbidden", HttpStatus.SC_FORBIDDEN),
    NOT_FOUND("not.found", "The specified entity can not be found", HttpStatus.SC_NOT_FOUND),
    USER_NOT_FOUND("user.not.found", "The specified user can not be found", HttpStatus.SC_NOT_FOUND),
    PHYSICIAN_NOT_FOUND("physician.not.found", "The specified physician can not be found", HttpStatus.SC_NOT_FOUND),
    EVENT_NOT_FOUND("event.not.found", "The specified event can not be found", HttpStatus.SC_NOT_FOUND),
    RESIDENT_OPTED_OUT_OR_DEACTIVATED("resident.opted.out.or.deactivated", "The specified resident has been opted out or deactivated and is not accessible at the moment.", HttpStatus.SC_NOT_FOUND),
    NOT_FOUND_PATIENT_INFO("patient.info.not.found", "Not found patient info for specified user", HttpStatus.SC_INTERNAL_SERVER_ERROR),
    NOT_FOUND_PATIENT_INFO_DURING_INVITATION("patient.info.not.found.during.invitation",
            "Not found patient info for the specified user. Patients that are not registered in Web SimplyConnect system can not have a care team.", HttpStatus.SC_INTERNAL_SERVER_ERROR),
    NOT_FOUND_EMPLOYEE_INFO("employee.info.not.found", "Not found employee info", HttpStatus.SC_INTERNAL_SERVER_ERROR),
    RESIDENT_RECORD_NOT_ASSOCIATED("resident.record.not.associated", "Specified resident is not associated with that user", HttpStatus.SC_FORBIDDEN),
    ACCOUNT_TYPE_NOT_AVAILABLE("account.type.not.available", "Specified account type is not available for this user", HttpStatus.SC_FORBIDDEN),
    SELF_INVITE_TO_CT("self.invite", "You can't invite yourself to your care team", HttpStatus.SC_UNPROCESSABLE_ENTITY),
    SELF_INVITE_TO_CT_PHYSICIAN("self.invite.physician", "You can't invite a physician to his/her own care team", HttpStatus.SC_UNPROCESSABLE_ENTITY),
    INVITEE_EMAIL_CONFLICT("invitee.email.conflict",
            "We can't invite the person based on the provided information. There's a registered person with the specified email, but other provided information seems to be incorrect. Please check the data and try again.", HttpStatus.SC_CONFLICT),
    USER_EMAIL_CONFLICT("user.email.conflict",
            "There's already a registered user with the specified email present. If it's you, in order to restore access to your account, please, go back and sign in via 'I have an account' link.", HttpStatus.SC_CONFLICT),
    DUPLICATED_EMAIL("duplicated.email", "There's already a registered person with the specified email.", HttpStatus.SC_CONFLICT),
    SECONDARY_EMAIL_IN_USE("duplicated.secondary.email", "This email is already used as your primary email address.", HttpStatus.SC_CONFLICT),
    SECONDARY_PHONE_IN_USE("duplicated.secondary.phone", "This phone is already used as your primary phone number.", HttpStatus.SC_CONFLICT),
    CTM_NOT_ASSOCIATED("ctm.not.associated", "Specified care team member is not associated with that user", HttpStatus.SC_FORBIDDEN),
    CR_NOT_ASSOCIATED("cr.not.associated", "Specified care receiver is not associated with your care team", HttpStatus.SC_FORBIDDEN),
    EVENT_NOT_ASSOCIATED("event.not.associated", "Specified event is not associated with that user", HttpStatus.SC_FORBIDDEN),
    NOTE_NOT_ASSOCIATED("note.not.associated", "Specified note is not associated with that user", HttpStatus.SC_FORBIDDEN),
    NO_ASSOCIATED_PATIENT_FOUND("no.associated.patients.found", "No patients with specified data found", HttpStatus.SC_OK),

    PROBLEM_NOT_FOUND("problem.not.found", "Problem not found", HttpStatus.SC_NOT_FOUND),
    IMMUNIZATION_NOT_FOUND("immunization.not.found", "Immunization not found", HttpStatus.SC_NOT_FOUND),

    NO_FAX_FOR_NOTIFICATION("no.fax.for.notification", "You have no fax. Please select other notification type or add fax number in Web Simply Connect system.", HttpStatus.SC_FORBIDDEN),
    NO_PHONE_FOR_NOTIFICATION("no.phone.for.notification", "You have no phone. Please select other notification type or add phone number in Web Simply Connect system.", HttpStatus.SC_FORBIDDEN),
    NO_EMAIL_FOR_NOTIFICATION("no.email.for.notification", "You have no email. Please select other notification type or add email in Web Simply Connect system.", HttpStatus.SC_FORBIDDEN),
    NO_SECURE_EMAIL_FOR_NOTIFICATION("no.secure.email.for.notification", "You have no secure email. Please select other notification type or add secure email in Web Simply Connect system.", HttpStatus.SC_FORBIDDEN),
    NO_PHONE_FOR_REGISTRATION("no.phone.for.registration", "Please add your mobile phone number on the “Edit Contact” screen in Web Simply Connect system to proceed with the PHR app registration.", HttpStatus.SC_FORBIDDEN),
    NO_EMAIL_FOR_REGISTRATION("no.email.for.registration", "Please add your email on the “Edit Contact” screen in Web Simply Connect system to proceed with the PHR app registration.", HttpStatus.SC_FORBIDDEN),
    NO_PHONE_AND_EMAIL_FOR_REGISTRATION("no.phone.and.email.for.registration", "Please add your mobile phone number and email on the “Edit Contact” screen in Web Simply Connect system to proceed with the PHR app registration.", HttpStatus.SC_FORBIDDEN),

    ARCHIVED_NOTE_MODIFICATION("note.archived.modification", "Trying to modify already archived note.", HttpStatus.SC_FORBIDDEN),
    /**
     * General unrecoverable error that should never happen
     */
    INTERNAL_SERVER_ERROR("internal.server.error", "Internal Server Error", HttpStatus.SC_INTERNAL_SERVER_ERROR),

    APP_VERSION_NOT_SUPPORTED("app.version.not.supported", "This feature isn't supported anymore. Please update your app to the latest version.", HttpStatus.SC_OK),

    REGISTRATION_FLOW_NOT_FOUND("registration.flow.not.found", "There is no registration application found with data provided. It may never have existed, not reviewed yet, or already have been completed.", HttpStatus.SC_OK),

    AVATAR_NOT_FOUND("avatar.not.found", "Avatar not found", HttpStatus.SC_NOT_FOUND),
    DOCUMENT_NOT_FOUND("document.not.found", "Document not found", HttpStatus.SC_NOT_FOUND),
    DOWNLOAD_ERROR("download.error", "Download error", HttpStatus.SC_INTERNAL_SERVER_ERROR),
    UPLOAD_ERROR("upload.error", "Upload error", HttpStatus.SC_INTERNAL_SERVER_ERROR),
    ATTACHMENT_TYPE_IS_NOT_ALLOWED("attachment.type.is.not.allowed",
            "Uploading this type of documents is not allowed. The following types of the files can be attached to the request: Word, PDF, XLS, TXT, XML, JPG, PNG.", HttpStatus.SC_BAD_REQUEST),
    NO_ADMIT_DATE_FOR_FOLLOW_UP_NOTE("no.admit.date.for.followUp.note", "No admit date provided for follow up note.", HttpStatus.SC_UNPROCESSABLE_ENTITY),
    NOT_ASSOCIATED_ADMIT_RECORD("not.associated.admit.record", "Admit date history record is not associated with provided resident.", HttpStatus.SC_FORBIDDEN),
    ALREADY_TAKEN_ADMIT_RECORD("already.taken.admit.record", "Specified note type has been already created for this admit/intake date.", HttpStatus.SC_FORBIDDEN),
    CANT_CREATE_APPOINTMENT("cant.create.appointment", "Not allowed to create appointments for specified marketplace", HttpStatus.SC_FORBIDDEN);


    private final String code;
    private final String message;
    private final int httpStatus;

    PhrExceptionType(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public int httpStatus() {
        return httpStatus;
    }
}
