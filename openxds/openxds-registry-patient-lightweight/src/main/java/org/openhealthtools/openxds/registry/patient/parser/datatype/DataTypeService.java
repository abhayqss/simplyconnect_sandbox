package org.openhealthtools.openxds.registry.patient.parser.datatype;

import ca.uhn.hl7v2.model.AbstractPrimitive;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.datatype.*;
import com.misyshealthcare.connect.base.demographicdata.Address;
import com.misyshealthcare.connect.base.demographicdata.PhoneNumber;
import com.misyshealthcare.connect.net.Identifier;
import org.openhealthexchange.openpixpdq.data.PersonName;
import org.openhealthtools.openxds.entity.datatype.*;
import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable;
import org.openhealthtools.openxds.entity.hl7table.HL7DefinedCodeTable;
import org.openhealthtools.openxds.entity.hl7table.HL7UserDefinedCodeTable;

import java.util.Date;
import java.util.List;

public interface DataTypeService {

    CECodedElement createCE(CE ce231);

    List<CECodedElement> createCEList(CE[] ces231);

    XTNPhoneNumber createXTNPhoneNumber(PhoneNumber phoneNumber);

    XADPatientAddress createXADPatientAddress(List<Address> addresses);

    XADPatientAddress createXADPatientAddress(Address address);

    XPNPersonName createPersonName(PersonName patientName);

    XCNExtendedCompositeIdNumberAndNameForPersons createXCN(XCN[] xnc231List);

    XCNExtendedCompositeIdNumberAndNameForPersons createXCN(XCN xcn231);

    XONExtendedCompositeNameAndIdForOrganizations createXON(XON xon231);

    CXExtendedCompositeId createCX(org.openhealthexchange.openpixpdq.data.PersonIdentifier patientIdentifier);

    CXExtendedCompositeId createCX(CX cx231);

    List<CXExtendedCompositeId> createCXList(CX[] cx231Array);

    HDHierarchicDesignator createHd(Identifier identifier);

    HDHierarchicDesignator createHd(HD identifier);

    Date convertTsToDate(TS tsDate);

    Date convertHL7Date(String fromDate);

    Date convertDtToDate(DT dt) throws DataTypeException;

    PLPatientLocation createPL(PL pl);

    DLDDischargeLocation createDLD(DLD dld);

    String getValue(AbstractPrimitive abstractPrimitive);

    XTNPhoneNumber createXTN(XTN xtn);

    List<XTNPhoneNumber> createXTNList(XTN[] xtns);

    List<XONExtendedCompositeNameAndIdForOrganizations> createXONList(XON[] xons231);

    List<XPNPersonName> createXPNList(XPN[] xpns231);

    XPNPersonName createXPN(XPN xpn231);

    XADPatientAddress createXAD(XAD xad);

    List<XADPatientAddress> createXADList(XAD[] xads231);

    <T extends HL7UserDefinedCodeTable> ISCodedValueForUserDefinedTables<T> createIS(IS is231, Class<T> tableClass);

    <T extends HL7UserDefinedCodeTable> List<ISCodedValueForUserDefinedTables<T>> createISList(IS[] is231Array, Class<T> tableClass);

    CECodedElement createCEWithCodedValues(CE ce231, Class<? extends HL7CodeTable> ... tableClasses);

    List<CECodedElement> createCEListWithCodedValues(CE[] ces231, Class<? extends HL7CodeTable> ... tableClasses);

    CECodedElement createCEWithCodedValuesFromIS(IS is231, Class<? extends HL7CodeTable> ... tableClasses);

    <T extends HL7DefinedCodeTable> IDCodedValueForHL7Tables<T> createID(ID id231, Class<T> tableClass);

    <T extends HL7DefinedCodeTable> IDCodedValueForHL7Tables<T> createID(String rawCode, Class<T> tableClass);

    List<String> createStringList(ST[] st231Array);

    Integer convertNM(NM nm231);

    DLNDriverSLicenseNumber createDLN(DLN dln231);

}
