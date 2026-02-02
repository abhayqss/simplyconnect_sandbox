package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.datatype;

import ca.uhn.hl7v2.model.AbstractPrimitive;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v251.datatype.*;
import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7DefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DataTypeConverter {

    CECodedElement createCE(CE ce231);

    CECodedElement createCE(CWE cwe);

    List<CECodedElement> createCEList(CE[] ces231);

    CECodedElement createCEWithCodedValues(CE ce231, Class<? extends HL7CodeTable> tableClasses);

    List<CECodedElement> createCEListWithCodedValues(CE[] ces231, Class<? extends HL7CodeTable> tableClasses);

    CECodedElement createCEWithCodedValuesFromIS(IS is231, Class<? extends HL7CodeTable> tableClasses);

    EIEntityIdentifier createEI(EI ei);

    EIPEntityIdentifierPair createEIP(EIP eip);

    DRDateRange createDR(DR dr);

    CXExtendedCompositeId createCX(CX cx231);

    List<CXExtendedCompositeId> createCXList(CX[] cx231Array);

    XCNExtendedCompositeIdNumberAndNameForPersons createXCN(XCN xcn231);

    XCNExtendedCompositeIdNumberAndNameForPersons createFirstPresentXCN(XCN[] xnc231List);

    List<XCNExtendedCompositeIdNumberAndNameForPersons> createXCNList(XCN[] xnc231List);

    XONExtendedCompositeNameAndIdForOrganizations createXON(XON xon231);

    List<XONExtendedCompositeNameAndIdForOrganizations> createXONList(XON[] xons231);

    HDHierarchicDesignator createHd(HD identifier);

    MSGMessageType createMSG(MSG msg);

    Instant convertTS(TS tsDate);

    LocalDateTime convertTSToLocalDateTime(TS tsDate);

    LocalDate convertTSToLocalDate(TS tsDate);

    Instant convertDtToInstant(DT dt);

    Instant convertHL7Date(String fromDate);

    PLPatientLocation createPL(PL pl);

    DLDDischargeLocation createDLD(DLD dld);

    XTNPhoneNumber createXTN(XTN xtn);

    List<XTNPhoneNumber> createXTNList(XTN[] xtns);

    XPNPersonName createXPN(XPN xpn231);

    List<XPNPersonName> createXPNList(XPN[] xpns231);

    XADPatientAddress createXAD(XAD xad);

    List<XADPatientAddress> createXADList(XAD[] xads231);

    <T extends HL7UserDefinedCodeTable> ISCodedValueForUserDefinedTables<T> createIS(IS is231, Class<T> tableClass);

    //used for backwards compatibility
    <T extends HL7UserDefinedCodeTable> ISCodedValueForUserDefinedTables<T> createISFromCE(CE ce213, Class<T> tableClass);

    <T extends HL7UserDefinedCodeTable> List<ISCodedValueForUserDefinedTables<T>> createISList(IS[] is231Array, Class<T> tableClass);

    <T extends HL7DefinedCodeTable> IDCodedValueForHL7Tables<T> createID(ID id231, Class<T> tableClass);

    List<String> createStringList(ST[] st231Array);

    List<String> createStringList(Varies[] varies);

    List<String> createStringList(FT[] st231Array);

    Integer convertNM(NM nm231);

    DLNDriverSLicenseNumber createDLN(DLN dln231);

    PTProcessingType convertPT(PT source);

    String getValue(AbstractPrimitive abstractPrimitive);
}
