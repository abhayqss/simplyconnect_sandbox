import Client from "./Client";
import ReferralMarketplace from "./ReferralMarketplace";

const { Record, Set, List } = require("immutable");

const ReferralRequest = Record({
  id: null,
  date: null,

  statusId: null,
  statusName: null,
  statusTitle: null,

  priorityId: null,
  priorityName: null,
  priorityTitle: null,

  person: null,
  communityTitle: null,
  organizationPhone: null,
  organizationEmail: null,
  referringCommunityId: null,
  referringOrganizationId: null,

  services: List(),

  instructions: null,
  assigneeId: null,

  client: Client(),

  marketplace: ReferralMarketplace(),

  attachmentFiles: List(),

  attachedClientDocumentIds: List(),
  attachedClientDocumentFiles: List(),
  vendorCareTeams: List(),

  isFacesheetShared: false,
  isCcdShared: false,
  isServicePlanShared: false,

  sharedCommunityIds: {},
  categoryType: Set(),
});

export default ReferralRequest;
