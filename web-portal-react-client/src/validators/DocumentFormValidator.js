import BaseSchemeValidator from './BaseSchemeValidator'
import DocumentScheme from 'schemes/DocumentScheme'

class DocumentFormValidator extends BaseSchemeValidator {
    constructor() {
        super(DocumentScheme)
    }
}

export default DocumentFormValidator
