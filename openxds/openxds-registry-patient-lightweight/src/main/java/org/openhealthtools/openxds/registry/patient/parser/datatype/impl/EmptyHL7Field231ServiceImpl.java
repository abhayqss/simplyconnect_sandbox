package org.openhealthtools.openxds.registry.patient.parser.datatype.impl;

import ca.uhn.hl7v2.model.AbstractPrimitive;
import ca.uhn.hl7v2.model.v231.datatype.*;
import org.apache.commons.lang.StringUtils;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmptyHL7Field231ServiceImpl implements EmptyHL7Field231Service {

    @Autowired
    private DataTypeService dataTypeService;

    @Override
    public boolean isTSEmpty(final TS tsDate) {
        return tsDate == null || tsDate.getTimeOfAnEvent() == null || isAbstractPrimitiveEmpty(tsDate.getTimeOfAnEvent());
    }

    @Override
    public boolean isCEEmpty(CE ce) {
        return ce == null || isAbstractPrimitiveEmpty(ce.getIdentifier())
                && isAbstractPrimitiveEmpty(ce.getText())
                && isAbstractPrimitiveEmpty(ce.getNameOfCodingSystem())
                && isAbstractPrimitiveEmpty(ce.getAlternateIdentifier())
                && isAbstractPrimitiveEmpty(ce.getAlternateText())
                && isAbstractPrimitiveEmpty(ce.getNameOfAlternateCodingSystem());
    }

    @Override
    public boolean isCXEmpty(CX cx) {
        return cx == null || isAbstractPrimitiveEmpty(cx.getID())
                && isAbstractPrimitiveEmpty(cx.getCheckDigit())
                && isAbstractPrimitiveEmpty(cx.getCodeIdentifyingTheCheckDigitSchemeEmployed())
                && isHDEmpty(cx.getAssigningAuthority())
                && isAbstractPrimitiveEmpty(cx.getIdentifierTypeCode())
                && isHDEmpty(cx.getAssigningFacility());
    }

    @Override
    public boolean isCXArrayEmpty(CX[] cxArray) {
        if (cxArray == null) {
            return true;
        }
        for (CX cx : cxArray) {
            if (!isCXEmpty(cx)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isXPNEmpty(XPN xpn) {
        return xpn == null || isFNEmpty(xpn.getFamilyLastName())
                && isAbstractPrimitiveEmpty(xpn.getGivenName())
                && isAbstractPrimitiveEmpty(xpn.getMiddleInitialOrName())
                && isAbstractPrimitiveEmpty(xpn.getSuffixEgJRorIII())
                && isAbstractPrimitiveEmpty(xpn.getPrefixEgDR())
                && isAbstractPrimitiveEmpty(xpn.getDegreeEgMD())
                && isAbstractPrimitiveEmpty(xpn.getNameTypeCode())
                && isAbstractPrimitiveEmpty(xpn.getNameRepresentationCode());
    }

    @Override
    public boolean isXPNArrayEmpty(XPN[] xpnArray) {
        if (xpnArray == null) {
            return true;
        }
        for (XPN xpn : xpnArray) {
            if (!isXPNEmpty(xpn)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isHDEmpty(HD hd) {
        return hd == null || isAbstractPrimitiveEmpty(hd.getNamespaceID())
                && isAbstractPrimitiveEmpty(hd.getUniversalID())
                && isAbstractPrimitiveEmpty(hd.getUniversalIDType());
    }

    @Override
    public boolean isFNEmpty(FN fn) {
        return fn == null || isAbstractPrimitiveEmpty(fn.getFamilyName())
                && isAbstractPrimitiveEmpty(fn.getLastNamePrefix());
    }

    @Override
    public boolean isAbstractPrimitiveEmpty(final AbstractPrimitive abstractPrimitive) {
        return abstractPrimitive == null || StringUtils.isBlank(abstractPrimitive.getValue());
    }

    public DataTypeService getDataTypeService() {
        return dataTypeService;
    }

    public void setDataTypeService(final DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }
}
