import BaseSchemeValidator from './BaseSchemeValidator'
import ProspectDocumentScheme from 'schemes/ProspectDocumentScheme'

class ProspectDocumentFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ProspectDocumentScheme)
    }
}

export default ProspectDocumentFormValidator
