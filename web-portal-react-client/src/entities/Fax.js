const { Record, Set, List } = require("immutable");

const Fax = Record({
  isActive: true,
  /**
   * General Data
   */
  content: "",
  recipientCategory: "internal",
  header: "",
  jobName: "",
  contactId: "",
  organizationId: "",
  communityId: "",
  recipientName: "",
  receiveFaxNumber: "",
  file: "",
  roleName: "",
});

export default Fax;
