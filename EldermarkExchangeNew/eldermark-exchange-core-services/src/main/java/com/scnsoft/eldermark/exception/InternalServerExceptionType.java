package com.scnsoft.eldermark.exception;

import com.scnsoft.eldermark.service.CareCoordinationConstants;
import org.springframework.http.HttpStatus;

public enum InternalServerExceptionType {

    FILE_IO_ERROR("file.io.error", "I/O error occurred during file upload.", HttpStatus.NOT_FOUND),
    FILE_NOT_DELETED("file.not.deleted", "file not deleted.", HttpStatus.INTERNAL_SERVER_ERROR),
    HUD_IO_ERROR("hud.io.error", "I/O error occurred during HUD report generation.", HttpStatus.INTERNAL_SERVER_ERROR),
    SDOH_IO_ERROR("SDOH.io.error", "I/O error occurred while while working with SDoH report.", HttpStatus.INTERNAL_SERVER_ERROR),
    EXCEL_IO_ERROR("excel.io.error", "I/O error occurred while while working with excel file.", HttpStatus.INTERNAL_SERVER_ERROR),
    COVID_19_IO_ERROR("covid19.io.error", "I/O error occurred during COVID-19 report generation.", HttpStatus.INTERNAL_SERVER_ERROR),
    AUTH_NOT_FOUND("auth.not.found", "There is no account corresponding to the data provided.", HttpStatus.UNAUTHORIZED),
    AUTH_CREDENTIALS_EXPIRED("auth.credentials.expired", "Your credentials has expired and must be changed.", HttpStatus.UNAUTHORIZED),
    AUTH_ACCOUNT_LOCKED("auth.account.locked", "Your account has been locked out because you have reached the maximum number of invalid logon attempts. Please try again in %d minutes.", HttpStatus.UNAUTHORIZED),
    AUTH_BAD_CREDENTIALS("auth.bad.credentials", "Invalid credentials.", HttpStatus.UNAUTHORIZED),
    AUTH_ACCOUNT_INACTIVE("auth.account.inactive", "You are not authorized to log in to this application. Please contact your Administrator for more details.", HttpStatus.UNAUTHORIZED),

    AUTH_ACCOUNT_CONFIRMED("auth.account.confirmed", "This account is not active yet. Please, sign in to the mobile app to complete your registration.", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_DENIED("auth.access.denied", "You don't have access to Simply Connect portal. Please contact your Administrator.", HttpStatus.UNAUTHORIZED),
    APP_ACCESS_DENIED("app.access.denied", "Sorry, you don't have access to this application.", HttpStatus.UNAUTHORIZED),
    AUTH_UNAUTHORIZED("auth.unauthorized", "You are not authorized to access Simply Connect.", HttpStatus.UNAUTHORIZED),
    AUTH_FORBIDDEN("auth.forbidden", "You are forbidden to access.", HttpStatus.FORBIDDEN),
    AUTH_RESET_REQUEST_INVALID("auth.password.reset.request.invalid", "Your reset link invalid, please enter Company ID and Email to reset your password.", HttpStatus.UNAUTHORIZED),
    DATE_FORMAT_INCORRECT("date.format.incorrect", "Date format incorrect. Date should have format MM/dd/yyyy.", HttpStatus.BAD_REQUEST),
    ASSESSMENT_EXPORT_FAILURE("assessment.export.failure", "Assessment export failure", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("not.found", "The specified entity can not be found.", HttpStatus.NOT_FOUND),
    EVENT_NOT_EDITABLE("events.not.editable", "Event can't be edited", HttpStatus.NOT_ACCEPTABLE),
    CDA_TRANSFORMATION_EXCEPTION("cda.transformation.failed", "Cda transformation error", HttpStatus.INTERNAL_SERVER_ERROR),
    HL7_GENERATION_EXCEPTION("hl7.generation.failed", "HL7 message generation error", HttpStatus.INTERNAL_SERVER_ERROR),
    HL7_PROCESSING_EXCEPTION("hl7.processing.failed", "HL7 message processing error", HttpStatus.INTERNAL_SERVER_ERROR),
    SFTP_COMMUNICATION_FAILED("sftp.communication.error", "SFTP communication failed", HttpStatus.INTERNAL_SERVER_ERROR),
    XDS_COMMUNICATION_FAILED("xds.communication.error", "Xds communication failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DOCUTRACK_COMMUNICATION_FAILED("docutrack.communication.error", "DocuTrack communication failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PDF_GENERATION_ERROR("pdf.generation..error", "Error occurred during pdf document generation.", HttpStatus.INTERNAL_SERVER_ERROR),
    TIFF_CONVERSION_ERROR("tiff.conversion.error", "Error occurred during conversion to TIFF", HttpStatus.INTERNAL_SERVER_ERROR),
    PDF_CONVERSION_ERROR("pdf.conversion.error", "Error occurred during conversion to PDF", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_UPDATE_INCORRECT_CONTACT_STATUS("password.update.incorrect.contact.status","Contact status is incorrect to create/update password.", HttpStatus.FORBIDDEN),
    MISSING_REQUIRED_FIELDS("missing.required.fields","Required fields are empty.", HttpStatus.UNPROCESSABLE_ENTITY),
    PASSWORD_COMPLEXITY_VALIDATION_FAILURE("password.complexity.validation.failure","The password you entered doesn't meet complexity requirements.", HttpStatus.UNPROCESSABLE_ENTITY),
    PASSWORD_HISTORY_VALIDATION_FAILURE("password.history.validation.failure","In order to secure your account, please create a unique password you have not used before.", HttpStatus.UNPROCESSABLE_ENTITY),
    AVATAR_ERROR("avatar.error", "Failed to retreive avatar", HttpStatus.UNPROCESSABLE_ENTITY),
    CDA_PARSING_ERROR("cda.parsing.error", "Failed to parse CDA", HttpStatus.INTERNAL_SERVER_ERROR),
    TWILIO_ERROR("twilio.error", "Twilio error",  HttpStatus.UNPROCESSABLE_ENTITY),

    MEDI_SPAN_ERROR("medispan.error", "Medi-Span error",  HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_IMPLEMENTED("not.implemented", "service not implemented",  HttpStatus.NOT_IMPLEMENTED),

    //TODO move to validation exception after http status refactoring to have object instead of int
    DOCUMENTS_INCORRECT_TYPE("documents.incorrect.type", "Unsupported file type. Supported file types: Word, PDF, Excel, TXT, JPEG, GIF, PNG, TIFF, TIF, XML.", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENTS_NULL("documents.null", "Document is null, please upload the document properly.", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENTS_INCORRECT_SIZE("documents.incorrect.size", "Document size should be less than " +CareCoordinationConstants.MAX_FILE_SIZE_MB + " MB.", HttpStatus.UNPROCESSABLE_ENTITY),
    LABS_APOLLO_UNAVAILABLE("labs.apollo.unavailable", "Failed to create Lab Order: Apollo Labs software is unavailable. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED("access.denied", "Access is denied", HttpStatus.INTERNAL_SERVER_ERROR),
    ENCRYPTION_KEY_GENERATION_ERROR("encryption.key.generation.error", "Error while generating encryption key", HttpStatus.INTERNAL_SERVER_ERROR),
    ENCRYPTION_ERROR("encryption.error", "Error while encrypting file", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    InternalServerExceptionType(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
