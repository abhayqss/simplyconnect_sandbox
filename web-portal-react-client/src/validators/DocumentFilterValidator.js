import scheme from 'schemes/ClientDocumentFilterScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class DocumentFilterValidator extends BaseSchemeValidator {
    constructor() {
        super(scheme)
    }
}

export default DocumentFilterValidator
