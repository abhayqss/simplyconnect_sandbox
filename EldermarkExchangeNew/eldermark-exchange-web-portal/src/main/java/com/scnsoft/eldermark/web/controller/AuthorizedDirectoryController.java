package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.ClientAccessType;
import com.scnsoft.eldermark.beans.ContactNameFilter;
import com.scnsoft.eldermark.beans.OrganizationFilter;
import com.scnsoft.eldermark.beans.PartnerNetworkFilter;
import com.scnsoft.eldermark.beans.PharmacyFilter;
import com.scnsoft.eldermark.dto.ClientNameBirthdayDto;
import com.scnsoft.eldermark.dto.ContactNameRoleDto;
import com.scnsoft.eldermark.dto.DirectoryClientListItemDto;
import com.scnsoft.eldermark.dto.EventTypeGroupDto;
import com.scnsoft.eldermark.dto.InsuranceNetworkDto;
import com.scnsoft.eldermark.dto.InsurancePlanDto;
import com.scnsoft.eldermark.dto.KeyValueDto;
import com.scnsoft.eldermark.dto.NotificationsPreferencesDto;
import com.scnsoft.eldermark.dto.PartnerNetworkOrganizationListItemDto;
import com.scnsoft.eldermark.dto.ReportTypeDto;
import com.scnsoft.eldermark.dto.ResponsibilityDto;
import com.scnsoft.eldermark.dto.RoleDto;
import com.scnsoft.eldermark.dto.ServiceCategoryAwareIdentifiedTitledDto;
import com.scnsoft.eldermark.dto.ServiceTypeListItemDto;
import com.scnsoft.eldermark.dto.assessment.AssessmentManagementDto;
import com.scnsoft.eldermark.dto.assessment.AssessmentTypeGroupDto;
import com.scnsoft.eldermark.dto.directory.DirCommunityListItemDto;
import com.scnsoft.eldermark.dto.directory.DirOrganizationListItemDto;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderPermissionLevelDto;
import com.scnsoft.eldermark.dto.filter.ClientFilterDto;
import com.scnsoft.eldermark.dto.signature.DocumentSignatureTemplateToolboxSignerFieldTypeDto;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.Responsibility;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.facade.CareTeamRoleFacade;
import com.scnsoft.eldermark.facade.CcdCodeFacade;
import com.scnsoft.eldermark.facade.ClientAssessmentFacade;
import com.scnsoft.eldermark.facade.ClientFacade;
import com.scnsoft.eldermark.facade.CommunityFacade;
import com.scnsoft.eldermark.facade.ContactFacade;
import com.scnsoft.eldermark.facade.DirectoryFacade;
import com.scnsoft.eldermark.facade.InsuranceFacade;
import com.scnsoft.eldermark.facade.OrganizationFacade;
import com.scnsoft.eldermark.facade.PartnerNetworkFacade;
import com.scnsoft.eldermark.facade.ReportsFacade;
import com.scnsoft.eldermark.facade.document.category.DocumentCategoryFacade;
import com.scnsoft.eldermark.facade.document.folder.DocumentFolderFacade;
import com.scnsoft.eldermark.facade.signature.DocumentSignatureTemplateFacade;
import com.scnsoft.eldermark.shared.ccd.CcdCodeDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledEntityDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping("/authorized-directory")
@RestController
public class AuthorizedDirectoryController {

    @Autowired
    private OrganizationFacade organizationFacade;

    @Autowired
    private CommunityFacade communityFacade;

    @Autowired
    private CareTeamRoleFacade careTeamRoleFacade;

    @Autowired
    private ClientAssessmentFacade clientAssessmentFacade;

    @Autowired
    private DirectoryFacade directoryFacade;

    @Autowired
    private ClientFacade clientFacade;

    @Autowired
    private InsuranceFacade insuranceFacade;

    @Autowired
    private CcdCodeFacade ccdCodeFacade;

    @Autowired
    private PartnerNetworkFacade partnerNetworkFacade;

    @Autowired
    private ContactFacade contactFacade;

    @Autowired
    private DocumentCategoryFacade documentCategoryFacade;

    @Autowired
    private DocumentFolderFacade documentFolderFacade;

    @Autowired
    private ReportsFacade reportsFacade;

    @Autowired
    private DocumentSignatureTemplateFacade documentSignatureTemplateFacade;

    @ResponseBody
    @GetMapping(value = "/organizations")
    public Response<List<DirOrganizationListItemDto>> getOrganizations(
        @ModelAttribute OrganizationFilter filter) {
        return Response.successResponse(organizationFacade.findAll(filter));
    }

    @ResponseBody
    @GetMapping(value = "/communities")
    public Response<List<DirCommunityListItemDto>> getCommunities(
            @RequestParam Long organizationId, @RequestParam(required = false) Boolean isMarketplaceEnabledOnly
            ) {
        return Response.successResponse(communityFacade.findNonBlankByOrgId(organizationId, isMarketplaceEnabledOnly));
    }

    @ResponseBody
    @GetMapping(value = "/editable-system-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<RoleDto>> getSystemRoles() {
        return Response.successResponse(careTeamRoleFacade.findEditableSystemRoles());
    }

    @GetMapping(value = "/assessment-survey", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<String> getSurvey(@RequestParam("clientId") Long clientId, @RequestParam("typeId") Long typeId) {
        String json = clientAssessmentFacade.findSurveyJson(clientId, typeId);
        if (StringUtils.isBlank(json)) {
            return Response.errorResponse(BusinessExceptionType.NOT_FOUND);
        }
        return Response.successResponse(json);
    }

    @GetMapping(value = "/assessment-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<AssessmentTypeGroupDto>> getAssessmentTypes(@RequestParam("clientId") Long clientId,
                                                                     @RequestParam(value = "types", required = false) List<String> filterBy) {
        return Response.successResponse(clientAssessmentFacade.findGroupedAssessmentTypes(clientId, filterBy));
    }

    @GetMapping(value = "/assessment-management", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<AssessmentManagementDto> getManagement(@RequestParam("clientId") Long clientId,
                                                           @RequestParam("typeId") Long typeId) {
        var assessmentManagementDto = clientAssessmentFacade.getManagement(clientId, typeId);
        return Response.successResponse(assessmentManagementDto);
    }

    @GetMapping(value = "/assessment-score", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> getScore(@RequestParam("typeId") Long typeId,
                                   @RequestParam("clientId") Long clientId,
                                   @RequestParam("dataJson") String dataJson) {
        return Response.successResponse(clientAssessmentFacade.calculateScore(clientId, typeId, dataJson));
    }

    @GetMapping(value = "/care-team/client-member-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<RoleDto>> getClientCareTeamMemberRoles(@RequestParam(name = "contactId", required = false) Long employeeId) {
        return Response.successResponse(careTeamRoleFacade.findEditableClientCareTeamMemberRoles(employeeId));
    }

    @GetMapping(value = "/care-team/community-member-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<RoleDto>> getCommunityCareTeamMemberRoles() {
        return Response.successResponse(careTeamRoleFacade.findEditableCommunityCareTeamMemberRoles());
    }

    @GetMapping(value = "/care-team/responsibilities", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ResponsibilityDto>> getCareTeamMemberResponsibilities() {
        return Response.successResponse(Stream.of(Responsibility.values())
            .map(r -> new ResponsibilityDto(r.toString(), r.getDescription(), r.isAssignable())).collect(Collectors.toList()));
    }

    @GetMapping(value = "/care-team/notification-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getCareTeamMemberNotificationTypes() {
        return Response.successResponse(
            Stream.of(NotificationType.values())
                .map(r -> new NamedTitledEntityDto(r.toString(), r.getDescription()))
                .sorted(Comparator.comparing(NamedTitledEntityDto::getTitle))
                .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/care-team/default-notification-preferences", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NotificationsPreferencesDto>> getDefaultNotificationPreferences(@RequestParam(value = "careTeamRoleId") Long careTeamRoleId) {
        return Response.successResponse(directoryFacade.getDefaultNotificationPreferences(careTeamRoleId));
    }

    @GetMapping(value = "/grouped-event-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<EventTypeGroupDto>> getGroupedEventTypes() {
        return Response.successResponse(directoryFacade.getEventGroups());
    }


    @GetMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DirectoryClientListItemDto>> getClientNames(@ModelAttribute ClientFilterDto filter) {
        if (filter.getClientAccessType() == null) filter.setClientAccessType(ClientAccessType.DETAILS);
        return Response.successResponse(clientFacade.findClientsWithNonBlankNames(filter));
    }

    @GetMapping(value = "/client-pharmacy-names", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<String>> getClientPharmacyNames(@ModelAttribute PharmacyFilter filter) {
        return Response.successResponse(clientFacade.findClientPharmacyNames(filter));
    }

    @GetMapping(value = "/services-in-use")
    public Response<List<ServiceTypeListItemDto>> getServicesTreatmentApproachesInUse(
            @RequestParam(required = false) Long excludeCommunityId
    ) {
        return Response.successResponse(directoryFacade.getServicesInUseExcluding(excludeCommunityId));
    }

    @GetMapping(value = "/marketplace-languages", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<KeyValueDto<Long>>> getLanguages() {
        return Response.successResponse(directoryFacade.getLanguageServices());
    }

    @GetMapping(value = "/insurance/networks")
    public Response<List<InsuranceNetworkDto>> getNetworks(
        @RequestParam(name = "name", required = false) String title) {
        return Response.successResponse(insuranceFacade.getNetworks(title));
    }

    @ResponseBody
    @GetMapping(value = "/insurance/payment-plans")
    public Response<List<InsurancePlanDto>> getNetworkPaymentPlans(
        @RequestParam(value = "networkId", required = false) Long networkId) {
        return Response.successResponse(insuranceFacade.getPaymentPlans(networkId));
    }

    @GetMapping(value = "/service-statuses", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedTitledEntityDto>> getServiceStatuses() {
        return Response.successResponse(directoryFacade.getReferralServiceStatuses());
    }

    @GetMapping(value = "/service-control-request-statuses", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedTitledEntityDto>> getServiceControlRequestStatuses() {
        return Response.successResponse(directoryFacade.getReferralServiceControlRequestStatuses());
    }

    @ResponseBody
    @GetMapping(value = "/insurance/network-aggregated-names")
    public Response<List<String>> getInsuranceNetworkAggregatedNames(@RequestParam(name = "text", required = false) String value,
                                                                     @RequestParam Long organizationId) {
        return Response.successResponse(insuranceFacade.findAggregatedNamesLike(organizationId, value));
    }

    @GetMapping(value = "/partner-networks")
    public Response<Collection<PartnerNetworkOrganizationListItemDto>> getPartnersNetworks(@ModelAttribute PartnerNetworkFilter filter) {
        return Response.successResponse(partnerNetworkFacade.findAllGroupedByOrganization(filter));
    }

    @GetMapping(value = "/referral-reasons")
    public Response<List<CcdCodeDto>> getReferralReasons(
        @RequestParam(value = "search", required = false) String search) {
        return Response.successResponse(ccdCodeFacade.findReferralReason(search));
    }

    @GetMapping(value = "/referral-decline-reasons")
    @Deprecated
    public Response<List<IdentifiedNamedTitledEntityDto>> getReferralDeclineReasons() {
        return Response.successResponse(directoryFacade.getReferralDeclineReasons());
    }

    @GetMapping(value = "/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedEntityDto>> findContactNames(
        @RequestParam Long organizationId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) List<String> roles,
        @RequestParam(required = false) List<EmployeeStatus> statuses
    ) {
        ContactNameFilter filter = populateContactNameFilter(organizationId, name, roles, statuses);
        return Response.successResponse(contactFacade.findNames(filter));
    }

    @GetMapping(value = "/document-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentCategoryItemDto>> findDocumentCategories(@RequestParam Long organizationId) {
        return Response.successResponse(documentCategoryFacade.findByOrganizationId(organizationId));
    }

    @GetMapping(value = "/document-folder-permission-levels", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentFolderPermissionLevelDto>> findDocumentFolderPermissionLevels() {
        return Response.successResponse(documentFolderFacade.findPermissionLevels());
    }

    @GetMapping(value = "/inaccessible-client-properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<String>> findInaccessibleClientProperties() {
        return Response.successResponse(clientFacade.findInaccessibleClientProperties());
    }

    @GetMapping(value = "/report-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ReportTypeDto>> getReportTypes(
            @RequestParam(value = "organizationId", required = false) Long organizationId,
            @RequestParam(value = "communityIds", required = false) List<Long> communityIds
    ) {
        return Response.successResponse(reportsFacade.getAvailableReportTypes(organizationId, communityIds));
    }

    @GetMapping(value = "/documents/e-sign/request-notification-methods", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getDocumentESignRequestNotificationMethods() {
        return Response.successResponse(directoryFacade.getDocumentESignRequestNotificationMethods());
    }

    @GetMapping(value = "/documents/e-sign/signature-statuses", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getDocumentESignSignatureStatuses() {
        return Response.successResponse(directoryFacade.getDocumentESignSignatureStatuses());
    }

    @GetMapping(value = "/documents/e-sign/request-recipient-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getDocumentESignRequestRecipientTypes() {
        return Response.successResponse(directoryFacade.getDocumentESignRequestRecipientTypes());
    }

    @GetMapping(value = "/documents/e-sign/templates/auto-fill-field-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedTitledEntityDto>> getAutoFillFieldTypes() {
        return Response.successResponse(documentSignatureTemplateFacade.getAutoFillFieldTypes());
    }

    @GetMapping(value = "/documents/e-sign/templates/organization-auto-fill-field-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedTitledEntityDto>> getOrganizationAutoFillFieldTypes() {
        return Response.successResponse(documentSignatureTemplateFacade.getOrganizationAutoFillFieldTypes());
    }

    @GetMapping(value = "/documents/e-sign/templates/requester-field-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedTitledEntityDto>> getToolboxRequesterFieldTypes() {
        return Response.successResponse(documentSignatureTemplateFacade.getToolboxRequesterTypes());
    }

    @GetMapping(value = "/documents/e-sign/templates/signer-field-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentSignatureTemplateToolboxSignerFieldTypeDto>> getToolboxSignerFieldTypes() {
        return Response.successResponse(documentSignatureTemplateFacade.getToolboxSignerFieldTypes());
    }

    @GetMapping(value = "/appointments/notification-methods", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getAppointmentNotificationMethods() {
        return Response.successResponse(directoryFacade.getAppointmentNotificationMethods());
    }

    @GetMapping(value = "/appointments/reminder-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getAppointmentReminderTypes() {
        return Response.successResponse(directoryFacade.getAppointmentReminderTypes());
    }

    @GetMapping(value = "/appointments/service-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getAppointmentServiceCategories() {
        return Response.successResponse(directoryFacade.getAppointmentServiceCategories());
    }

    @GetMapping(value = "/appointments/statuses", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getAppointmentStatuses() {
        return Response.successResponse(directoryFacade.getAppointmentStatuses());
    }

    @GetMapping(value = "/appointments/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getAppointmentTypes() {
        return Response.successResponse(directoryFacade.getAppointmentTypes());
    }

    @GetMapping(value = "/client-birthdays", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientNameBirthdayDto>> getClientNamesWithBirthdays(@ModelAttribute ClientFilterDto filter) {
        filter.setClientAccessType(ClientAccessType.DETAILS);
        return Response.successResponse(clientFacade.findNonBlankNamesWithBirthdays(filter));
    }

    @GetMapping(value = "/contacts-&-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ContactNameRoleDto>> findContactNamesWithRoles(
            @RequestParam Long organizationId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) List<EmployeeStatus> statuses
    ) {
        ContactNameFilter filter = populateContactNameFilter(organizationId, name, roles, statuses);
        return Response.successResponse(contactFacade.findNamesWithRoles(filter));
    }

    @GetMapping(value = "/service-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedTitledEntityDto>> getServiceCategories(
            @RequestParam(value = "isAccessibleOnly", required = false) Boolean isAccessibleOnly) {
        return Response.successResponse(directoryFacade.getServiceCategories(isAccessibleOnly));
    }

    @GetMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ServiceCategoryAwareIdentifiedTitledDto>> getServices(
            @RequestParam(value = "serviceCategoryIds", required = false) List<Long> serviceCategoryIds,
            @RequestParam(value = "text", required = false) String searchText,
            @RequestParam(value = "isAccessibleOnly", required = false) Boolean isAccessibleOnly) {
        return Response.successResponse(directoryFacade.getServiceTypes(serviceCategoryIds, searchText, isAccessibleOnly));
    }

    private ContactNameFilter populateContactNameFilter(Long organizationId,
                                                        String name,
                                                        List<String> roles,
                                                        List<EmployeeStatus> statuses) {
        var filter = new ContactNameFilter();
        filter.setOrganizationIds(Set.of(organizationId));
        filter.setName(name);
        filter.setRoles(
                Stream.ofNullable(roles)
                        .flatMap(Collection::stream)
                        .map(CareTeamRoleCode::getByCode)
                        .collect(Collectors.toList())
        );
        filter.setStatuses(statuses);
        return filter;
    }

}
