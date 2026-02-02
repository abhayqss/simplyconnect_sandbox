const { Record } = require("immutable");

export const ReferralEmail = Record({
  canEdit: true,
  value: "",
});

export default Record({
  tab: 0,
  error: null,
  isFetching: false,

  isValid: true,
  isValidLegalInfoTab: true,
  isValidMarketplaceTab: true,

  fields: Record({
    id: null,

    name: "",
    nameHasError: false,
    nameErrorText: "",

    oid: "",
    oidHasError: false,
    oidErrorText: "",

    isSharingData: false,
    isSharingDataHasError: false,
    isSharingDataErrorText: "",

    numberOfBeds: null,
    numberOfBedsHasError: null,
    numberOfBedsErrorText: null,

    numberOfVacantBeds: null,
    numberOfVacantBedsHasError: null,
    numberOfVacantBedsErrorText: null,

    fax: null,
    faxHasError: null,
    faxErrorText: null,
    addFax: null,

    faxLogin: null,
    faxLoginHasError: null,
    faxLoginErrorText: null,

    faxPassword: null,
    faxPasswordHasError: null,
    faxPasswordErrorText: null,

    email: "",
    emailHasError: false,
    emailErrorText: "",

    phone: "",
    phoneHasError: false,
    phoneErrorText: "",

    street: "",
    streetHasError: false,
    streetErrorText: "",

    city: "",
    cityHasError: false,
    cityErrorText: "",

    stateId: null,
    stateIdHasError: false,
    stateIdErrorText: "",

    zipCode: "",
    zipCodeHasError: false,
    zipCodeErrorText: "",

    logo: null,
    logoHasError: false,
    logoErrorText: "",

    pictures: [],
    pictureFiles: [],
    pictureFilesHasError: false,
    pictureFilesErrorText: {},

    logoName: "",

    docutrackPharmacyConfig: Record({
      isIntegrationEnabled: false,

      serverDomain: "",
      serverDomainHasError: false,
      serverDomainErrorText: "",

      clientType: null,
      clientTypeHasError: false,
      clientTypeErrorText: "",

      serverCertificate: null,
      configuredCertificate: null,

      publicKeyCertificates: [],
      publicKeyCertificatesHasError: false,
      publicKeyCertificatesErrorText: "",

      businessUnitCodes: [""],
      businessUnitCodesErrorText: {},

      useSuggestedCertificate: true,
      shouldRemoveCertificate: false,
    })(),

    allowExternalInboundReferrals: false,

    marketplace: Record({
      id: null,

      confirmVisibility: false,

      prerequisite: "",
      prerequisiteHasError: false,
      prerequisiteErrorText: "",

      exclusion: "",
      exclusionHasError: false,
      exclusionErrorText: "",

      appointmentsSecureEmail: "",
      appointmentsSecureEmailHasError: false,
      appointmentsSecureEmailErrorText: "",

      referralEmails: [ReferralEmail()],
      referralEmailsHasError: false,
      referralEmailsErrorText: "",

      servicesSummaryDescription: "",
      servicesSummaryDescriptionHasError: false,
      servicesSummaryDescriptionErrorText: "",

      primaryFocusIds: [],
      primaryFocusIdsHasError: false,
      primaryFocusIdsErrorText: "",

      communityTypeIds: [],
      communityTypeIdsHasError: false,
      communityTypeIdsErrorText: "",

      levelOfCareIds: [],
      levelOfCareIdsHasError: false,
      levelOfCareIdsErrorText: "",

      ageGroupIds: [],
      ageGroupIdsHasError: false,
      ageGroupIdsErrorText: "",

      serviceTreatmentApproachIds: [],
      serviceTreatmentApproachIdsHasError: false,
      serviceTreatmentApproachIdsErrorText: "",

      emergencyServiceIds: [],
      emergencyServiceIdsHasError: false,
      emergencyServiceIdsErrorText: "",

      languageServiceIds: [],
      languageServiceIdsHasError: false,
      languageServiceIdsErrorText: "",

      additionalServiceIds: [],
      additionalServiceIdsHasError: false,
      additionalServiceIdsErrorText: "",

      insuranceNetworkIds: [],
      insuranceNetworkIdsHasError: false,
      insuranceNetworkIdsErrorText: "",

      insurancePaymentPlanIds: [],
      insurancePaymentPlanIdsHasError: false,
      insurancePaymentPlanIdsErrorText: "",

      allInsurancesAccepted: false,
      allInsurancesAcceptedHasError: false,
      allInsurancesAcceptedErrorText: "",

      featuredCommunities: [],
      featuredCommunitiesHasError: false,
      featuredCommunitiesErrorText: "",

      allowAppointments: false, //This Name Has to be changed , but using it for backend integration
      isSaved: false,
      rating: 0,
    })(),
  })(),
});
