import { Shape, string } from "./types";

const WorkflowCategoryScheme = Shape({
  workflowCategoryName: string().nullable().required(),
  organizationId: string().nullable().required(),
});

export default WorkflowCategoryScheme;
