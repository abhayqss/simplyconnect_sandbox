package com.scnsoft.eldermark.service.document.cda.schema;

import java.util.List;

import com.scnsoft.eldermark.entity.Authenticator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.FunctionalStatus;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.ccd.AdvanceDirective;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.Custodian;
import com.scnsoft.eldermark.entity.document.ccd.DataEnterer;
import com.scnsoft.eldermark.entity.document.ccd.DocumentationOf;
import com.scnsoft.eldermark.entity.document.ccd.Encounter;
import com.scnsoft.eldermark.entity.document.ccd.FamilyHistory;
import com.scnsoft.eldermark.entity.document.ccd.Immunization;
import com.scnsoft.eldermark.entity.document.ccd.Informant;
import com.scnsoft.eldermark.entity.document.ccd.InformationRecipient;
import com.scnsoft.eldermark.entity.document.ccd.LegalAuthenticator;
import com.scnsoft.eldermark.entity.document.ccd.MedicalEquipment;
import com.scnsoft.eldermark.entity.document.ccd.Participant;
import com.scnsoft.eldermark.entity.document.ccd.Payer;
import com.scnsoft.eldermark.entity.document.ccd.PlanOfCare;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.document.ccd.Procedure;
import com.scnsoft.eldermark.entity.document.ccd.Result;
import com.scnsoft.eldermark.entity.document.ccd.SocialHistory;
import com.scnsoft.eldermark.entity.document.ccd.VitalSign;
import com.scnsoft.eldermark.entity.medication.Medication;

/**
 * A holder for Clinical Document content.
 * <br/><br/>
 * This is a new abstraction that gives a few advantages:
 * <ol>
 * 		<li>It allows to separate a process of document parsing from a process of document storing in database.</li>
 * 		<li>It allows to separate a process of document construction from a process of fetching resident-related data from database.</li>
 *      <li><ul>
 * 			<li>Consequence A. It allows to easily include / exclude particular sections from a generated clinical document.</li>
 * 			<li>Consequence B. It allows to easily merge data from multiple resident records (useful in aggregated documents construction).</li>
 * 		</ul></li>
 * 		<li>It represents health data in a way very similar to Continuity of Care Document and still is independent from any specific document type.</li>
 *      <ul>
 * 			<li>Consequence A. It promotes encapsulation and other good architecture design principles.</li>
 * 		</ul>
 * 		<li>It's not a new entity; it's a holder for existing entities, so ClinicalDocumentVO does not require database changes and the contained data is easy to persist.</li>
 * </ol>
 * TODO replace List<> with Collection<> ?
 *
 * @see CcdHeaderDetails
 * @author phomal
 * Created on 4/13/2018.
 */
public class ClinicalDocumentVO {

    private Client recordTarget;

    // header sections
    private List<Authenticator> authenticators;
    private List<Author> authors;
    private List<BasicEntity> authorizations;
    private BasicEntity component;
    private Custodian custodian;
    private DataEnterer dataEnterer;
    private List<DocumentationOf> documentationOfs;
    private List<Informant> informants;
    private List<InformationRecipient> informationRecipients;
    private List<BasicEntity> inFulfillmentOfs;
    private LegalAuthenticator legalAuthenticator;
    private List<Participant> participants;

    // body sections
    private List<AdvanceDirective> advanceDirectives;
    private List<Allergy> allergies;
    private List<Encounter> encounters;
    private List<FamilyHistory> familyHistories;
    private List<FunctionalStatus> functionalStatuses;
    private List<Immunization> immunizations;
    private List<MedicalEquipment> medicalEquipments;
    private List<Medication> medications;
    private List<Payer> payers;
    private List<PlanOfCare> planOfCares;
    private List<Problem> problems;
    private List<Procedure> procedures;
    private List<Result> results;
    private List<SocialHistory> socialHistories;
    private List<VitalSign> vitalSigns;


    public Client getRecordTarget() {
        return recordTarget;
    }

    public void setRecordTarget(Client recordTarget) {
        this.recordTarget = recordTarget;
    }

    public List<Authenticator> getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(List<Authenticator> authenticators) {
        this.authenticators = authenticators;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<BasicEntity> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(List<BasicEntity> authorizations) {
        this.authorizations = authorizations;
    }

    public BasicEntity getComponent() {
        return component;
    }

    public void setComponent(BasicEntity component) {
        this.component = component;
    }

    public Custodian getCustodian() {
        return custodian;
    }

    public void setCustodian(Custodian custodian) {
        this.custodian = custodian;
    }

    public DataEnterer getDataEnterer() {
        return dataEnterer;
    }

    public void setDataEnterer(DataEnterer dataEnterer) {
        this.dataEnterer = dataEnterer;
    }

    public List<DocumentationOf> getDocumentationOfs() {
        return documentationOfs;
    }

    public void setDocumentationOfs(List<DocumentationOf> documentationOfs) {
        this.documentationOfs = documentationOfs;
    }

    public List<Informant> getInformants() {
        return informants;
    }

    public void setInformants(List<Informant> informants) {
        this.informants = informants;
    }

    public List<InformationRecipient> getInformationRecipients() {
        return informationRecipients;
    }

    public void setInformationRecipients(List<InformationRecipient> informationRecipients) {
        this.informationRecipients = informationRecipients;
    }

    public List<BasicEntity> getInFulfillmentOfs() {
        return inFulfillmentOfs;
    }

    public void setInFulfillmentOfs(List<BasicEntity> inFulfillmentOfs) {
        this.inFulfillmentOfs = inFulfillmentOfs;
    }

    public LegalAuthenticator getLegalAuthenticator() {
        return legalAuthenticator;
    }

    public void setLegalAuthenticator(LegalAuthenticator legalAuthenticator) {
        this.legalAuthenticator = legalAuthenticator;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<AdvanceDirective> getAdvanceDirectives() {
        return advanceDirectives;
    }

    public void setAdvanceDirectives(List<AdvanceDirective> advanceDirectives) {
        this.advanceDirectives = advanceDirectives;
    }

    public List<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<Allergy> allergies) {
        this.allergies = allergies;
    }

    public List<Encounter> getEncounters() {
        return encounters;
    }

    public void setEncounters(List<Encounter> encounters) {
        this.encounters = encounters;
    }

    public List<FamilyHistory> getFamilyHistories() {
        return familyHistories;
    }

    public void setFamilyHistories(List<FamilyHistory> familyHistories) {
        this.familyHistories = familyHistories;
    }

    public List<FunctionalStatus> getFunctionalStatuses() {
        return functionalStatuses;
    }

    public void setFunctionalStatuses(List<FunctionalStatus> functionalStatuses) {
        this.functionalStatuses = functionalStatuses;
    }

    public List<Immunization> getImmunizations() {
        return immunizations;
    }

    public void setImmunizations(List<Immunization> immunizations) {
        this.immunizations = immunizations;
    }

    public List<MedicalEquipment> getMedicalEquipments() {
        return medicalEquipments;
    }

    public void setMedicalEquipments(List<MedicalEquipment> medicalEquipments) {
        this.medicalEquipments = medicalEquipments;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    public List<Payer> getPayers() {
        return payers;
    }

    public void setPayers(List<Payer> payers) {
        this.payers = payers;
    }

    public List<PlanOfCare> getPlanOfCares() {
        return planOfCares;
    }

    public void setPlanOfCares(List<PlanOfCare> planOfCares) {
        this.planOfCares = planOfCares;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

    public List<Procedure> getProcedures() {
        return procedures;
    }

    public void setProcedures(List<Procedure> procedures) {
        this.procedures = procedures;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public List<SocialHistory> getSocialHistories() {
        return socialHistories;
    }

    public void setSocialHistories(List<SocialHistory> socialHistories) {
        this.socialHistories = socialHistories;
    }

    public List<VitalSign> getVitalSigns() {
        return vitalSigns;
    }

    public void setVitalSigns(List<VitalSign> vitalSigns) {
        this.vitalSigns = vitalSigns;
    }
}
