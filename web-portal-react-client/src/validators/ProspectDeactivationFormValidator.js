import BaseSchemeValidator from './BaseSchemeValidator'
import ProspectDeactivationFormScheme from 'schemes/ProspectDeactivationFormScheme'

class ProspectDeactivationFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ProspectDeactivationFormScheme)
    }
}

export default ProspectDeactivationFormValidator
