package com.scnsoft.eldermark.ws.server;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.facades.ResidentFacade;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilterWsDto;
import com.scnsoft.eldermark.ws.server.exceptions.ContractViolationException;
import com.scnsoft.eldermark.ws.server.exceptions.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.jws.WebService;
import java.util.List;

//@WebService(
//        endpointInterface = "com.scnsoft.eldermark.ws.server.ResidentsEndpoint",
//        targetNamespace = Constants.WEB_SERVICES_NAMESPACE_RESIDENTS,
//        wsdlLocation = "wsdl/residents.wsdl")
//@PreAuthorize(
//        SecurityExpressions.IS_ELDERMARK_USER)
public class ResidentsEndpointImpl extends SpringBeanAutowiringSupport implements ResidentsEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(ResidentsEndpointImpl.class);

    @Autowired
    private ResidentFacade residentFacade;

    public @Value("${patient.discovery.ssn.required}") boolean ssnRequired;
    public @Value("${patient.discovery.dateOfBirth.required}") boolean dateOfBirthRequired;

//    @Resource
//    private WebServiceContext context;

    @Override
    public List<ResidentDto> searchResidents(ResidentFilterWsDto filter) {
//        HttpServletRequest servletRequest = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        if (filter.getFirstName() == null) {
            throw new ContractViolationException("First name is required");
        }

        if (filter.getLastName() == null) {
            throw new ContractViolationException("Last name is required");
        }

        if (filter.getGender() == null) {
            throw new ContractViolationException("Gender is required");
        }

        if (dateOfBirthRequired && filter.getDateOfBirth() == null) {
            throw new ContractViolationException("Date of Birth is required");
        }

        try {
            return residentFacade.getResidents(filter);
        } catch (Exception e) {
            logger.error("Patient discovery failed: " + filter.toString(), e);
            throw new InternalServerException();
        }
    }
}
