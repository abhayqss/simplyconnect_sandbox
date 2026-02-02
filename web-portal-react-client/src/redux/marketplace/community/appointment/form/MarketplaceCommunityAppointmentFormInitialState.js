const { Record, hash } = require("immutable");

let hashCode = hash();

export default Record({
  tab: 0,
  error: null,
  isValid: true,
  isFetching: false,
  fields: new Record({
    id: null,

    organization: "",
    organizationHasError: false,
    organizationErrorText: "",

    community: "",
    communityHasError: false,
    communityErrorText: "",

    name: "",
    nameHasError: false,
    nameErrorText: "",

    email: "",
    emailHasError: false,
    emailErrorText: "",

    serviceIds: [],
    serviceIdsHasError: false,
    serviceIdsErrorText: "",

    phone: "",
    phoneHasError: false,
    phoneErrorText: "",

    isUrgentCare: false,
    isUrgentCareHasError: false,
    isUrgentCareErrorText: "",

    appointmentDate: null,
    appointmentDateHasError: false,
    appointmentDateErrorText: "",

    comment: "",
    commentHasError: false,
    commentErrorText: "",
  })(),
  getHashCode() {
    return hashCode;
  },
  updateHashCode() {
    hashCode = this.fields?.hashCode();

    return this;
  },
  updateHashCodeIf(condition = false) {
    return condition ? this.updateHashCode() : this;
  },
  isChanged() {
    return this.fields?.hashCode() !== hashCode;
  },
});
