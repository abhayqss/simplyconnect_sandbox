package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.IDCodedValueForHL7Tables;
import com.scnsoft.eldermark.entity.xds.datatype.XADPatientAddress;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7DefinedCodeTable;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XADPatientAddressDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class XADPatientAddressTransformer extends ListAndItemTransformer<XADPatientAddress, XADPatientAddressDto> {

    @Autowired
    private Converter<IDCodedValueForHL7Tables<? extends HL7DefinedCodeTable>, String> idCodedValueForHL7TablesStringConverter;

    @Override
    public XADPatientAddressDto convert(XADPatientAddress xadPatientAddress) {
        if (xadPatientAddress == null) {
            return null;
        }
        XADPatientAddressDto target = new XADPatientAddressDto();
        target.setStreetAddress(xadPatientAddress.getStreetAddress());
        target.setOtherDesignation(xadPatientAddress.getOtherDesignation());
        target.setCity(xadPatientAddress.getCity());
        target.setState(xadPatientAddress.getState());
        target.setZip(xadPatientAddress.getZip());
        target.setCountry(idCodedValueForHL7TablesStringConverter.convert(xadPatientAddress.getCountry()));
        target.setAddressType(idCodedValueForHL7TablesStringConverter.convert(xadPatientAddress.getAddressType()));
        target.setCounty(xadPatientAddress.getCounty());
        target.setOtherGeographicDesignation(xadPatientAddress.getOtherGeographicDesignation());
        target.setCensusTract(xadPatientAddress.getCensusTract());
        target.setAddressRepresentationCode(idCodedValueForHL7TablesStringConverter.convert(xadPatientAddress.getAddressRepresentationCode()));
        return target;
    }
}
