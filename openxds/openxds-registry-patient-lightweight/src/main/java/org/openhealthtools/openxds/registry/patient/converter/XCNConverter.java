package org.openhealthtools.openxds.registry.patient.converter;

import ca.uhn.hl7v2.model.v231.datatype.XCN;
import org.openhealthtools.openxds.entity.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class XCNConverter implements Converter<XCN, XCNExtendedCompositeIdNumberAndNameForPersons> {

    @Autowired
    private DataTypeService dataTypeService;

    @Override
    public XCNExtendedCompositeIdNumberAndNameForPersons convert(final XCN xcn231) {
        XCNExtendedCompositeIdNumberAndNameForPersons xcn = new XCNExtendedCompositeIdNumberAndNameForPersons();
        if (xcn231.getGivenName() != null)
            xcn.setFirstName(xcn231.getGivenName().getValue());
        if (xcn231.getFamilyLastName() != null)
            xcn.setLastName(xcn231.getFamilyLastName().getFamilyName().getValue());
        if (xcn231.getMiddleInitialOrName() != null)
            xcn.setMiddleName(xcn231.getMiddleInitialOrName().getValue());
        if (xcn231.getPrefixEgDR() != null)
            xcn.setPrefix(xcn231.getPrefixEgDR().getValue());
        if (xcn231.getSuffixEgJRorIII() != null)
            xcn.setSuffix(xcn231.getSuffixEgJRorIII().getValue());
        if (xcn231.getDegreeEgMD() != null)
            xcn.setDegree(xcn231.getDegreeEgMD().getValue());
        if (xcn231.getSourceTable() != null)
            xcn.setSourceTable(xcn231.getSourceTable().getValue());
        if (xcn231.getAssigningAuthority() != null)
            xcn.setAssigningAuthority(getDataTypeService().createHd(xcn231.getAssigningAuthority()));
        if (xcn231.getAssigningFacility() != null)
            xcn.setAssigningFacility(getDataTypeService().createHd(xcn231.getAssigningFacility()));
        if (xcn231.getNameTypeCode() != null)
            xcn.setNameTypeCode(xcn231.getNameTypeCode().getValue());
        if (xcn231.getIdentifierTypeCode() != null)
            xcn.setIdentifierTypeCode(xcn231.getIdentifierTypeCode().getValue());
        if (xcn231.getNameRepresentationCode() != null)
            xcn.setNameRepresentationCode(xcn231.getNameRepresentationCode().getValue());
        return xcn;
    }

    public DataTypeService getDataTypeService() {
        return dataTypeService;
    }

    public void setDataTypeService(final DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }
}
