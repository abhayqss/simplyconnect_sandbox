import BaseSchemeValidator from "./BaseSchemeValidator";
import WorkflowCategoryScheme from "schemes/WorkflowCategoryScheme";

class WorkflowCategoryFormSchemeValidator extends BaseSchemeValidator {
  constructor() {
    super(WorkflowCategoryScheme);
  }
}

export default WorkflowCategoryFormSchemeValidator;
