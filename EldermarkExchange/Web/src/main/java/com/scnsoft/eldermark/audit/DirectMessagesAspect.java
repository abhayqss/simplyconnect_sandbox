package com.scnsoft.eldermark.audit;

import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.entity.DirectErrorCode;
import com.scnsoft.eldermark.facades.DirectMessagesFacade;
import com.scnsoft.eldermark.services.direct.DirectAccountDetails;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.xml.ws.soap.SOAPFaultException;

@Aspect
@Order(2)
@Component
public class DirectMessagesAspect {
    @Autowired
    private DirectMessagesFacade directMessagesFacade;

    @Autowired
    private EmployeeDao employeeDao;


    // Deactivates the account and saves error code for manager when Registration failed
    @Pointcut("execution(* com.scnsoft.eldermark.facades.DirectMessagesFacade.registerDirectAccount(..))")
    public void registerAccountMethod() {}

    @Pointcut("registerAccountMethod() && args(employeeId, ..)")
    public void registerAccountWithArg(Long employeeId) {}

    @AfterThrowing(pointcut = "registerAccountWithArg(employeeId)",
                   throwing = "error")
    public void afterThrowingError(Long employeeId, SOAPFaultException error) throws Throwable {
        handleSoapFaultExceptions(employeeId, error);
    }


    // Deactivates the account and saves error code for manager if access to mailbox was lost
    @Pointcut("execution(* com.scnsoft.eldermark.facades.DirectMessagesFacade.getInboxMessage*(..)) || " +
              "execution(* com.scnsoft.eldermark.facades.DirectMessagesFacade.deleteMessage(..)) || " +
              "execution(* com.scnsoft.eldermark.facades.DirectMessagesFacade.sendMessage(..)) || " +
              "execution(* com.scnsoft.eldermark.facades.DirectMessagesFacade.directorySearch(..))")
    public void allMethods() {}

    @Pointcut("allMethods() && args(.., directAccount)")
    public void allMethodsWithDirectAccount(DirectAccountDetails directAccount) {}

    @AfterThrowing(pointcut = "allMethodsWithDirectAccount(directAccount)",
                   throwing = "error")
    public void afterThrowingError(DirectAccountDetails directAccount, SOAPFaultException error) throws Throwable {
        Long employeeId = employeeDao.getEmployeeIdBySecureEmail(directAccount.getSecureEmail());
        handleSoapFaultExceptions(employeeId, error);
    }


    // Transforms SES errors to DirectErrorCodes
    private void handleSoapFaultExceptions(Long employeeId, SOAPFaultException e) {
        String errorMessage = e.getMessage();
        if (errorMessage == null) {
            directMessagesFacade.deactivateSecureMessaging(employeeId, DirectErrorCode.UNKNOWN, "SOAP Error Message is null.");
            return;
        }

        if (errorMessage.contains("Request not Authorized for target ")) {
            directMessagesFacade.deactivateSecureMessaging(employeeId, DirectErrorCode.REQUEST_NOT_AUTHORIZED);
        } else if (errorMessage.contains("Error Register Name Is Invalid.")) {
            directMessagesFacade.deactivateSecureMessaging(employeeId, DirectErrorCode.EMPLOYEE_NOT_VALID);
        } else if (!("Error Sending message Error Sending message: No valid To or Cc email addresses".equals(errorMessage) ||
                     "Error Sending message: Can't send a blank message with no attachments or subject".equals(errorMessage))) {
            directMessagesFacade.deactivateSecureMessaging(employeeId, DirectErrorCode.UNKNOWN, errorMessage);
        }
    }
}