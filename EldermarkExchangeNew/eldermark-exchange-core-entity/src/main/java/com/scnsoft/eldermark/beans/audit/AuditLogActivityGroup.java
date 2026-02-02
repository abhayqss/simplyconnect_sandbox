package com.scnsoft.eldermark.beans.audit;

public enum AuditLogActivityGroup {
    LOG_IN_OUT("Log in / Log out"),
    CLIENT_PROFILE("Client profile"),
    TRANSPORTATION("Transportation"),
    SERVICE_PLAN("Service Plan"),
    ASSESSMENT("Assessments"),
    DOCUMENT("Documents"),
    EVENT_AND_NOTE("Events&Notes"),
    CLIENT_CARE_TEAM("Client care team"),
    INCIDENT_REPORT("Incident reports"),
    LAB("Labs"),
    CONTACT("Contacts"),
    ORGANIZATION("Organizations"),
    COMMUNITY("Communities"),
    COMMUNITY_CARE_TEAM("Community care team"),
    REFERRAL("Referrals"),
    EXPENSE("Expenses"),
    COMPANY_DOCUMENT("Company documents"),
    MARKETPLACE("Marketplace"),
    ESIGN_BUILDER("E-sign template builder"),
    APPOINTMENT("Appointments"),
    REPORT("Reports"),
    CHAT_CALL("Chats & Calls"),
    SIGNATURE_REQUEST("Signature requests"),
    HELP("Help"),
    RECORD_SEARCH("Record search"),
    PROSPECT("Prospects");

    private final String displayName;

    AuditLogActivityGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
