import BaseSchemeValidator from './BaseSchemeValidator'
import WorkflowScheme from "../schemes/WorkflowScheme";

class WorkflowTemplateFormSchemeValidator extends BaseSchemeValidator {
    constructor() {
        super(WorkflowScheme)
    }
}

export default WorkflowTemplateFormSchemeValidator
