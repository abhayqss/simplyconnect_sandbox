import BaseSchemeValidator from './BaseSchemeValidator'
import Scheme from 'schemes/ESignDocumentTemplateFileScheme'

class ESignDocumentTemplateFileUploadFormValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }
}

export default ESignDocumentTemplateFileUploadFormValidator
