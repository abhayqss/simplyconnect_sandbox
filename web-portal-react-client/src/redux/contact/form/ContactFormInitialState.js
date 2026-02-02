const { Record } = require("immutable");

export default Record({
  tab: 0,
  error: null,
  isValid: true,
  isFetching: false,
  isValidLoginField: true,
  fields: new Record({
    id: null,

    /*General Data*/
    firstName: "",
    firstNameHasError: false,
    firstNameErrorText: "",

    lastName: "",
    lastNameHasError: false,
    lastNameErrorText: "",

    systemRoleId: null,
    systemRoleIdHasError: false,
    systemRoleIdErrorText: "",

    professionals: null,
    professionalsHasError: false,
    professionalsErrorText: "",

    login: "",
    loginHasError: false,
    loginErrorCode: null,
    loginErrorText: "",

    status: null,

    organizationId: null,
    organizationIdHasError: false,
    organizationIdErrorText: "",

    communityId: null,
    communityIdHasError: false,
    communityIdErrorText: "",

    //1011
    otherCommunityIds: null,
    otherCommunityIdsHasError: false,
    otherCommunityIdsErrorText: "",

    associatedClientIds: [],

    avatar: null,
    avatarHasError: false,
    avatarErrorText: null,

    avatarName: "",

    /*Contact Info*/
    isCommunityAddressUsed: true,
    vendorAddressUsed: false,
    careTeamRoleCode: "",
    associationAddressUsed: false,

    markedForDeletionAt: null,

    address: Record({
      street: "",
      streetHasError: false,
      streetErrorText: "",

      city: "",
      cityHasError: false,
      cityErrorText: "",

      stateId: null,
      stateIdHasError: false,
      stateIdErrorText: "",

      zip: "",
      zipHasError: false,
      zipErrorText: "",
    })(),

    phone: "",
    phoneHasError: false,
    phoneErrorText: "",

    mobilePhone: "",
    mobilePhoneHasError: false,
    mobilePhoneErrorText: "",

    fax: null,
    faxHasError: false,
    faxErrorText: "",

    secureMail: null,
    secureMailHasError: false,
    secureMailErrorText: "",

    /*Settings*/
    /*enabledSearchCapability: false,
        enabledSearchCapabilityHasError: false,
        enabledSearchCapabilityErrorText: '',*/

    enableContact: true,
    enableContactHasError: false,
    enableContactErrorText: "",

    enableContactVendor: false,
    enableContactVendorHasError: false,
    enableContactVendorErrorText: "",

    qaIncidentReports: false,
    qaIncidentReportsHasError: false,
    qaIncidentReportsErrorText: "",
  })(),
});
