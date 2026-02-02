import scheme from 'schemes/ProspectEventFilterScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class ProspectEventFilterValidator extends BaseSchemeValidator {
    constructor() {
        super(scheme)
    }
}

export default ProspectEventFilterValidator
