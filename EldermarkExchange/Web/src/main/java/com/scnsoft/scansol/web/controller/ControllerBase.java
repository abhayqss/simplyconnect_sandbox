package com.scnsoft.scansol.web.controller;

import com.scnsoft.scansol.shared.codebase.exceptions.CloudScanValidationException;
import com.scnsoft.scansol.shared.codebase.exceptions.CommunityWasRemovedException;
import com.scnsoft.scansol.shared.codebase.exceptions.RecordNotFoundException;
import com.scnsoft.scansol.shared.enums.ERROR_CODE;
import com.scnsoft.scansol.shared.enums.STATUS;
import com.scnsoft.scansol.shared.response.ScanSolResponseBase;

import javax.persistence.NonUniqueResultException;
import java.util.logging.Logger;

public class ControllerBase {

    //protected static final Logger logger = Logger.getLogger ("ControllerBase.class");

    protected ScanSolResponseBase createFailureResponse (final String message) {
     //   logger.error (message);
        if(message.contains("Invalid"))
            return new ScanSolResponseBase (STATUS.DUPLICATE, ERROR_CODE.COMMON_ERROR, message);
        else {
            return new ScanSolResponseBase(STATUS.FAILURE, ERROR_CODE.COMMON_ERROR, message);
        }
    }

    protected ScanSolResponseBase createFailureResponse (final ERROR_CODE errorCode, final String message) {
     //   logger.error (message);

        return new ScanSolResponseBase (STATUS.FAILURE, errorCode, message);
    }

    protected ScanSolResponseBase createSuccessResponse () {
        return new ScanSolResponseBase (STATUS.SUCCESS, "The request has been successfully processed!");
    }

    protected abstract class RequestHandler {

        public ScanSolResponseBase handle () {
            try {
                validateRequest ();

                try {
                    return doExecute ();
                }
                catch (final RecordNotFoundException e) {
                    return createFailureResponse (ERROR_CODE.RECORD_NOT_FOUND, e.getMessage ());
                }
                catch (final CommunityWasRemovedException e) {
                    return createFailureResponse (ERROR_CODE.COMMUNITY_WAS_REMOVED, e.getMessage ());
                }
                catch (final NonUniqueResultException e) {
                    return createFailureResponse (ERROR_CODE.NON_UNIQUE_RESULT, e.getMessage ());
                }
                catch (final Throwable th) {
                    throw new CloudScanValidationException (th.getMessage ());
                }
            }
            catch (final CloudScanValidationException e) {
                return createFailureResponse (e.getMessage ());
            }
        }

        public abstract void validateRequest () throws CloudScanValidationException;

        public ScanSolResponseBase doExecute () throws Exception {
            return createSuccessResponse ();
        }
    }
}
