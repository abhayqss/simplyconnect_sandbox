import Address from "./Address";

const { Record, Set, List } = require("immutable");

const PrimaryContact = Record({
  typeName: "",
  typeTitle: "",
  notificationMethodName: "",
  notificationMethodTitle: "",
  careTeamMemberId: null,
  firstName: "",
  lastName: "",
  active: false,
  isMarkedForDeletion: false,
});

export const HousingVouchers = Record({
  tCode: null,
  expiryDate: null,
});
export const Attorney = Record({
  id: null,
  firstName: null,
  lastName: null,
  types: Set(),
  email: null,
  phone: null,
  street: null,
  city: null,
  state: null,
  stateTitle: null,
  zipCode: null,
});
export const ContactItem = Record({
  id: null,
  firstName: null,
  lastName: null,
  type: null,
  email: null,
  phone: null,
  street: null,
  city: null,
  state: null,
  stateTitle: null,
  zipCode: null,
});

export const Insurance = Record({
  id: null,
  groupNumber: null,
  memberNumber: null,
  networkId: null,
  paymentPlan: null,
});

const Client = Record({
  id: null,
  legacyId: null,

  isActive: true,
  manuallyCreated: true,
  careTeamManager: "",
  /*Demographics*/
  ssn: null,
  hasNoSSN: false,
  unit: null,
  race: "",
  raceId: null,
  firstName: "",
  lastName: "",
  fullName: "",
  genderId: null,
  ethnicityId: null,
  maritalStatusId: null,
  birthDate: "",
  location: "",
  locationPhone: "",
  /*Attorneys*/
  attorneys: List(),
  languageWritten: List(),
  languageSpoken: List(),

  /*Community*/
  organizationId: null,
  communityId: null,
  community: "",

  /* Contact */
  contact: List(),
  /*housingVouchers */
  housingVouchers: List(),
  canViewHousingVouchers: false,

  /*Telecom*/
  cellPhone: null,
  homePhone: null,
  phone: null,
  hasNoEmail: false,
  email: "",
  address: Address(),
  avatar: null,
  avatarId: null,
  avatarName: "",

  /*Insurance*/
  diagnoses: Set(),
  insurances: List(),
  medicareNumber: null,
  medicaidNumber: null,
  insuranceNetwork: "",
  insuranceNetworkTitle: "",
  insuranceAuthorizations: List(),

  /*Primary contact*/
  primaryContact: PrimaryContact(),

  /*Ancillary Information*/
  primaryCarePhysicianFirstName: "",
  primaryCarePhysicianLastName: "",
  primaryCarePhysicianPhone: "",
  retained: null,
  intakeDate: null,
  currentPharmacyName: null,
  referralSource: null,
  riskScore: null,
  hasAdvancedDirectiveOnFile: false,

  isDataShareEnabled: false,

  policyNumber: "",
  policyHolderName: "",
  policyHolderRelationName: "",
  policyHolderDOB: "",

  /*HIE Consent Policy*/
  hieConsentPolicyName: null,
  hieConsentPolicyObtainedFrom: null,
  hieConsentPolicyObtainedBy: null,
  hieConsentPolicyObtainedDate: null,

  isHL7: false,

  /**
   *  1011
   */
  hasNoMedicareNumber: false,
  hasNoMedicaidNumber: false,
  /**
   * add Primary Emergency Contact
   */
  emergencyContactName: null,
  emergencyContactRelationship: null,
  emergencyContactCellPhone: null,
  emergencyContactWorkPhone: null,
  emergencyContactEmail: null,
});

export default Client;
