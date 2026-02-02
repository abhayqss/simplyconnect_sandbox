package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Created by pzhurba on 02-Nov-15.
 */
public enum CareTeamRoleCode {
    /**
     * Case Manager is the person with administrative responsibility for a particular case.
     */
    ROLE_CASE_MANAGER("ROLE_CASE_MANAGER"),
    /**
     * Care Coordinator is the person in an organisation who is responsible for ensuring that a patient gets needed health and social services.
     */
    ROLE_CARE_COORDINATOR("ROLE_CARE_COORDINATOR"),
    /**
     * A person lawfully invested with the power, and charged with the obligation, of taking care of and managing the property and rights of a patient.
     */
    ROLE_PARENT_GUARDIAN("ROLE_PARENT_GUARDIAN"),
    /**
     * In terms of the system person receiving services is the patient.
     */
    ROLE_PERSON_RECEIVING_SERVICES("ROLE_PERSON_RECEIVING_SERVICES"),
    /**
     * Primary physician is a mainstream physician who provides care to a patient at the time of first (non-emergency) contact, which usually occurs on an outpatient basis.
     */
    ROLE_PRIMARY_PHYSICIAN("ROLE_PRIMARY_PHYSICIAN"),
    /**
     * Behavioral Health is a person who is treating patinet's behavioral health issues (stress, depression, anxiety, relationship problems, grief, addiction, ADHD or learning disabilities, mood disorders, or other psychological concerns).
     */
    ROLE_BEHAVIORAL_HEALTH("ROLE_BEHAVIORAL_HEALTH"),
    /**
     * Community Member is a member of community team. Community Member is not able to manage community team members.
     */
    ROLE_COMMUNITY_MEMBERS("ROLE_COMMUNITY_MEMBERS"),
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
     *
     */
    ROLE_TELE_HEALTH_NURSE("ROLE_TELE_HEALTH_NURSE"),

    ROLE_EXTERNAL_USER("ROLE_EXTERNAL_USER"),

    /**
     * The Community Administrator role is administrative role, that allows user to view and manage data on the community level.
     */
    ROLE_COMMUNITY_ADMINISTRATOR("ROLE_COMMUNITY_ADMINISTRATOR"),
    /**
     * The Administrator role is administrative role, that allows user to view and manage data on the organization level.
     */
    ROLE_ADMINISTRATOR("ROLE_ADMINISTRATOR"),

    /**
     * The Super Administrator role is administrative role, that allows user to create and edit delegated administration roles, view and manage data for all organizations.
     */
    ROLE_SUPER_ADMINISTRATOR("ROLE_SUPER_ADMINISTRATOR"),

    ROLE_NOTIFY_USER("ROLE_NOTIFY_USER"),

    ROLE_HCA("ROLE_HCA"),

    ROLE_CONTENT_CREATOR("ROLE_CONTENT_CREATOR"),

    ROLE_MARKETER("ROLE_MARKETER");

    public static final String CASE_MANAGER = "ROLE_CASE_MANAGER";
    public static final String CARE_COORDINATOR = "ROLE_CARE_COORDINATOR";
    public static final String PARENT_GUARDIAN = "ROLE_PARENT_GUARDIAN";
    public static final String PERSON_RECEIVING_SERVICES = "ROLE_PERSON_RECEIVING_SERVICES";
    public static final String PRIMARY_PHYSICIAN = "ROLE_PRIMARY_PHYSICIAN";
    public static final String BEHAVIORAL_HEALTH = "ROLE_BEHAVIORAL_HEALTH";
    public static final String COMMUNITY_MEMBERS = "ROLE_COMMUNITY_MEMBERS";
    public static final String SERVICE_PROVIDER = "ROLE_SERVICE_PROVIDER";
    public static final String PHARMACIST = "ROLE_PHARMACIST";
    public static final String NURSE = "ROLE_NURSE";
    public static final String TELE_HEALTH_NURSE = "ROLE_TELE_HEALTH_NURSE";
    /**
     * The Administrator role is administrative role, that allows user to view and manage data on the organization level.
     */
    public static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    /**
     * The Super Administrator role is administrative role, that allows user to create and edit delegated administration roles, view and manage data for all organizations.
     */
    public static final String SUPER_ADMINISTRATOR = "ROLE_SUPER_ADMINISTRATOR";
    /**
     * The Community Administrator role is administrative role, that allows user to view and manage data on the community level.
     */
    public static final String COMMUNITY_ADMINISTRATOR = "ROLE_COMMUNITY_ADMINISTRATOR";
    //synthetic role for case when new user linking account with existing
    public static final String LINKING_NEW_ACCOUNT = "LINKING_NEW_ACCOUNT";

    public static final String allRoles = "'ROLE_CASE_MANAGER', 'ROLE_CARE_COORDINATOR', 'ROLE_PARENT_GUARDIAN' " +
            ", 'ROLE_PERSON_RECEIVING_SERVICES', 'ROLE_PRIMARY_PHYSICIAN', 'ROLE_BEHAVIORAL_HEALTH', 'ROLE_COMMUNITY_MEMBERS', 'ROLE_SERVICE_PROVIDER'" +
            ", 'ROLE_PHARMACIST', 'ROLE_NURSE', 'ROLE_TELE_HEALTH_NURSE', 'ROLE_ADMINISTRATOR', 'ROLE_SUPER_ADMINISTRATOR', 'ROLE_COMMUNITY_ADMINISTRATOR'";

    public static final String IS_CAN_ADD_AFF_PATIENT_CARE_TEAM_MEMBER = "hasAnyRole(" + "'" + CASE_MANAGER + "','" + CARE_COORDINATOR + "','" + SERVICE_PROVIDER + "','" + PHARMACIST + "','" + NURSE + "','" + ADMINISTRATOR + "','" + SUPER_ADMINISTRATOR + "','" + COMMUNITY_ADMINISTRATOR + "')";
    public static final String IS_CAN_SEE_COMMUNITIES = "hasAnyRole(" + "'" + CASE_MANAGER + "'," + "'" + CARE_COORDINATOR + "'," + "'" + PRIMARY_PHYSICIAN + "'," + "'" + BEHAVIORAL_HEALTH + "'," + "'" + COMMUNITY_MEMBERS + "','" + ADMINISTRATOR + "','" + SUPER_ADMINISTRATOR + "','" + COMMUNITY_ADMINISTRATOR + "')";

    public static final String IS_SUPER_ADMINISTRATOR = "hasAnyRole('" + SUPER_ADMINISTRATOR + "')";
    public static final String IS_ADMINISTRATOR = "hasAnyRole('" + ADMINISTRATOR + "')";
    public static final String IS_NOT_COMMUNITY_ADMINISTRATOR = "!hasAnyRole('" + COMMUNITY_ADMINISTRATOR + "')";

    public static final String IS_CAN_ADD_NEW_PATIENTS = "hasAnyRole(" + "'" + CASE_MANAGER + "'," + "'" + CARE_COORDINATOR + "'," + "'" + PARENT_GUARDIAN + "','" + COMMUNITY_ADMINISTRATOR + "','" + ADMINISTRATOR + "','" + SUPER_ADMINISTRATOR + "')";
    public static final String IS_CAN_ACTIVATE_PATIENTS = "hasAnyRole(" + "'" + CASE_MANAGER + "'," + "'" + CARE_COORDINATOR + "'," + "'" + ADMINISTRATOR + "','" + SUPER_ADMINISTRATOR + "','" + COMMUNITY_ADMINISTRATOR + "')";

    /*public static final String IS_CAN_ADD_COMMUNITY_CARE_TEAM_MEMBERS = "hasAnyRole(" + "'" + CASE_MANAGER + "'," + "'" + CARE_COORDINATOR + "','" + ADMINISTRATOR + "','"+SUPER_ADMINISTRATOR+"')";*/



    /*
    ----
     */

    public static String[] ROLES_ADMINISTRATORS = {SUPER_ADMINISTRATOR, ADMINISTRATOR};
    public static String[] ROLES_ALL_ADMINISTRATORS = {SUPER_ADMINISTRATOR, ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};
    public static String[] ROLES_CAN_VIEW_ALL_PATIENTS = {SUPER_ADMINISTRATOR, ADMINISTRATOR};
    public static String[] ROLES_CAN_VIEW_ALL_EVENTS = {SUPER_ADMINISTRATOR, ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};

    public static String[] ROLES_CAN_VIEW_ALL_COMMUNITIES = {SUPER_ADMINISTRATOR, ADMINISTRATOR};
    public static String[] ROLES_CAN_VIEW_OWN_COMMUNITIES = {SUPER_ADMINISTRATOR, ADMINISTRATOR, COMMUNITY_ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR, PRIMARY_PHYSICIAN, BEHAVIORAL_HEALTH, COMMUNITY_MEMBERS};
    public static String[] ROLES_CAN_ADD_EDIT_COMMUNITIES = {SUPER_ADMINISTRATOR, ADMINISTRATOR};
    public static String[] ROLES_CAN_ADD_EDIT_ALL_COMMUNITY_CARE_TEAM_MEMBERS = {SUPER_ADMINISTRATOR, ADMINISTRATOR};
    public static String[] ROLES_CAN_ADD_EDIT_COMMUNITY_CARE_TEAM_MEMBERS = {SUPER_ADMINISTRATOR, ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR};
    public static String[] ROLES_CAN_ADD_EDIT_AFF_COMMUNITY_CARE_TEAM_MEMBERS = {SUPER_ADMINISTRATOR, ADMINISTRATOR, COMMUNITY_ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR};
    public static String[] ROLES_CAN_EDIT_SELF_CARE_TEAM_MEMBERS = {SUPER_ADMINISTRATOR, ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR, PRIMARY_PHYSICIAN, BEHAVIORAL_HEALTH};
    public static String[] ROLES_CAN_EDIT_SELF_ONLY_COMMUNITY_CARE_TEAM_MEMBERS = {PRIMARY_PHYSICIAN, BEHAVIORAL_HEALTH};
    public static String[] ROLES_CAN_VIEW_CONTACTS = {CASE_MANAGER, CARE_COORDINATOR, COMMUNITY_MEMBERS, SERVICE_PROVIDER, NURSE, TELE_HEALTH_NURSE, PHARMACIST, ADMINISTRATOR, SUPER_ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};
    public static String[] ROLES_CAN_ADD_EDIT_ALL_CONTACTS = {ADMINISTRATOR, SUPER_ADMINISTRATOR};
    public static String[] ROLES_CAN_EDIT_COMMUNITY_CONTACTS = {CASE_MANAGER, CARE_COORDINATOR, COMMUNITY_MEMBERS, SERVICE_PROVIDER, NURSE, TELE_HEALTH_NURSE,  PHARMACIST, ADMINISTRATOR, SUPER_ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};

    //    public static String[] ROLES_CAN_ADD_EDIT_ALL_PATIENT_CARE_TEAM_MEMBER = {CASE_MANAGER, CARE_COORDINATOR, SERVICE_PROVIDER, NURSE, PHARMACIST, ADMINISTRATOR, SUPER_ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};
    public static String[] ROLES_CAN_ADD_EDIT_PATIENT_CARE_TEAM_MEMBER = {CASE_MANAGER, CARE_COORDINATOR, SERVICE_PROVIDER, NURSE, TELE_HEALTH_NURSE, PHARMACIST, ADMINISTRATOR, SUPER_ADMINISTRATOR, PARENT_GUARDIAN, PRIMARY_PHYSICIAN};
    public static String[] ROLES_CAN_ADD_EDIT_AFF_PATIENT_CARE_TEAM_MEMBERS = {SUPER_ADMINISTRATOR, ADMINISTRATOR, COMMUNITY_ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR, SERVICE_PROVIDER, NURSE, PHARMACIST};
    public static String[] ROLES_CAN_EDIT_SELF_PATIENT_CARE_TEAM_MEMBER = {CASE_MANAGER, CARE_COORDINATOR, PARENT_GUARDIAN, PRIMARY_PHYSICIAN, PERSON_RECEIVING_SERVICES, SERVICE_PROVIDER, NURSE, PHARMACIST, COMMUNITY_ADMINISTRATOR, ADMINISTRATOR, SUPER_ADMINISTRATOR};
    public static String[] ROLES_CAN_EDIT_SELF_ONLY_PATIENT_CARE_TEAM_MEMBER = {PERSON_RECEIVING_SERVICES};
    public static String[] ROLES_CAN_NOT_EDIT_PATIENT_CARE_TEAM_MEMBER_ROLES = {PARENT_GUARDIAN, PRIMARY_PHYSICIAN};
    public static String[] ROLES_CAN_EDIT_ORGANIZATION_PATIENT_CARE_TEAM_MEMBER = {CASE_MANAGER, CARE_COORDINATOR, SERVICE_PROVIDER, NURSE, TELE_HEALTH_NURSE, PHARMACIST, PARENT_GUARDIAN, PRIMARY_PHYSICIAN};

    public static String[] ROLES_CAN_ADD_PATIENT = {COMMUNITY_ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR, PARENT_GUARDIAN, ADMINISTRATOR, SUPER_ADMINISTRATOR};
    public static String[] ROLES_CAN_EDIT_PATIENTS = {CASE_MANAGER, CARE_COORDINATOR, ADMINISTRATOR, SUPER_ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};

    public static String[] ROLES_CAN_VIEW_REPORTS = {ADMINISTRATOR, SUPER_ADMINISTRATOR, COMMUNITY_ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR, PRIMARY_PHYSICIAN, BEHAVIORAL_HEALTH, SERVICE_PROVIDER, NURSE, TELE_HEALTH_NURSE, PHARMACIST, COMMUNITY_MEMBERS};
    public static String[] ROLES_CAN_VIEW_ALL_PRIMARY_PATIENTS_EVENTS = {ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};

    public static String[] ROLES_CAN_ADD_EDIT_ASSESSMENTS_RESULTS = {ADMINISTRATOR, SUPER_ADMINISTRATOR, COMMUNITY_ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR, PRIMARY_PHYSICIAN, BEHAVIORAL_HEALTH};

    public static String IS_CAN_VIEW_ALL_COMMUNITIES = SecurityUtils.hasAnyRoleExpression(ROLES_CAN_VIEW_ALL_COMMUNITIES);
    public static String IS_CAN_VIEW_OWN_COMMUNITIES = SecurityUtils.hasAnyRoleExpression(ROLES_CAN_VIEW_OWN_COMMUNITIES);
    public static String IS_CAN_ADD_COMMUNITIES = SecurityUtils.hasAnyRoleExpression(ROLES_CAN_ADD_EDIT_COMMUNITIES);
    public static String IS_CAN_EDIT_COMMUNITIES = SecurityUtils.hasAnyRoleExpression(SUPER_ADMINISTRATOR, ADMINISTRATOR);
    public static String IS_CAN_ADD_EDIT_AFF_COMMUNITY_CARE_TEAM_MEMBERS = SecurityUtils.hasAnyRoleExpression(ROLES_CAN_ADD_EDIT_AFF_COMMUNITY_CARE_TEAM_MEMBERS);
    public static String IS_CAN_EDIT_SELF_CARE_TEAM_MEMBERS = SecurityUtils.hasAnyRoleExpression(ROLES_CAN_EDIT_SELF_CARE_TEAM_MEMBERS);
    public static String IS_CAN_EDIT_ORGANIZATIONS = SecurityUtils.hasAnyRoleExpression(SUPER_ADMINISTRATOR, ADMINISTRATOR);

    public static String IS_CAN_ADD_EDIT_ASSESSMENT_RESULTS = SecurityUtils.hasAnyRoleExpression(ROLES_CAN_ADD_EDIT_ASSESSMENTS_RESULTS);

    public static String[] ROLES_CAN_EDIT_SECURE_MESSAGING = {SUPER_ADMINISTRATOR, ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};

    public static String[] ROLES_CAN_VIEW_CCD_SECTION = {CASE_MANAGER, CARE_COORDINATOR, PRIMARY_PHYSICIAN, BEHAVIORAL_HEALTH, SERVICE_PROVIDER, NURSE, TELE_HEALTH_NURSE, PHARMACIST, SUPER_ADMINISTRATOR, ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};

    public static String[] ROLES_CAN_ADD_VIEW_SERVICE_PLANS = {ADMINISTRATOR, SUPER_ADMINISTRATOR, COMMUNITY_ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR, PRIMARY_PHYSICIAN};
    public static String[] ROLES_CAN_EDIT_SERVICE_PLANS = {ADMINISTRATOR, SUPER_ADMINISTRATOR};
    public static String[] ROLES_CAN_EDIT_SELF_SERVICE_PLANS = {COMMUNITY_ADMINISTRATOR, CASE_MANAGER, CARE_COORDINATOR, PRIMARY_PHYSICIAN};
    public static String IS_CAN_ADD_VIEW_SERVICE_PLANS = SecurityUtils.hasAnyRoleExpression(ROLES_CAN_ADD_VIEW_SERVICE_PLANS);


    public static String[] ALL_ROLES = {CASE_MANAGER, CARE_COORDINATOR, PARENT_GUARDIAN, PERSON_RECEIVING_SERVICES, PRIMARY_PHYSICIAN, BEHAVIORAL_HEALTH, COMMUNITY_MEMBERS, SERVICE_PROVIDER, NURSE, TELE_HEALTH_NURSE, PHARMACIST, SUPER_ADMINISTRATOR, ADMINISTRATOR, COMMUNITY_ADMINISTRATOR};

    /*
    ----
     */


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


    public static Set<CareTeamRoleCode> getRoleListAbleForEditing(CareTeamRoleCode role) {
        switch (role) {
            case ROLE_CASE_MANAGER:
            case ROLE_CARE_COORDINATOR: {
                return EnumSet.of(
                        ROLE_CASE_MANAGER
                        , ROLE_CARE_COORDINATOR
                        , ROLE_PARENT_GUARDIAN
                        , ROLE_PERSON_RECEIVING_SERVICES
                        , ROLE_PRIMARY_PHYSICIAN
                        , ROLE_BEHAVIORAL_HEALTH
                        , ROLE_PHARMACIST
                        , ROLE_NURSE
                        , ROLE_TELE_HEALTH_NURSE
                        , ROLE_COMMUNITY_MEMBERS
                        , ROLE_SERVICE_PROVIDER
                );
            }
            case ROLE_PARENT_GUARDIAN:
            case ROLE_PERSON_RECEIVING_SERVICES: {
                return EnumSet.of(
                        ROLE_PARENT_GUARDIAN
                        , ROLE_PERSON_RECEIVING_SERVICES
                );
            }
            case ROLE_PRIMARY_PHYSICIAN:
                return EnumSet.of(
                        ROLE_PRIMARY_PHYSICIAN
                        , ROLE_BEHAVIORAL_HEALTH
                        , ROLE_PHARMACIST
                );
            case ROLE_BEHAVIORAL_HEALTH: {
                return EnumSet.of(
                        ROLE_PRIMARY_PHYSICIAN
                        , ROLE_BEHAVIORAL_HEALTH
                );
            }

            case ROLE_COMMUNITY_MEMBERS: {
                return EnumSet.of(ROLE_COMMUNITY_MEMBERS);
            }
            case ROLE_SERVICE_PROVIDER: {
                return EnumSet.of(
                        ROLE_PARENT_GUARDIAN
                        , ROLE_PERSON_RECEIVING_SERVICES
                        , ROLE_PRIMARY_PHYSICIAN
                        , ROLE_BEHAVIORAL_HEALTH
                        , ROLE_PHARMACIST
                        , ROLE_SERVICE_PROVIDER
                );
            }
            case ROLE_PHARMACIST:
                return EnumSet.of(
                        ROLE_PHARMACIST
                );
            case ROLE_NURSE:
                return EnumSet.of(
                        ROLE_NURSE
                );
                case ROLE_TELE_HEALTH_NURSE:
                return EnumSet.of(
                        ROLE_TELE_HEALTH_NURSE
                );
            case ROLE_COMMUNITY_ADMINISTRATOR:
                return EnumSet.of(
                        ROLE_CASE_MANAGER
                        , ROLE_CARE_COORDINATOR
                        , ROLE_PARENT_GUARDIAN
                        , ROLE_PERSON_RECEIVING_SERVICES
                        , ROLE_PRIMARY_PHYSICIAN
                        , ROLE_BEHAVIORAL_HEALTH
                        , ROLE_PHARMACIST
                        , ROLE_NURSE
                        , ROLE_TELE_HEALTH_NURSE
                        , ROLE_COMMUNITY_MEMBERS
                        , ROLE_SERVICE_PROVIDER
                        , ROLE_COMMUNITY_ADMINISTRATOR
                );
            case ROLE_ADMINISTRATOR:
                return EnumSet.of(
                        ROLE_CASE_MANAGER
                        , ROLE_CARE_COORDINATOR
                        , ROLE_PARENT_GUARDIAN
                        , ROLE_PERSON_RECEIVING_SERVICES
                        , ROLE_PRIMARY_PHYSICIAN
                        , ROLE_BEHAVIORAL_HEALTH
                        , ROLE_PHARMACIST
                        , ROLE_NURSE
                        , ROLE_TELE_HEALTH_NURSE
                        , ROLE_COMMUNITY_MEMBERS
                        , ROLE_SERVICE_PROVIDER
                        , ROLE_COMMUNITY_ADMINISTRATOR
                        , ROLE_ADMINISTRATOR
                );
            case ROLE_SUPER_ADMINISTRATOR:
                return EnumSet.of(
                        ROLE_CASE_MANAGER
                        , ROLE_CARE_COORDINATOR
                        , ROLE_PARENT_GUARDIAN
                        , ROLE_PERSON_RECEIVING_SERVICES
                        , ROLE_PRIMARY_PHYSICIAN
                        , ROLE_BEHAVIORAL_HEALTH
                        , ROLE_PHARMACIST
                        , ROLE_NURSE
                        , ROLE_TELE_HEALTH_NURSE
                        , ROLE_COMMUNITY_MEMBERS
                        , ROLE_SERVICE_PROVIDER
                        , ROLE_COMMUNITY_ADMINISTRATOR
                        , ROLE_ADMINISTRATOR
                        , ROLE_SUPER_ADMINISTRATOR
                );
            default:
                return Collections.emptySet();
        }
    }

    public static Set<CareTeamRoleCode> getRoleListAbleForEditing(Iterable<CareTeamRoleCode> roles) {
        final Set<CareTeamRoleCode> result = EnumSet.noneOf(CareTeamRoleCode.class);
        for (CareTeamRoleCode role : roles) {
            result.addAll(getRoleListAbleForEditing(role));
        }
        return result;
    }

    private static boolean isRoleAbleForEditing(final CareTeamRoleCode loggedRole, final CareTeamRoleCode roleToAdd) {
        return (getRoleListAbleForEditing(loggedRole).contains(roleToAdd));
    }

    public static boolean isRoleAbleForEditing(final Set<CareTeamRoleCode> loggedRoles, final CareTeamRoleCode roleToAdd) {
        boolean result = false;
        if (CollectionUtils.isEmpty(loggedRoles)) return false;
        for (CareTeamRoleCode loggedRole : loggedRoles) {
            result = result || isRoleAbleForEditing(loggedRole, roleToAdd);
        }
        return result;
    }

    public static boolean isRoleAbleForCreating(final Set<CareTeamRoleCode> loggedRoles) {
        boolean result = false;
        if (CollectionUtils.isEmpty(loggedRoles)) return false;
        for (CareTeamRoleCode loggedRole : loggedRoles) {
            result = result || CollectionUtils.isNotEmpty(getRoleListAbleForEditing(loggedRole));
        }
        return result;
    }

}
