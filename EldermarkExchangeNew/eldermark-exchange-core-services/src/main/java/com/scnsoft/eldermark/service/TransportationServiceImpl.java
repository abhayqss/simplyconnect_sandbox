package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.TransportationDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TransportationServiceImpl implements TransportationService {


    @Value("${transportation.integration.enabled}")
    private Boolean transportationEnabled;

    @Value("${transportation.request.new.ride.url}")
    private String requestNewRideUrl;

    @Value("${transportation.history.ride.url}")
    private String historyRideUrl;

    @Value("${transportation.sso.url}")
    private String ssoUrl;

    @Value("${transportation.expiration}")
    private Integer expiration;

    @Value("${transportation.issuer}")
    private String issuer;

    @Value("${transportation.subject}")
    private String subject;

    @Value("${transportation.secret}")
    private String secret;

    @Autowired
    private ClientService clientService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private Converter<Address, AddressDto> addressDtoConverter;

    @Override
    public TransportationDto requestNewRide(Long clientId) {
        TransportationDto result = new TransportationDto();
        result.setUrl(requestNewRideUrl);
        result.setToken(createJwt(clientId));
        return result;
    }

    @Override
    public TransportationDto rideHistory(Long clientId) {
        TransportationDto result = new TransportationDto();
        result.setUrl(historyRideUrl);
        result.setToken(createJwt(clientId));
        return result;
    }

    private String createJwt(Long patientId) {
        if (BooleanUtils.isNotTrue(transportationEnabled)) {
            throw new ValidationException("Transportation integration is disabled");
        }

        Client client = clientService.getById(patientId);
        Employee employee = loggedUserService.getCurrentEmployee();
        AddressDto addressDto = addressDtoConverter.convert(client.getPerson().getAddresses().get(0));

        Date now = new Date();
        long nowMillis = now.getTime();
        long expMillis = nowMillis + expiration;
        Date exp = new Date(expMillis);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        var key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        JwtBuilder builder = null;
        builder = Jwts.builder().setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setNotBefore(now)
                .setSubject(String.valueOf(client.getId()))
                .setIssuer(issuer)
                .setExpiration(exp)
                .claim("click2track.com/client-id", client.getId())
                .claim("click2track.com/client-first-name", client.getFirstName())
                .claim("click2track.com/client-last-name", client.getLastName())
                .claim("click2track.com/client-dob", dateTimeFormatter.format(client.getBirthDate()))
                .claim("click2track.com/client-email", PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.EMAIL).orElse(PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.EMAIL).orElse("")))
                .claim("click2track.com/client-address-line-1", addressDto.getStreet())
                .claim("click2track.com/client-city", addressDto.getCity())
                .claim("click2track.com/client-zip", addressDto.getZip())
                .claim("click2track.com/client-state", addressDto.getStateName())
                .claim("click2track.com/client-phone", PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.MC)
                        .orElse(PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.HP)
                                .orElse(Optional.ofNullable(client.getCommunity().getPhone())
                                        .orElse(client.getOrganization().getAddressAndContacts().getPhone()))))
                .claim("click2track.com/employee-id", employee.getId())
                .claim("click2track.com/employee-email", PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.EMAIL).orElse(""))
                .claim("click2track.com/employee-first-name", employee.getFirstName())
                .claim("click2track.com/employee-last-name", employee.getLastName())
                .claim("click2track.com/employee-org-id", employee.getOrganizationId())
                .claim("click2track.com/employee-com-id", employee.getCommunityId())
                .claim("click2track.com/employee-phone", PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.MC).orElse(""))
                .signWith(key, SignatureAlgorithm.HS512);
        return builder.compact();
    }

}
