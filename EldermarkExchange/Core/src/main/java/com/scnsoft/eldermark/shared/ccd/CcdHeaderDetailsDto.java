package com.scnsoft.eldermark.shared.ccd;

import java.util.List;

public class CcdHeaderDetailsDto {

    private List<AuthorDto> authors;
    private DataEntererDto dataEnterer;
    private List<InformantDto> informants;
    private CustodianDto custodian;
    private List<InformationRecipientDto> informationRecipients;
    private LegalAuthenticatorDto legalAuthenticator;
    private List<AuthenticatorDto> authenticators;
    private List<ParticipantDto> participants;
    private List<DocumentationOfDto> documentationOfs;

    public List<AuthorDto> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorDto> authors) {
        this.authors = authors;
    }

    public DataEntererDto getDataEnterer() {
        return dataEnterer;
    }

    public void setDataEnterer(DataEntererDto dataEnterer) {
        this.dataEnterer = dataEnterer;
    }

    public List<InformantDto> getInformants() {
        return informants;
    }

    public void setInformants(List<InformantDto> informants) {
        this.informants = informants;
    }

    public CustodianDto getCustodian() {
        return custodian;
    }

    public void setCustodian(CustodianDto custodian) {
        this.custodian = custodian;
    }

    public List<InformationRecipientDto> getInformationRecipients() {
        return informationRecipients;
    }

    public void setInformationRecipients(List<InformationRecipientDto> informationRecipients) {
        this.informationRecipients = informationRecipients;
    }

    public LegalAuthenticatorDto getLegalAuthenticator() {
        return legalAuthenticator;
    }

    public void setLegalAuthenticator(LegalAuthenticatorDto legalAuthenticator) {
        this.legalAuthenticator = legalAuthenticator;
    }

    public List<AuthenticatorDto> getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(List<AuthenticatorDto> authenticators) {
        this.authenticators = authenticators;
    }

    public List<ParticipantDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDto> participants) {
        this.participants = participants;
    }

    public List<DocumentationOfDto> getDocumentationOfs() {
        return documentationOfs;
    }

    public void setDocumentationOfs(List<DocumentationOfDto> documentationOfs) {
        this.documentationOfs = documentationOfs;
    }
}
