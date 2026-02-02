package com.scnsoft.eldermark.ws.server;


import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilterWsDto;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

//@WebService(targetNamespace = Constants.WEB_SERVICES_NAMESPACE_RESIDENTS)
public interface ResidentsEndpoint {
//    @WebMethod
    List<ResidentDto> searchResidents(
//            @XmlElement(name = "residentFilter", required = true)
            ResidentFilterWsDto filter
    );
}
