const { Record } = require("immutable");

const AddToGroupConversation = Record({
  type: null,

  clientId: null,
  contactId: null,

  organizationId: null,
  communityId: null,
  chatUserId: null,
});

export default AddToGroupConversation;
