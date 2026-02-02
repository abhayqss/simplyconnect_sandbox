import BaseSchemeValidator from './BaseSchemeValidator'
import DomainsSelectionScheme from 'schemes/DomainsSelectionScheme'

class DomainsSelectionFormValidator extends BaseSchemeValidator {
    constructor() {
        super(DomainsSelectionScheme)
    }
}

export default DomainsSelectionFormValidator
