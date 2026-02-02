package com.scnsoft.eldermark.hl7v2.processor.patient.demographics;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.*;
import ca.uhn.hl7v2.model.v251.segment.*;
import com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.datatype.DataTypeConverter;
import com.scnsoft.eldermark.hl7v2.model.*;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class HL7v2PatientDemographicsDefault implements InitializingHL7v2PatientDemographics {

    protected final Message in;
    protected final MessageSource messageSource;
    protected final DataTypeConverter dataTypeService;

    protected MSH msh;
    protected PID pid;
    protected PV1 pv1;
    protected PV2 pv2;
    protected PD1 pd1;
    protected DG1 dg1;
    protected MRG mrg;

    private boolean initialized = false;

    public HL7v2PatientDemographicsDefault(Message in, MessageSource messageSource, DataTypeConverter dataTypeService) {
        this.in = in;
        this.messageSource = messageSource;
        this.dataTypeService = dataTypeService;
    }

    @Override
    public void init() {
        if (initialized) {
            return;
        }
        try {
            this.msh = getMSH();
            this.pid = getPID();
            this.pv1 = getPV1();
            try {
                this.pv2 = getPV2();
            } catch (HL7Exception var11) {
                this.pv2 = null;
            }

            try {
                this.dg1 = getDG1();
            } catch (HL7Exception var10) {
                this.dg1 = null;
            }

            try {
                this.pd1 = getPD1();
            } catch (HL7Exception var9) {
                this.pd1 = null;
            }

            try {
                this.mrg = getMRG();
            } catch (HL7Exception var8) {
                this.mrg = null;
            }
        } catch (HL7Exception var12) {
            throw new ExceptionInInitializerError(var12);
        }

        if (this.msh == null || this.pid == null) {
            throw new ExceptionInInitializerError();
        }
        initialized = true;
    }


    protected MSH getMSH() throws HL7Exception {
        return (MSH) in.get("MSH");
    }

    protected PID getPID() throws HL7Exception {
        return (PID) in.get("PID");
    }

    protected PV1 getPV1() throws HL7Exception {
        return (PV1) in.get("PV1");
    }

    protected PV2 getPV2() throws HL7Exception {
        return (PV2) in.get("PV2");
    }

    protected DG1 getDG1() throws HL7Exception {
        return (DG1) in.get("DG1");
    }

    protected PD1 getPD1() throws HL7Exception {
        return (PD1) in.get("PD1");
    }

    protected MRG getMRG() throws HL7Exception {
        return (MRG) in.get("MRG");
    }

    //    public List<PersonIdentifier> getPatientIds() {
//        List<PersonIdentifier> patientIds = new ArrayList();
//        PersonIdentifier identifier = new PersonIdentifier();
//        CX[] cxs = this.pid.getPatientIdentifierList();
//        CX[] arr$ = cxs;
//        int len$ = cxs.length;
//
//        for(int i$ = 0; i$ < len$; ++i$) {
//            CX cx = arr$[i$];
//            Identifier assignAuth = new Identifier(cx.getAssigningAuthority().getNamespaceID().getValue(), cx.getAssigningAuthority().getUniversalID().getValue(), cx.getAssigningAuthority().getUniversalIDType().getValue());
//            Identifier assignFac = new Identifier(cx.getAssigningFacility().getNamespaceID().getValue(), cx.getAssigningFacility().getUniversalID().getValue(), cx.getAssigningFacility().getUniversalIDType().getValue());
//            Identifier reconciledAssignAuth = AssigningAuthorityUtil.reconcileIdentifier(assignAuth, this.connection);
//            identifier.setAssigningAuthority(reconciledAssignAuth);
//            identifier.setAssigningFacility(assignFac);
//            identifier.setId(cx.getID().getValue());
//            identifier.setIdentifierTypeCode(cx.getIdentifierTypeCode().getValue());
//            patientIds.add(identifier);
//        }
//
//        return patientIds;
//    }

//    public List<PersonIdentifier> getMrgPatientIds() {
//        List<PersonIdentifier> patientIds = new ArrayList();
//        PersonIdentifier identifier = new PersonIdentifier();
//        CX[] cxs = this.mrg.getPriorPatientIdentifierList();
//        CX[] arr$ = cxs;
//        int len$ = cxs.length;
//
//        for(int i$ = 0; i$ < len$; ++i$) {
//            CX cx = arr$[i$];
//            Identifier assignAuth = new Identifier(cx.getAssigningAuthority().getNamespaceID().getValue(), cx.getAssigningAuthority().getUniversalID().getValue(), cx.getAssigningAuthority().getUniversalIDType().getValue());
//            Identifier assignFac = new Identifier(cx.getAssigningFacility().getNamespaceID().getValue(), cx.getAssigningFacility().getUniversalID().getValue(), cx.getAssigningFacility().getUniversalIDType().getValue());
//            Identifier reconciledAssignAuth = AssigningAuthorityUtil.reconcileIdentifier(assignAuth, this.connection);
//            identifier.setAssigningAuthority(reconciledAssignAuth);
//            identifier.setAssigningFacility(assignFac);
//            identifier.setId(cx.getID().getValue());
//            identifier.setIdentifierTypeCode(cx.getIdentifierTypeCode().getValue());
//            patientIds.add(identifier);
//        }
//
//        return patientIds;
//    }

    @Override
    public PersonIdentifier getPatientAccountNumber() {
        validateInitialized();
        CX cx = this.pid.getPatientAccountNumber();
        return cxToPersonIdentifier(cx);
    }


    public PersonIdentifier getMrgpatientAccountNumber() {
        validateInitialized();
        CX cx = this.mrg.getPriorPatientAccountNumber();
        return cxToPersonIdentifier(cx);
    }

    public PersonIdentifier cxToPersonIdentifier(CX cx) {
        PersonIdentifier identifier = new PersonIdentifier();
        Identifier assignAuth = new Identifier(cx.getAssigningAuthority().getNamespaceID().getValue(), cx.getAssigningAuthority().getUniversalID().getValue(), cx.getAssigningAuthority().getUniversalIDType().getValue());
        Identifier assignFac = new Identifier(cx.getAssigningFacility().getNamespaceID().getValue(), cx.getAssigningFacility().getUniversalID().getValue(), cx.getAssigningFacility().getUniversalIDType().getValue());
        identifier.setAssigningAuthority(assignAuth);
        identifier.setAssigningFacility(assignFac);
        identifier.setId(cx.getIDNumber().getValue());
        identifier.setIdentifierTypeCode(cx.getIdentifierTypeCode().getValue());
        return identifier;
    }

    @Override
    public PersonIdentifier getMothersId() {
        validateInitialized();
        if (this.pid.getMotherSIdentifier() == null || this.pid.getMotherSIdentifier().length == 0) {
            return null;
        }

        return cxToPersonIdentifier(this.pid.getMotherSIdentifier()[0]);
    }

    protected PersonName convertPersonName(XPN pidName) {
        PersonName pName = new PersonName();
        pName.setSuffix(pidName.getSuffixEgJRorIII().getValue());
        pName.setSecondName(pidName.getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().getValue());
        pName.setLastName(pidName.getXpn1_FamilyName().getFn1_Surname().getValue());
        pName.setFirstName(pidName.getGivenName().getValue());
        pName.setPrefix(pidName.getPrefixEgDR().getValue());
        pName.setNameTypeCode(pidName.getNameTypeCode().getValue());
        pName.setNameRepresentationCode(pidName.getNameRepresentationCode().getValue());
        pName.setDegree(pidName.getDegreeEgMD().getValue());
        return pName;
    }

    @Override
    public PersonName getPatientName() {
        validateInitialized();
        var pidName = this.pid.getPatientName(0);

        return convertPersonName(pidName);
    }

//    public PersonName getMrgPatientName() throws HL7Exception {
//        PersonName pName = new PersonName();
//        pName.setSuffix(this.mrg.getPriorPatientName(0).getSuffixEgJRorIII().getValue());
//        pName.setSecondName(this.mrg.getPriorPatientName(0).getMiddleInitialOrName().getValue());
//        pName.setLastName(this.mrg.getPriorPatientName(0).getFamilyLastName().getFamilyName().getValue());
//        pName.setFirstName(this.mrg.getPriorPatientName(0).getGivenName().getValue());
//        pName.setPrefix(this.mrg.getPriorPatientName(0).getPrefixEgDR().getValue());
//        pName.setNameTypeCode(this.mrg.getPriorPatientName(0).getNameTypeCode().getValue());
//        pName.setNameRepresentationCode(this.mrg.getPriorPatientName(0).getNameRepresentationCode().getValue());
//        pName.setDegree(this.mrg.getPriorPatientName(0).getDegreeEgMD().getValue());
//        return pName;
//    }

    @Override
    public PersonName getMotherMaidenName() {
        validateInitialized();
        var pidName = this.pid.getMotherSMaidenName(0);

        return convertPersonName(pidName);

    }

    @Override
    public PersonName getPatientAliasName() {
        validateInitialized();
        var pidName = this.pid.getPatientAlias(0);

        return convertPersonName(pidName);

    }

//    public DriversLicense getDriversLicense() {
//        DriversLicense lic = new DriversLicense();
//        lic.setLicenseNumber(this.pid.getDriverSLicenseNumberPatient().getDriverSLicenseNumber().getValue());
//        lic.setExpirationDate(DateUtil.convertHL7DateToCalender(this.pid.getDriverSLicenseNumberPatient().getExpirationDate().getValue()));
//        lic.setIssuingState(this.pid.getDriverSLicenseNumberPatient().getIssuingStateProvinceCountry().getValue());
//        return lic;
//    }

    @Override
    public String getRace() {
        validateInitialized();
        String race = this.pid.getRace(0).getText().getValue();
        return race;
    }

    @Override
    public String getPrimaryLanguage() {
        validateInitialized();
        return this.pid.getPrimaryLanguage().getText().getValue();
    }

    @Override
    public String getMaritalStatus() {
        validateInitialized();
        return this.pid.getMaritalStatus().getText().getValue();
    }

    @Override
    public String getReligion() {
        validateInitialized();
        return this.pid.getReligion().getText().getValue();
    }

    @Override
    public String getEthnicGroup() {
        validateInitialized();
        String ethnicGroup = this.pid.getEthnicGroup(0).getText().getValue();
        return ethnicGroup;
    }

    @Override
    public String getBirthPlace() {
        validateInitialized();
        return this.pid.getBirthPlace().getValue();
    }

    @Override
    public int getBirthOrder() {
        validateInitialized();
        return this.pid.getBirthOrder().getValue() == null ? 0 : Integer.parseInt(this.pid.getBirthOrder().getValue());
    }

    @Override
    public LocalDateTime getDeathDate() {
        validateInitialized();
        return dataTypeService.convertTSToLocalDateTime(this.pid.getPatientDeathDateAndTime());
    }

    @Override
    public List<AddressDemographics> getAddressList() {
        validateInitialized();
        List<AddressDemographics> addressList = new ArrayList<>();
        XAD[] xads = this.pid.getPatientAddress();
        XAD[] arr$ = xads;
        int len$ = xads.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            XAD xad = arr$[i$];


            AddressDemographics address = new AddressDemographics();
            address.setAddCity(xad.getCity().getValue());
            address.setAddCountry(xad.getCountry().getValue());
            address.setAddLine1(dataTypeService.getValue(xad.getStreetAddress().getSad1_StreetOrMailingAddress()));
            address.setAddLine2(xad.getOtherDesignation().getValue());
            address.setAddState(xad.getStateOrProvince().getValue());
            address.setAddType(this._mapAddressToBase(xad.getAddressType().getValue()));
            address.setAddZip(xad.getZipOrPostalCode().getValue());
            addressList.add(address);
        }

        return addressList;
    }

    private AddressDemographics.AddressType _mapAddressToBase(String value) {
        return AddressDemographics.AddressType.hl7ValueOf(value);
    }

    @Override
    public Gender getSexType() {
        validateInitialized();
        String sex = this.pid.getAdministrativeSex().getValue();
        return sex == null ? null : Gender.getByString(sex);
    }

    @Override
    public LocalDate getBirthDate() {
        validateInitialized();
        return dataTypeService.convertTSToLocalDate(this.pid.getDateTimeOfBirth());
    }

//    public List<Visit> getVisitList() {
//        if (this.pv1 == null) {
//            return null;
//        } else {
//            List<Visit> visitList = new ArrayList();
//            String systemId = this.getHomeSystem();
//            String visitId = this.pv1.getVisitNumber().getID().getValue();
//            if (!StringUtil.goodString(visitId)) {
//            }
//
//            Visit visit = new Visit(systemId, visitId);
//            visit.setProblemList(this.getProblemList());
//            visit.setProviderList(this.getProviderList());
//            visit.setReason(this.getVisitReason());
//            visit.setVisitEndTimestamp(this.getEndDate());
//            Date startDate = this.getStartDate();
//            visit.setVisitStartTimestamp(startDate == null ? (new GregorianCalendar()).getTime() : startDate);
//            visitList.add(visit);
//            return visitList;
//        }
//    }
//
//    public List<Visit> getMrgVisitList() {
//        List<Visit> visitList = new ArrayList();
//        String systemId = this.mrg.getPriorVisitNumber().getAssigningFacility().getNamespaceID().getValue();
//        String visitId = this.mrg.getPriorAlternateVisitID().getID().getValue();
//        if (!StringUtil.goodString(visitId)) {
//        }
//
//        Visit visit = new Visit(systemId, visitId);
//        visit.setProblemList(this.getProblemList());
//        visit.setProviderList(this.getProviderList());
//        visit.setReason(this.getVisitReason());
//        visit.setVisitEndTimestamp(this.getEndDate());
//        Date startDate = this.getStartDate();
//        visit.setVisitStartTimestamp(startDate == null ? (new GregorianCalendar()).getTime() : startDate);
//        visitList.add(visit);
//        return visitList;
//    }

//    private List<Problem> getProblemList() {
//        List<Problem> probList = new ArrayList();
//        if (this.dg1 != null) {
//            Problem problem = new Problem();
//            problem.setProbCode(this.dg1.getDiagnosisCodeDG1().getIdentifier().getValue());
//            problem.setProbCodeSystem(this.dg1.getDiagnosisCodingMethod().getValue());
//            problem.setProbName(this.dg1.getDiagnosisDescription().getValue());
//            probList.add(problem);
//        }
//
//        return probList;
//    }

//    private List<Provider> getProviderList() {
//        List<Provider> providerList = new ArrayList();
//        XCN[] xcns = this.pv1.getAttendingDoctor();
//        XCN[] arr$ = xcns;
//        int len$ = xcns.length;
//
//        for (int i$ = 0; i$ < len$; ++i$) {
//            XCN xcn = arr$[i$];
//            Provider provider = new Provider();
//            provider.setProviderId(xcn.getIDNumber().getValue());
//            provider.setProvNameFirst(xcn.getGivenName().getValue());
//            provider.setProvNameLast(xcn.getFamilyLastName().getName());
//            provider.setProvNameMiddle(xcn.getMiddleInitialOrName().getValue());
//            provider.setProvNameSuffix(xcn.getSuffixEgJRorIII().getValue());
//            provider.setProvNameTitle(xcn.getPrefixEgDR().getValue());
//            providerList.add(provider);
//        }
//
//        return providerList;
//    }

//    private String getVisitReason() {
//        return this.pv2 == null ? null : this.pv2.getAdmitReason().getText().getValue();
//    }

//    private Date getEndDate() {
//        String dob = this.pv1.getDischargeDateTime().getTimeOfAnEvent().getValue();
//        return DateUtil.convertHL7Date(dob);
//    }
//
//    private Date getStartDate() {
//        return DateUtil.convertHL7Date(this.pv1.getAdmitDateTime().getTimeOfAnEvent().getValue());
//    }

    @Override
    public String getSsn() {
        validateInitialized();
        return this.pid.getSSNNumberPatient().getValue();
    }


//    public String getHomeSystem() {
//        return this.msh.getSendingFacility().getNamespaceID().getValue();
//    }


    @Override
    public String getRaceIdentifier() {
        validateInitialized();
        return getCEIdentifier(this.pid.getPid10_Race(0));
    }

    @Override
    public String getPrimaryLanguageIdentifier() {
        validateInitialized();
        return getCEIdentifier(this.pid.getPid15_PrimaryLanguage());
    }

    @Override
    public String getMaritalStatusIdentifier() {
        validateInitialized();
        return getCEIdentifier(this.pid.getPid16_MaritalStatus());
    }

    @Override
    public String getReligionIdentifier() {
        validateInitialized();
        return getCEIdentifier(this.pid.getPid17_Religion());
    }

    @Override
    public String getEthnicGroupIdentifier() {
        validateInitialized();
        return getCEIdentifier(this.pid.getPid22_EthnicGroup(0));
    }

    public String getCitizenShipIdentifier() {
        return getCEIdentifier(this.pid.getPid26_Citizenship(0));
    }

    @Override
    public String getCitizenShip() {
        validateInitialized();
        return getCEName(this.pid.getPid26_Citizenship(0));
    }

    @Override
    public String getVeteranMilitaryStatusIdentifier() {
        validateInitialized();
        return getCEIdentifier(this.pid.getPid27_VeteransMilitaryStatus());
    }


    @Override
    public String getVeteranMilitaryStatus() {
        validateInitialized();
        return getCEName(this.pid.getPid27_VeteransMilitaryStatus());
    }

    public String getNationalityIdentifier() {
        return getCEIdentifier(this.pid.getPid28_Nationality());
    }


    public String getNationality() {
        return getCEName(this.pid.getPid28_Nationality());
    }

    protected String getCEIdentifier(CE element) {
        return element.getIdentifier().getValue();
    }

    protected String getCEName(CE element) {
        return element.getText().getValue();
    }


    @Override
    public List<PhoneNumber> getPhoneList() {
        validateInitialized();
        var phoneList = new ArrayList<PhoneNumber>();

        PhoneNumber e;

        if ((this.pid.getPhoneNumberHome() != null) && (this.pid.getPhoneNumberHome().length != 0)) {
            for (XTN phoneNumberHome : this.pid.getPhoneNumberHome()) {
                e = this._getPhoneNumber(phoneNumberHome);
                if (e != null) {
                    e.setType(PhoneNumber.PhoneType.HOME);
                    phoneList.add(e);
                }
            }
        }


        if ((this.pid.getPhoneNumberBusiness() != null) && (this.pid.getPhoneNumberBusiness().length != 0)) {
            for (XTN phoneNumberBusiness : this.pid.getPhoneNumberBusiness()) {
                e = this._getPhoneNumber(phoneNumberBusiness);
                if (e != null) {
                    e.setType(PhoneNumber.PhoneType.WORK);
                    phoneList.add(e);
                }
            }
        }

        return phoneList;
    }

//    public List<PhoneNumber> getEmails() {
//        if (true) {
//            //todo fix for phones instead of emails
//            throw new RuntimeException("NOT IMPLEMENTED PROPERLY");
//        }
//        var phoneList = new ArrayList<PhoneNumber>();
//
//        if (this.pid.get() != null) {
//            for (XTN phoneNumber : this.pid.getPhoneNumberHome()) {
//                PhoneNumber num = new PhoneNumber();
//                num.setType(SharedEnums.PhoneType.HOME);
//                num.setEmail(phoneNumber.getEmailAddress().getValue());
//                phoneList.add(num);
//            }
//        }
//
//        if (this.pid.getPhoneNumberBusiness() != null) {
//            for (XTN phoneNumber : this.pid.getPhoneNumberBusiness()) {
//                PhoneNumber num = new PhoneNumber();
//                num.setType(SharedEnums.PhoneType.WORK);
//                num.setEmail(phoneNumber.getEmailAddress().getValue());
//                phoneList.add(num);
//            }
//        }
//
//
//        return phoneList;
//    }

    private PhoneNumber _getPhoneNumber(XTN xtn) {
        PhoneNumber number = new PhoneNumber();
        String sNum = xtn.getXtn9_AnyText().getValue();
        if (sNum == null) {

            return null;
        } else {
            number.setAreaCode(this._parseAreaCode(sNum));
            number.setExtension(this._parseExtension(sNum));
            number.setNote(this._parseNote(sNum));
            number.setNumber(this._parseNumber(sNum));
            return number;
        }
    }

    private String _parseNumber(String sNum) {
        int sIndex = sNum.indexOf(")");
        if (sIndex < 0) {
            return sNum.substring(0, Math.min(8, sNum.length()));
        }
        if (sNum.length() > sIndex + 1) {
            return sNum.substring(sIndex + 1, Math.min(sIndex + 9, sNum.length()));
        }
        return null;
    }

    private String _parseNote(String sNum) {
        int sIndex = sNum.indexOf("C");
        return sIndex < 0 ? null : sNum.substring(sIndex + 1);
    }

    private String _parseExtension(String sNum) {
        int sIndex = sNum.indexOf("X");
        if (sIndex < 0) {
            return null;
        } else {
            int eIndex = sNum.indexOf("C");
            return eIndex < 0 ? sNum.substring(sIndex + 1) : sNum.substring(sIndex + 1, eIndex);
        }
    }

    private String _parseAreaCode(String sNum) {
        int sIndex = sNum.indexOf("(");
        return sIndex < 0 ? null : sNum.substring(sIndex + 1, sIndex + 4);
    }

    @Override
    public Boolean getDeathIndicator() {
        validateInitialized();
        String deathIndicator = this.pid.getPatientDeathIndicator().getValue();
        if (deathIndicator != null) {
            if (deathIndicator.equals("1")) {
                return true;
            } else return Boolean.valueOf(deathIndicator);
        }
        return false;
    }

    private void validateInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Patient demographics not initialized");
        }
    }
}
