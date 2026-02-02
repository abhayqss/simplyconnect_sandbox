package com.scnsoft.exchange.adt.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 * Created by averazub on 10/6/2016.
 */
@Component
public class Iti8Iti30BaseController extends ItiController {
    protected SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssZ");

    @Value("${xds.exchange.hl7v2.keystore}")
    protected String keyStorePath;

    @Value("${xds.exchange.hl7v2.keystore.password}")
    protected String keyStorePassword;

    @Value("${xds.exchange.hl7v2.truststore}")
    protected String trustStorePath;

    @Value("${xds.exchange.hl7v2.truststore.password}")
    protected String trustStorePassword;
}
