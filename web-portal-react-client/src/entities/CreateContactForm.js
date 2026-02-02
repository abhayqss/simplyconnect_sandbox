import Address from "./Address";

const { Record, Set, List } = require("immutable");

const CreateContactForm = Record({
  firstName: "",
  lastName: "",
  login: "", // email
  avatar: "", // userPhone
  vendorAddressUsed: false,

  mobilePhone: "",
  phone: "",
  fax: "",
  secureEmail: "",
  address: Address(),
  vendorId: "",
  careTeamRoleCode: "",
});

export default CreateContactForm;
