import BaseService from "./BaseService";

import { interpolate } from "../lib/utils/Utils";

class DirectoryService extends BaseService {
  findStates(params, options) {
    return super.request({
      url: "/directory/states",
      ...options,
      params,
    });
  }

  findGenders(params, options) {
    return super.request({
      url: "/directory/genders",
      ...options,
      params,
    });
  }

  findGendersNoStructure(params, options) {
    return super.request({
      url: "/directory/genders",
      ...options,
      params,
    });
  }

  findEthnicity(params, options) {
    return super.request({
      url: "/directory/Ethnicity",
      ...options,
      params,
    });
  }

  findMaritalStatus(params, options) {
    return super.request({
      url: "/directory/marital-status",
      params,
      ...options,
    });
  }

  findPrimaryFocuses(params) {
    return super.request({
      url: "/authorized-directory/primary-focuses",
      params,
    });
  }

  findAgeGroups(params) {
    return super.request({
      url: "/directory/accepted-age-groups",
      params,
    });
  }

  findCareLevels(params) {
    return super.request({
      url: "/directory/care-levels",
      params,
    });
  }

  findCommunityTypes(params) {
    return super.request({
      url: "/authorized-directory/community-types",
      params,
    });
  }

  findOrganizationTypes() {
    return super.request({
      url: "/directory/organization-type",
    });
  }

  findEmergencyServices() {
    return super.request({
      url: "/authorized-directory/emergency-services",
    });
  }

  findAdditionalServices() {
    return super.request({
      url: "/authorized-directory/additional-services",
    });
  }

  findLanguageServices() {
    return super.request({
      url: "/authorized-directory/language-services",
    });
  }

  findTreatmentServices(params) {
    return super.request({
      url: "/authorized-directory/services",
      params,
    });
  }

  findServices({ isAuthorizedAccess = true, ...params }) {
    return super.request({
      url: `/${isAuthorizedAccess ? "authorized-" : ""}directory/services`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findMarketplaceLanguages(params) {
    return super.request({
      url: "/authorized-directory/marketplace-languages",
      response: { extractDataOnly: true },
      params,
    });
  }

  findServiceCategories({ isAuthorizedAccess = true, ...params }) {
    return super.request({
      url: `/${isAuthorizedAccess ? "authorized-" : ""}directory/service-categories`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findInsuranceNetworks(params) {
    return super.request({
      url: "/authorized-directory/insurance/networks",
      params,
    });
  }

  findInsuranceNetworkAggregatedNames(params, options) {
    return super.request({
      url: "/authorized-directory/insurance/network-aggregated-names",
      ...options,
      params,
    });
  }

  findInsurancePaymentPlans(params) {
    return super.request({
      url: "/authorized-directory/insurance/payment-plans",
      params,
    });
  }

  findOrganizations(params, options) {
    return super.request({
      url: "/authorized-directory/organizations",
      ...options,
      params,
    });
  }

  findCommunities(params, options) {
    return super.request({
      url: "/authorized-directory/communities",
      ...options,
      params,
    });
  }

  findVendorOrganizations(vendorId) {
    return super.request({
      url: "/referrals/vendor/organizations",
      params: {
        vendorId,
      },
    });
  }

  findVendorCommunities(params) {
    return super.request({
      url: "/referrals/vendor/communities",
      params,
    });
  }

  findDomains() {
    return super.request({
      url: "/directory/service-plan-domains",
    });
  }

  findProgramTypes() {
    return super.request({
      url: "/directory/service-plan-program-types",
    });
  }

  findProgramSubTypes() {
    return super.request({
      url: "/directory/service-plan-program-subtypes",
    });
  }

  findPriorities() {
    return super.request({
      url: "/directory/service-plan-priorities",
    });
  }

  findServiceStatuses() {
    return super.request({
      url: "/authorized-directory/service-statuses",
    });
  }

  findServiceControlRequestStatuses() {
    return super.request({
      url: "/authorized-directory/service-control-request-statuses",
    });
  }

  findSystemRoles({ isEditable = false, ...params } = {}, options) {
    return super.request({
      url: isEditable ? "/authorized-directory/editable-system-roles" : "/directory/system-roles",
      ...options,
      params,
    });
  }

  findContactStatuses(options) {
    return super.request({
      url: "/directory/employee-statuses",
      ...options,
    });
  }

  findClientStatuses(options) {
    return super.request({
      url: "/directory/client-statuses",
      ...options,
    });
  }

  findClientPharmacyNames(params) {
    return super.request({
      params,
      response: { extractDataOnly: true },
      url: "/authorized-directory/client-pharmacy-names",
    });
  }

  findCareTeamRoles({ clientId, prospectId, contactId, ...rest }) {
    let url = "/authorized-directory/care-team/community-member-roles";
    if (clientId) url = "/authorized-directory/care-team/client-member-roles";
    if (prospectId) url = "/authorized-directory/care-team/prospect-member-roles";

    return super.request({
      url,
      params: { contactId },
      ...rest,
    });
  }

  findCareTeamChannels({ isProspect }) {
    let url = "/authorized-directory/care-team/notification-types";

    /**
     * @TODO waiting for the intergration
     */
    if (isProspect) {
      url = "/authorized-directory/care-team/notification-types";
    }

    return super.request({ url });
  }

  findCareTeamResponsibilities() {
    return super.request({
      url: "/authorized-directory/care-team/responsibilities",
    });
  }

  findCareTeamNotificationPreferences(params) {
    return super.request({
      url: "/authorized-directory/care-team/default-notification-preferences",
      params,
    });
  }

  //todo the correct url is /directory/note-subtypes
  findNoteTypes() {
    return super.request({
      url: "/directory/note-types",
    });
  }

  findNoteEncounterTypes() {
    return super.request({
      url: "/directory/encounter-note-types",
    });
  }

  //todo this is not directory controller
  findNoteAdmittanceDates(clientId) {
    return super.request({
      url: "/clients/" + clientId + "/notes/admit-dates",
    });
  }

  findMarketplaceCommunityLocations() {
    return super.request({
      url: "/directory/marketplace/community-locations",
    });
  }

  findAssessmentSurvey(params) {
    return super.request({
      url: "/authorized-directory/assessment-survey",
      params,
    });
  }

  findAssessmentTypes(params) {
    return super.request({
      url: `/authorized-directory/assessment-types`,
      params,
    });
  }

  getAssessmentManagement(params) {
    return super.request({
      url: "/authorized-directory/assessment-management",
      params,
    });
  }

  getAssessmentScore(params) {
    return super.request({
      url: "/authorized-directory/assessment-score",
      params,
    });
  }

  findReportGroups() {
    return super.request({
      url: "/directory/report-groups",
    });
  }

  findReportTypes(params) {
    return super.request({
      url: "/authorized-directory/report-types",
      params,
    });
  }
  // get events type
  findGroupedEventTypes(params, options) {
    if (!params?.isProspect)
      return super.request({
        url: "/authorized-directory/grouped-event-types",
        ...options,
        params,
      });

    /**
     * @TODO waiting for the integration
     */
    return super.request({
      url: "/authorized-directory/grouped-event-types",
    });
  }

  findClients(params, options) {
    return super.request({
      url: "/authorized-directory/clients",
      ...options,
      params,
    });
  }

  findWorkflowTemplate(params, options) {
    return super.request({
      url: "/workflowTemplate/find",
      ...options,
      params,
    });
  }

  findClientsWithBirthdays(params, options) {
    return super.request({
      url: "/authorized-directory/client-birthdays",
      ...options,
      response: { extractDataOnly: true },
      params,
    });
  }

  findReferralPriorities(params, options) {
    return super.request({
      url: "/directory/referral-request-priorities",
      ...options,
      params,
    });
  }

  findReferralStatuses(params) {
    return super.request({
      url: "/directory/referral-statuses",
      params,
    });
  }

  findReferralReasons(params) {
    return super.request({
      url: "/authorized-directory/referral-reasons",
      response: { extractDataOnly: true },
      params,
    });
  }

  findReferralIntents(params) {
    return super.request({
      url: "/directory/referral-request-intents",
      response: { extractDataOnly: true },
      params,
    });
  }

  findReferralCategories(params) {
    return super.request({
      url: "/directory/referral-categories-grouped",
      response: { extractDataOnly: true },
      params,
    });
  }

  findReferralNetworks(params) {
    return super.request({
      url: "/authorized-directory/partner-networks",
      params,
    });
  }

  findReferralDeclineReasons(params) {
    return super.request({
      url: "/authorized-directory/referral-decline-reasons",
      params,
    });
  }

  findTreatmentServicesInUse(params) {
    return super.request({
      url: "/authorized-directory/services-treatment-approaches-in-use",
      response: { extractDataOnly: true },
      params,
    });
  }

  findServiceInUse(params) {
    return super.request({
      url: "/authorized-directory/services-in-use",
      response: { extractDataOnly: true },
      params,
    });
  }

  findLabResearchOrderStatuses(params) {
    return super.request({
      url: "/directory/lab-research/statuses",
      params,
    });
  }

  findLabResearchReasons(params) {
    return super.request({
      url: "/directory/lab-research/reasons",
      params,
    });
  }

  findPolicyHolderRelations(params) {
    return super.request({
      url: "/directory/lab-research/policy-holder-relations",
      params,
    });
  }

  findRaces(params, options) {
    return super.request({
      url: "/directory/races",
      params,
      ...options,
    });
  }

  findCommunityNames(params) {
    return super.request({
      url: "/authorized-directory/community-names",
      params,
    });
  }

  findContacts(params, options) {
    return super.request({
      url: "/authorized-directory/contacts",
      hasEmptyParams: true,
      ...options,
      params,
    });
  }

  findContactsWithRoles(params, options) {
    return super.request({
      url: "/authorized-directory/contacts-&-roles",
      ...options,
      response: { extractDataOnly: true },
      params,
    });
  }

  findClassMemberTypes(params) {
    return super.request({
      url: "/directory/class-member-types",
      params,
    });
  }

  findIncidentReportStatuses(params) {
    return super.request({
      url: "/directory/incident-report-statuses",
      response: { extractDataOnly: true },
      params,
    });
  }

  findIncidentPlaces(params) {
    return super.request({
      url: "/directory/incident-places",
      params,
    });
  }

  findIncidentLevelReportingSettings(params) {
    return super.request({
      url: "/directory/incident-level-reporting-settings",
      params,
    });
  }

  findClientDeactivationReasons(params) {
    return super.request({
      url: "/directory/deactivation-reasons",
      response: { extractDataOnly: true },
      params,
    });
  }

  findProspectDeactivationReasons(params) {
    return super.request({
      url: "/directory/deactivation-reasons",
      response: { extractDataOnly: true },
      params,
    });
  }

  findIncidentTypes(params) {
    return super.request({
      url: "/directory/incident-types",
      params,
    });
  }

  findIncidentWeatherConditionTypes(params) {
    return super.request({
      url: "/directory/incident-weather-condition-types",
      params,
    });
  }

  findActivityTypes(params, options) {
    return super.request({
      url: "/directory/activity-types",
      ...options,
      params,
    });
  }

  findClientProgramNoteTypes(params) {
    return super.request({
      url: "/directory/client-program-note-types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findClientExpenseTypes(params) {
    return super.request({
      url: "/directory/client-expense-types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findDocumentCategories(params) {
    return super.request({
      url: "/authorized-directory/document-categories",
      response: { extractDataOnly: true },
      params,
    });
  }

  findFolderPermissionLevels(params) {
    return super.request({
      url: "/authorized-directory/document-folder-permission-levels",
      response: { extractDataOnly: true },
      params,
    });
  }

  findDocumentSignatureStatuses(params) {
    return super.request({
      url: `/authorized-directory/documents/e-sign/signature-statuses`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findDocumentTemplates(params) {
    return super.request({
      url: `/authorized-directory/documents/e-sign/templates`,
      response: { extractDataOnly: true },
      params,
    });
  }

  findDocumentSignatureRequestNotificationMethods(params) {
    return super.request({
      url: "/authorized-directory/documents/e-sign/request-notification-methods",
      response: { extractDataOnly: true },
      params,
    });
  }

  findSupportTicketTypes(params) {
    return super.request({
      url: "/directory/support-ticket-types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findESignDocumentTemplateAutofillFieldTypes(params) {
    return super.request({
      url: "/authorized-directory/documents/e-sign/templates/auto-fill-field-types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findESignDocumentTemplateOrganizationAutofillFieldTypes(params) {
    return super.request({
      url: "/authorized-directory/documents/e-sign/templates/organization-auto-fill-field-types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findESignDocumentTemplateToolboxRequesterFieldTypes(params) {
    return super.request({
      url: "/authorized-directory/documents/e-sign/templates/requester-field-types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findESignDocumentTemplateToolboxSignerFieldTypes(params) {
    return super.request({
      url: "/authorized-directory/documents/e-sign/templates/signer-field-types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findAppointmentStatuses(params) {
    return super.request({
      url: "/authorized-directory/appointments/statuses",
      response: { extractDataOnly: true },
      params,
    });
  }

  findAppointmentTypes(params) {
    return super.request({
      url: "/authorized-directory/appointments/types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findAppointmentClientReminders(params) {
    return super.request({
      url: "/authorized-directory/appointments/reminder-types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findAppointmentTimeIntervals(params) {
    return super.request({
      url: "/authorized-directory/appointments/time-intervals",
      response: { extractDataOnly: true },
      params,
    });
  }

  findAppointmentNotificationMethods(params) {
    return super.request({
      url: "/authorized-directory/appointments/notification-methods",
      response: { extractDataOnly: true },
      params,
    });
  }

  findAppointmentServiceCategories(params) {
    return super.request({
      url: "/authorized-directory/appointments/service-categories",
      response: { extractDataOnly: true },
      params,
    });
  }

  findAttorneyTypes(params) {
    return super.request({
      url: "/authorized-directory/attorney-power-types",
      response: { extractDataOnly: true },
      params,
    });
  }

  findProspectStatuses(params) {
    return super.request({
      url: "/directory/prospect-statuses",
      response: { extractDataOnly: true },
      params,
    });
  }

  findRelatedPartyRelationshipTypes(params) {
    return super.request({
      url: "/directory/related-party-relationships",
      response: { extractDataOnly: true },
      params,
    });
  }

  findOrganizationCanhaveHousingVouchers(params, options) {
    return super.request({
      url: "/clients/can-have-housing-vouchers",
      ...options,
      params,
    });
  }

  validateHousingVouchersTCode(params, options) {
    return super.request({
      url: "/clients/validate-uniq-in-organization",
      ...options,
      params,
    });
  }
}

const service = new DirectoryService();

export default service;
