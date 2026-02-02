import BaseService from "./BaseService";
import { PAGINATION } from "lib/Constants";
import { isEmpty } from "lib/utils/Utils";

const { FIRST_PAGE } = PAGINATION;

export class CareTeamMemberService extends BaseService {
  find({ page = FIRST_PAGE, size = 15, sort, ...params }) {
    return super.request({
      url: "/care-team-members",
      params: { page: page - 1, size, sort, ...params },
    });
  }

  findNonClinicalTeam({ page, size, sort, ...params }) {
    return super.request({
      url: "/care-team-members/findNonClinicalTeam",
      params: { page: page - 1, size, sort, ...params },
    });
  }

  getVendorContacts(clientId, organizationId, communityId) {
    return super.request({
      url: "/care-team-members/vendorContacts",
      params: { clientId, organizationId, communityId },
    });
  }

  getNonClinicalTeamRole(contactId, nonClinicalTeam) {
    return super.request({
      url: `/authorized-directory/care-team/client-member-roles?contactId=${contactId}&nonClinicalTeam=${nonClinicalTeam}`,
    });
  }

  findDefaultMember({ clientId, affiliation }) {
    return super.request({
      url: `/care-team-members/findPrimaryDefault?clientId=${clientId}&affiliation=${affiliation}`,
    });
  }

  setPrimaryDefault({ clientId, careTeamId, removeDefault }) {
    return super.request({
      url: `/care-team-members/setPrimaryDefault`,
      method: "POST",
      body: {
        careTeamId,
        clientId,
        removeDefault,
      },
    });
  }

  findById(memberId, params) {
    return super.request({
      url: `/care-team-members/${memberId}`,
      params: params,
      mockParams: { id: memberId },
    });
  }

  findCareTeamEmployees(params) {
    return super.request({
      url: "/care-team-members/contacts",
      params: params,
    });
  }

  careTeamEmployeeCount(params) {
    return super.request({
      url: "/care-team-members/contacts/count",
      params: params,
      response: { extractDataOnly: true },
    });
  }

  count(params, options) {
    return super.request({
      url: "/care-team-members/count",
      ...options,
      params,
    });
  }

  save(member) {
    return super.request({
      method: !isEmpty(member.id) ? "PUT" : "POST",
      url: "/care-team-members",
      body: member,
      type: "json",
    });
  }

  saveConversationSid(member) {
    return super.request({
      method: "POST",
      url: "/care-team-members/saveConversationSid",
      body: member, // clientId  twilioConversationSid
    });
  }

  deleteById(careTeamMemberId) {
    return super.request({
      method: "DELETE",
      url: `/care-team-members/${careTeamMemberId}`,
      mockParams: { id: careTeamMemberId },
    });
  }

  deleteByIdForNonclinical(careTeamMemberId) {
    return super.request({
      method: "DELETE",
      url: `/care-team-members/deleNonclinical/${careTeamMemberId}`,
    });
  }

  canAdd(params, options) {
    return super.request({
      url: `/care-team-members/can-add`,
      ...options,
      params,
    });
  }

  canNewAdd(params) {
    return super.request({
      url: `/care-team-members/can-addMember`,
      params,
    });
  }

  canView(params, options) {
    return super.request({
      url: `/care-team-members/can-view`,
      ...options,
      params,
    });
  }

  findOrganizations(params) {
    return super.request({
      url: `/care-team-members/contacts/organizations`,
      params,
    });
  }

  hasAffiliatedCommunities(clientId, params) {
    return super.request({
      url: `/clients/${clientId}/exists-affiliated-communities`,
      params,
      response: { extractDataOnly: true },
    });
  }

  careTeamIncomingInvitationsExist(clientId, params) {
    return super.request({
      url: `/care-team-invitations/hie-consent-change/exists-incoming?clientId=${clientId}`,
      params,
      response: { extractDataOnly: true },
    });
  }

  findCareTeamIncomingInvitations(communityId, params) {
    return super.request({
      url: `/care-team-invitations/hie-consent-change/find-incoming?communityId=${communityId}`,
      params,
      response: { extractDataOnly: true },
    });
  }

  showRemindFill(clientId) {
    return super.request({
      url: `/clients/${clientId}/showRemindFill`,
      response: { extractDataOnly: true },
    });
  }

  neverShowRemindFill(clientId) {
    return super.request({
      url: `/clients/${clientId}/neverShowRemindFill`,
      response: { extractDataOnly: true },
      method: "PUT",
    });
  }
}

const service = new CareTeamMemberService();
export default service;
