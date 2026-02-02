import BaseSchemeValidator from './BaseSchemeValidator'
import ProspectActivationFormScheme from 'schemes/ProspectActivationFormScheme'

class ProspectActivationFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ProspectActivationFormScheme)
    }
}

export default ProspectActivationFormValidator
