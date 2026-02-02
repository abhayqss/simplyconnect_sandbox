package com.scnsoft.eldermark.exception;

import org.springframework.http.HttpStatus;

public enum BusinessExceptionType {

    //TODO Business Exceptions should not include exceptions that sre not related to business login (like io exception, another kind of Excpetion should be created like "ServerException"
    NOT_FOUND("not.found", "The specified entity can not be found.", HttpStatus.NOT_FOUND.value()),
    ACCESS_DENIED("access.denied", "Forbidden.", HttpStatus.FORBIDDEN.value()),
    NOT_RELATED_ENTITIES("not.related.entities", "Not related entities.", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    DOCUMENT_NOT_FOUND("not.found", "The specified document is not found.", HttpStatus.NOT_FOUND.value()),
    DOCUMENT_NOT_VISIBLE("document.not.visible", "The specified document is not found.", HttpStatus.NOT_FOUND.value()), //todo maybe we shouldn't expose to ui that document is not visible and use not found instead.
    FILE_ALREADY_EXISTS("file.already.exists", "The file is already existing.", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    FILE_IO_ERROR("file.io.error", "I/O error occurred during file upload.", HttpStatus.NOT_FOUND.value()),
    FILE_SAVE_INTERNAL_ERROR("file.save.internal.error", "Failed to save file due to some internal error.", HttpStatus.NOT_FOUND.value()),
    FILE_IS_EMPTY_OR_CORRUPTED("file.empty", "Sorry, empty or corrupted files can't be uploaded to Simply Connect.", HttpStatus.NOT_FOUND.value()),
    FAILED_TO_DELETE_FILE("failed.delete.file", "Failed to delete the specified file from storage.", HttpStatus.NOT_FOUND.value()),
    USER_WITH_EMAIL_ALREADY_EXISTS("user.already.exists", "User with given email already exists in the system.", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    USER_WITH_SECURE_EMAIL_ALREADY_EXISTS("user.already.exists.secure.email", "User with given secure email already exists in the system.", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    UNSUPPORTED_NOTIFICATION_TYPE("unsupported.notification.type", "Unsupported notification type", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    SDOH_DATA_ERROR("sdoh.data.error", "There are data errors in the report.", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    SDOH_ZIP_NOT_DOWNLOADED("sdoh.zip.not.downloaded", "You can't mark the report as sent to UHC until you download ZIP file.", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    SDOH_ALREADY_SENT("sdoh.already.sent", "SDoH report was already marked as sent.", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    INTUNE_REPORT_NO_DATA("report.intune.no.data", "No data available to generate the report.", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    INTUNE_REPORT_NO_TRIGGERS("report.intune.no.triggers", "No trigger questions in the last two assessments", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    ASSESSMENT_DEFAULT_DATA_NOT_ENABLED("assessment.default.not.enabled", "Default data is not enabled for this assessment type", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    ASSESSMENT_CREATE_NOT_AVAILABLE("assessment.create.not.available", "Cannot create assessment other assessment", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    ASSESSMENT_DEFAULT_NOT_AVAILABLE("assessment.default.not.available", "Cannot fill default values without parentAssessmentResultId", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    CHAT_DISABLED("chat.disabled", "Chat functionality is not available", HttpStatus.FORBIDDEN.value()),
    VIDEO_CALL_DISABLED("videocall.disabled", "Video call functionality is not available", HttpStatus.FORBIDDEN.value()),
    APPOINTMENT_CANCEL_NOT_AVAILABLE("appointment.cancel.not.available", "Cannot cancel appointment", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    APPOINTMENT_EDIT_NOT_AVAILABLE("appointment.edit.not.available", "Cannot edit appointment", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    APPOINTMENT_OVERLAPS_EXISTING("appointment.overlaps.existing", "Some participants are busy during appointment time", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    APPOINTMENT_CREATE_NOT_AVAILABLE("appointment.create.not.available", "Cannot create appointment", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    APPOINTMENT_COMPLETE_NOT_AVAILABLE("appointment.complete.not.available", "Cannot complete appointment", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    APPOINTMENT_COMPLETE_NOT_AVAILABLE_IN_FUTURE("appointment.complete.not.available.in.furure", "The appointment can't be completed before the appointment occurs", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    DOCUMENT_NO_ENCRYPTION_KEY("document.no.encryption.key", "No encryption key found.", HttpStatus.NOT_FOUND.value()),
    DOCUMENT_MULTIPLE_ENCRYPTION_KEYS("document.multiple.encryption.keys", "Multiple encryption keys found.", HttpStatus.NOT_FOUND.value()),
    DOCUMENT_ENCRYPTION_KEY_ALREADY_EXISTS("document.encryption.key.already.exists", "Encryption key already exists.", HttpStatus.UNPROCESSABLE_ENTITY.value()),
    NO_PRIMARY_CONTACT("primary.contact.not.found", "Primary contact not found", HttpStatus.NOT_FOUND.value()),
    ASSESSMENT_HOUSING_IN_PROCESS_ALREADY_EXISTS("assessment.housing.in.process.already.exists", "Housing assessment with in process status already exists", HttpStatus.UNPROCESSABLE_ENTITY.value());

    private final String code;
    private final String message;
    private final int httpStatus;

    BusinessExceptionType(String code, String message, int httpStatus) {
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
