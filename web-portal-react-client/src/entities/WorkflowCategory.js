const { Record } = require("immutable");

const WorkflowCategory = Record({
  organizationId: null,
  workflowCategoryName: null,
});

export default WorkflowCategory;
