package com.scnsoft.eldermark.entity;

/**
 * Created by pzhurba on 02-Nov-15.
 */
public enum CareTeamRoleCode {
    /**
     * Care Coordinator is a person who is responsible for ensuring that a client gets needed health and social services.
     */
    ROLE_CARE_COORDINATOR("ROLE_CARE_COORDINATOR"),

    /**
     * Case Manager is the person with administrative responsibility for a particular case.
     */
    ROLE_CASE_MANAGER("ROLE_CASE_MANAGER"),

    /**
     *  this is actually community provider
     */
    ROLE_COMMUNITY_MEMBERS("ROLE_COMMUNITY_MEMBERS"),

    /**
     * A person lawfully invested with the power, and charged with the obligation, of taking care of and managing the property and rights of a client.
     */
    ROLE_PARENT_GUARDIAN("ROLE_PARENT_GUARDIAN"),

    /**
     * In terms of the system it is a patient/client.
     */
    ROLE_PERSON_RECEIVING_SERVICES("ROLE_PERSON_RECEIVING_SERVICES"),

    /**
     * Primary physician is a mainstream physician who provides care to a client at the time of  first (non-emergency) contact,  which  usually  occurs on  an outpatient basis.
     */
    ROLE_PRIMARY_PHYSICIAN("ROLE_PRIMARY_PHYSICIAN"),

    /**
     * Behavioral Health is a person who is treating patient's behavioral health issues (stress, depression, anxiety, relationship problems, grief, addiction, ADHD or learning disabilities, mood disorders, or other psychological concerns).
     */
    ROLE_BEHAVIORAL_HEALTH("ROLE_BEHAVIORAL_HEALTH"),


    /**
     * A service provider is a representative from organization that provides services to individuals in return for payment, reimbursement.
     */
    ROLE_SERVICE_PROVIDER("ROLE_SERVICE_PROVIDER"),

    /**
     *
     */
    ROLE_PHARMACIST("ROLE_PHARMACIST"),

    /**
     *
     */
    ROLE_PHARMACY_TECHNICIAN("ROLE_PHARMACY_TECHNICIAN"),

    /**
     *
     */
    ROLE_NURSE("ROLE_NURSE"),

    /**
     * A Community Administrator role is administrative role that allows user to view and manage data at the community level.
     */
    ROLE_COMMUNITY_ADMINISTRATOR("ROLE_COMMUNITY_ADMINISTRATOR"),

    /**
     * A Administrator role is administrative role that allows user to view and manage data at the organization level.
     */
    ROLE_ADMINISTRATOR("ROLE_ADMINISTRATOR"),

    /**
     * The Super Administrator role is administrative role, that allows user to create and edit delegated administration roles, view and manage data for all organizations.
     */
    ROLE_SUPER_ADMINISTRATOR("ROLE_SUPER_ADMINISTRATOR"),

    ROLE_NOTIFY_USER("ROLE_NOTIFY_USER"),

    ROLE_EXTERNAL_USER("ROLE_EXTERNAL_USER"),

    ROLE_TELE_HEALTH_NURSE("ROLE_TELE_HEALTH_NURSE"),

    ROLE_HCA("ROLE_HCA"),

    ROLE_CONTENT_CREATOR("ROLE_CONTENT_CREATOR"),

    ROLE_MARKETER("ROLE_MARKETER");

    private String code;

    CareTeamRoleCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static CareTeamRoleCode getByCode(final String code) {
        for (CareTeamRoleCode c : CareTeamRoleCode.values()) {
            if (c.getCode().equals(code)) {
                return c;
            }
        }
        return null;
    }

}
