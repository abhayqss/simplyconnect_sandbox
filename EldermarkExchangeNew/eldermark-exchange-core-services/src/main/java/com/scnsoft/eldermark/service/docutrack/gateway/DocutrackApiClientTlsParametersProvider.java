package com.scnsoft.eldermark.service.docutrack.gateway;

import org.apache.cxf.configuration.jsse.TLSClientParameters;

import java.util.Optional;
import java.util.function.Supplier;

public interface DocutrackApiClientTlsParametersProvider extends Supplier<Optional<TLSClientParameters>> {

}
