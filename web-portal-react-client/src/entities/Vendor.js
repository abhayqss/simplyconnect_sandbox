const { Record, Set, List } = require("immutable");

const Vendor = Record({
  /*  add vendor  */
  id: null,
  name: null,
  website: null,
  email: null,
  companyId: null,
  phone: null,
  license: null,
  otherLicense: "",
  zipCode: null,
  premium: null,

  /*  address  */
  // address: Address(),
  city: "",
  street: "",
  state: "",
  introduction: "",
  credential: "",
  expYear: null,
  vendorTypeIds: Set(),
  serviceCategoryIds: Set(),
  serviceIds: Set(),
  languageIds: Set(),
  companyTypeId: null,
  logoPic: null,
  hieAgreement: false,

  /**
   * business hours
   */
  operatingWorkDay: "",
  operatingSaturday: "",
  operatingSunday: "",
  oid: "",
  cms: "",
  vendorPhotos: List(),
  clinicalVendor: false,
});

export default Vendor;
