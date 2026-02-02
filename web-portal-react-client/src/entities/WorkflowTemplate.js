const { Record, Set, List } = require("immutable");

const Workflow = Record({
  organizationId: null,
  communityIds: Set(),
  workflowTemplateName: null,
  categoryId: null,
  afterCommitShow: false,
  workflowType: null,
});

export default Workflow;
