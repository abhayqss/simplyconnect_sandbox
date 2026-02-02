package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.exceptions.BusinessException;
import com.scnsoft.eldermark.shared.carecoordination.*;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by knetkachou on 1/9/2017.
 */
//@Controller
//@RequestMapping(value = "/transportation/{patientId}")
//@PreAuthorize(SecurityExpressions.IS_EXCHANGE_USER)
public class TransportationController {

    @Autowired
    PatientFacade patientFacade;

    @Autowired
    StateService stateService;

    @Value("${request.new.ride.url}")
    private String requestNewRideUrl;

    @Value("${history.ride.url}")
    private String historyRideUrl;

    @Value("${sso.url}")
    private String ssoUrl;

    @Value( "${expiration}" )
    private Integer expiration;

    @Value( "${issuer}" )
    private String issuer;

    @Value( "${subject}" )
    private String subject;

    @Value( "${secret}" )
    private String secret;


    @RequestMapping(value = "/request-new-ride", method = RequestMethod.GET)
    public String requestNewRide(@PathVariable("patientId") Long patientId, final Model model) throws UnsupportedEncodingException {
        return createJwt(requestNewRideUrl,patientId,model);
    }

    @RequestMapping(value = "/history-ride", method = RequestMethod.GET)
    public String historyRide(@PathVariable("patientId") Long patientId, final Model model) throws UnsupportedEncodingException {
        return createJwt(historyRideUrl,patientId,model);
    }


    private String createJwt(String redirectUrl, Long patientId, Model model) throws UnsupportedEncodingException {

//        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
//        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        PatientDto patient = patientFacade.getTransportationPatientDto(patientId);

        validate(patient);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        Date now = new Date();
        long nowMillis = now.getTime();
        long expMillis = nowMillis + expiration;
        Date exp = new Date(expMillis);

        AddressDto addressDto = patient.getAddress();

        if (addressDto == null){
            throw  new BusinessException("This patient has no address!");
        }
        KeyValueDto state = addressDto.getState();
        String stateAbbr = "MN";
        if(state != null) {
            State st = stateService.get(state.getId());
            if (st != null){
                stateAbbr = stateService.get(state.getId()).getAbbr();
            }
        }

        JwtBuilder builder = Jwts.builder().setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setNotBefore(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .setExpiration(exp)
                .claim("click2track.com/username", SecurityUtils.getAuthenticatedUser().getEmployee().getLoginName())
                .claim("click2track.com/company-id", SecurityUtils.getAuthenticatedUser().getCompanyCode())
                .claim("click2track.com/member-id", patientId)
                .claim("click2track.com/member-last-name", patient.getLastName())
                .claim("click2track.com/member-first-name", patient.getFirstName())

                .claim("click2track.com/member-dob", dateFormat.format(patient.getBirthDate()))
                .claim("click2track.com/member-gender", patient.getGender())
                .claim("click2track.com/member-phone", patient.getPhone())
                .claim("click2track.com/member-address-line-1", addressDto.getStreet())
                .claim("click2track.com/member-city", addressDto.getCity())
                .claim("click2track.com/member-zip", addressDto.getZip())
                .claim("click2track.com/member-state", stateAbbr)
                .claim("click2track.com/member-phone", patient.getPhone())
                //.signWith(signatureAlgorithm, signingKey);
                .signWith(
                        SignatureAlgorithm.HS512,
                        secret.getBytes("UTF-8"));

        model.addAttribute("payload", builder.compact());
//        model.addAttribute("payload", compact);
        model.addAttribute("redirectUrl", redirectUrl);
        model.addAttribute("ssoUrl", ssoUrl);
        return "transportation";
    }

    private void validate(PatientDto patient) {
        if (patient.getBirthDate() == null) {
            Calendar date = new GregorianCalendar(1900, Calendar.JANUARY, 1);
            patient.setBirthDate(date.getTime());

        }
        if (StringUtils.isBlank(patient.getPhone())) {
            patient.setPhone("1111111111");
        }
        if (StringUtils.isBlank(patient.getEmail())) {
            patient.setEmail("test@test.ts");
        }
        if (patient.getAddress()==null){
            patient.setAddress(new AddressDto());
        }
        AddressDto addressDto = patient.getAddress();
        if (StringUtils.isBlank(addressDto.getStreet())) {
            addressDto.setStreet("Test");
        }
        if (StringUtils.isBlank(addressDto.getCity())) {
            addressDto.setCity("Test");
        }
        if (StringUtils.isBlank(addressDto.getZip())) {
            addressDto.setZip("11111");
        }
    }
}
