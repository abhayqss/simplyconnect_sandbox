import BaseSchemeValidator from './BaseSchemeValidator'
import AssociationScheme from "../schemes/AssociationScheme";

class AssociationFormValidator extends BaseSchemeValidator {
    constructor() {
        super(AssociationScheme)
    }
}

export default AssociationFormValidator