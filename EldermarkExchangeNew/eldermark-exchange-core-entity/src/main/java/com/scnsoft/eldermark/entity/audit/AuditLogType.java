package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;

import java.util.Arrays;

public enum AuditLogType {
    ALLERGY,
    APPOINTMENT,
    ASSESSMENT,
    CARE_TEAM_MEMBER,
    CLIENT_PROFILE,
    CLIENT_CTM,
    COMMUNITY_CTM,
    COMMUNITY,
    COMPANY_DOCUMENT,
    CONTACT,
    DOCUMENT,
    DOCUMENT_FOLDER,
    EVENT,
    INCIDENT_REPORT,
    EXPENSE,
    LAB_RESEARCH_ORDER,
    MARKETPLACE,
    MEDICATION,
    NOTE,
    ORGANIZATION,
    PROBLEM,
    REFERRAL,
    REFERRAL_REQUEST,
    SERVICE_PLAN,
    SIGNATURE_TEMPLATE,
    REPORT,
    CHAT,
    SIGNATURE_REQUEST,
    SIGNATURE_BULK_REQUEST,
    USER_MANUAL,
    RELEASE_NOTE,
    SUPPORT_TICKET,
    LOGIN_LOGOUT,
    TRANSPORTATION,
    RECORD_SEARCH,
    PROSPECT;

    public static AuditLogType getByAuditLogActivity(AuditLogActivity activity) {
        return Arrays.stream(AuditLogType.values())
                .filter(a -> a == activity.getActionWithParams().getAuditLogType())
                .findFirst()
                .orElse(null);
    }
}
