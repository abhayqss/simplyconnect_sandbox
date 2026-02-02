package com.scnsoft.eldermark.entity;

import java.util.Collection;

// TODO Refactoring: move CcdHeaderDetails (DTO) to somewhere else
public class CcdHeaderDetails {

    private Collection<Author> authors;
    private DataEnterer dataEnterer;
    private Collection<Informant> informants;
    private Custodian custodian;
    private Collection<InformationRecipient> recipients;
    private LegalAuthenticator legalAuthenticator;
    private Collection<Authenticator> authenticators;
    private Collection<Participant> participants;
    private Collection<DocumentationOf> documentationOfs;
    private Collection inFullfillmentOfs;
    private Collection authorizations;
    private Object component;

    public Collection<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<Author> authors) {
        this.authors = authors;
    }

    public DataEnterer getDataEnterer() {
        return dataEnterer;
    }

    public void setDataEnterer(DataEnterer dataEnterer) {
        this.dataEnterer = dataEnterer;
    }

    public Collection<Informant> getInformants() {
        return informants;
    }

    public void setInformants(Collection<Informant> informants) {
        this.informants = informants;
    }

    public Custodian getCustodian() {
        return custodian;
    }

    public void setCustodian(Custodian custodian) {
        this.custodian = custodian;
    }

    public Collection<InformationRecipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(Collection<InformationRecipient> recipients) {
        this.recipients = recipients;
    }

    public LegalAuthenticator getLegalAuthenticator() {
        return legalAuthenticator;
    }

    public void setLegalAuthenticator(LegalAuthenticator legalAuthenticator) {
        this.legalAuthenticator = legalAuthenticator;
    }

    public Collection<Authenticator> getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(Collection<Authenticator> authenticators) {
        this.authenticators = authenticators;
    }

    public Collection<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Collection<Participant> participants) {
        this.participants = participants;
    }

    public Collection<DocumentationOf> getDocumentationOfs() {
        return documentationOfs;
    }

    public void setDocumentationOfs(Collection<DocumentationOf> documentationOfs) {
        this.documentationOfs = documentationOfs;
    }

    public Collection getInFullfillmentOfs() {
        return inFullfillmentOfs;
    }

    public void setInFulfillmentOfs(Collection inFullfillmentOfs) {
        this.inFullfillmentOfs = inFullfillmentOfs;
    }

    public void setInFullfillmentOfs(Collection inFullfillmentOfs) {
        this.inFullfillmentOfs = inFullfillmentOfs;
    }

    public Collection getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(Collection authorizations) {
        this.authorizations = authorizations;
    }

    public Object getComponent() {
        return component;
    }

    public void setComponent(Object component) {
        this.component = component;
    }
}
