import { array, bool } from "yup";

import { Shape, string } from "./types";

const WorkflowScheme = Shape(
  {
    communityIds: array().of(string()).min(1).required(),
    workflowTemplateName: string().nullable().required(),
    organizationId: string().nullable().required(),
    workflowType: string().nullable().required(),
    afterCommitShow: bool().nullable().required(),
    categoryId: string().nullable().required(),
  },
  [["medicaidNumber", "medicareNumber"]],
);

export default WorkflowScheme;
