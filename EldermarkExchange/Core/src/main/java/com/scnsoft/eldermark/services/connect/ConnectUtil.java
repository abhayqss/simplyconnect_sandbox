package com.scnsoft.eldermark.services.connect;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import gov.hhs.fha.nhinc.common.nhinccommon.*;

import java.text.SimpleDateFormat;

public class ConnectUtil {

    private static SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

    public final static String EXCHANGE_HCID = "2.16.840.1.113883.3.6492";

    public static AssertionType createAssertion(ExchangeUserDetails employeeInfo) {

        AssertionType assertOut = new AssertionType();
        CeType purposeCoded = new CeType();
        UserType user = new UserType();
        PersonNameType userPerson = new PersonNameType();
        CeType userRole = new CeType();
        HomeCommunityType userHc = new HomeCommunityType();
        user.setPersonName(userPerson);
        user.setOrg(userHc);
        user.setRoleCoded(userRole);
        assertOut.setUserInfo(user);
        assertOut.setPurposeOfDisclosureCoded(purposeCoded);
        assertOut.setHomeCommunity(userHc);

        userPerson.setGivenName(employeeInfo.getEmployeeFirstName());
        userPerson.setFamilyName(employeeInfo.getEmployeeLastName());
        userPerson.setSecondNameOrInitials("DW");

        userHc.setHomeCommunityId("urn:oid:" + EXCHANGE_HCID);
        userHc.setName(employeeInfo.getAlternativeDatabaseId());
        user.setUserName(employeeInfo.getEmployeeLogin());

        userRole.setCode("307969004");
        userRole.setCodeSystem("2.16.840.1.113883.6.96");
        userRole.setCodeSystemName("SNOMED_CT");
        userRole.setCodeSystemVersion("1.0");
        userRole.setDisplayName("Public Health");
        userRole.setOriginalText("Public Health");
        assertOut.setAuthorized(true);

        purposeCoded.setCode("PUBLICHEALTH");
        purposeCoded.setCodeSystem("2.16.840.1.113883.3.18.7.1");
        purposeCoded.setCodeSystemName("nhin-purpose");
        purposeCoded.setCodeSystemVersion("1.0");
        purposeCoded.setDisplayName("Use or disclosure of Psychotherapy Notes");
        purposeCoded.setOriginalText("Use or disclosure of Psychotherapy Notes");


        SamlAuthnStatementType samlAuthnStatementType = new SamlAuthnStatementType();
        assertOut.setSamlAuthnStatement(samlAuthnStatementType);
        //samlAuthnStatementType.setAuthInstant(format.format(new Date()));
        samlAuthnStatementType.setAuthInstant("2009-04-16T13:15:39Z");
        samlAuthnStatementType.setAuthContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:X509");
        samlAuthnStatementType.setSessionIndex("987");
        samlAuthnStatementType.setSubjectLocalityAddress("158.147.185.168");
        samlAuthnStatementType.setSubjectLocalityDNSName("cs.myharris.net");

        SamlAuthzDecisionStatementType samlAuthzDecisionStatement = new SamlAuthzDecisionStatementType();
        assertOut.setSamlAuthzDecisionStatement(samlAuthzDecisionStatement);
        samlAuthzDecisionStatement.setAction("TestSaml");
        samlAuthzDecisionStatement.setDecision("Permit");
        samlAuthzDecisionStatement.setResource("https://158.147.185.168:8181/SamlReceiveService/SamlProcessWS");

        SamlAuthzDecisionStatementEvidenceType samlAuthzDecisionStatementEvidenceType =  new SamlAuthzDecisionStatementEvidenceType();
        samlAuthzDecisionStatement.setEvidence(samlAuthzDecisionStatementEvidenceType);
        SamlAuthzDecisionStatementEvidenceAssertionType samlAuthzDecisionStatementEvidenceAssertionType = new SamlAuthzDecisionStatementEvidenceAssertionType();
        samlAuthzDecisionStatementEvidenceType.setAssertion(samlAuthzDecisionStatementEvidenceAssertionType);
        samlAuthzDecisionStatementEvidenceAssertionType.setId("40df7c0a-ff3e-4b26-baeb-f2910f6d05a9");
        samlAuthzDecisionStatementEvidenceAssertionType.setIssueInstant("2009-04-16T13:10:39.093Z");
        samlAuthzDecisionStatementEvidenceAssertionType.setVersion("2.0");
        samlAuthzDecisionStatementEvidenceAssertionType.setIssuerFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName");
        samlAuthzDecisionStatementEvidenceAssertionType.setIssuer("CN=SAML User,OU=Harris,O=HITS,L=Melbourne,ST=FL,C=US");
        samlAuthzDecisionStatementEvidenceAssertionType.getAccessConsentPolicy().add("Claim-Ref-1234");
        samlAuthzDecisionStatementEvidenceAssertionType.getInstanceAccessConsentPolicy().add("Claim-Instance-1");

        SamlAuthzDecisionStatementEvidenceConditionsType samlAuthzDecisionStatementEvidenceConditions = new SamlAuthzDecisionStatementEvidenceConditionsType();
        samlAuthzDecisionStatementEvidenceAssertionType.setConditions(samlAuthzDecisionStatementEvidenceConditions);
        samlAuthzDecisionStatementEvidenceConditions.setNotBefore("2009-04-16T13:10:39.093Z");
        samlAuthzDecisionStatementEvidenceConditions.setNotOnOrAfter("2009-12-31T12:00:00.000Z");

        return assertOut;
    }

    public static NhinTargetCommunitiesType createNhinTargetCommunitiesType(String assigningAuthorityId) {
        NhinTargetCommunitiesType result = new NhinTargetCommunitiesType();
        result.getNhinTargetCommunity().add(createNhinTargetCommunityType(assigningAuthorityId));
        return result;
    }

    private static NhinTargetCommunityType createNhinTargetCommunityType(String assigningAuthorityId) {
        NhinTargetCommunityType result = new NhinTargetCommunityType();
        result.setHomeCommunity(createHomeCommunity(assigningAuthorityId));
        return result;
    }

    private static HomeCommunityType createHomeCommunity(String assigningAuthorityId) {
        HomeCommunityType result = new HomeCommunityType();
        result.setHomeCommunityId(assigningAuthorityId);
        result.setDescription("InternalTest2");
        result.setName("InternalTest2");
        return result;
    }
}
