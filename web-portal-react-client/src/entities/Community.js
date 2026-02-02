import Marketplace from "./Marketplace";

const { Record, List } = require("immutable");

const DocutrackPharmacyConfig = Record({
  isIntegrationEnabled: false,
  serverDomain: "",
  clientType: null,
  serverCertificate: null,
  configuredCertificate: null,
  publicKeyCertificates: [],
  businessUnitCodes: List(),
  useSuggestedCertificate: true,
  shouldRemoveCertificate: false,
});

const Community = Record({
  id: null,
  name: "",
  oid: "",
  isFamily: false,
  licenseNumber: null,
  isSharingData: false,
  numberOfBeds: null,
  numberOfVacantBeds: null,
  // fax: "",
  faxLogin: "",
  faxPassword: "",
  email: "",
  phone: "1",
  street: "",
  city: "",
  stateId: null,
  zipCode: "",
  logo: null,
  pictures: [],
  pictureFiles: [],
  logoName: "",
  signatureConfig: Record({
    canEdit: false,
    isPinEnabled: false,
  })(),
  websiteUrl: "",
  docutrackPharmacyConfig: DocutrackPharmacyConfig(),
  allowExternalInboundReferrals: false,
  marketplace: Marketplace(),
  //HIE Opt In / Opt Out Section
  hieConsentPolicyName: null,
  canEditHieConsentPolicy: false,
  eventsConfig: Record({
    canEdit: false,
    isMedicationRiskAlertingEnabled: false,
  })(),
  allowFamilyAppToGetMatchedClients: false,
  certificateLoaded: false,
  openFax: false,
  cover: null,
  coverName: "",
});

export default Community;
