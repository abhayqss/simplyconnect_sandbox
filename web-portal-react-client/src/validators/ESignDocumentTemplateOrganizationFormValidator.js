import BaseSchemeValidator from './BaseSchemeValidator'
import Scheme from 'schemes/ESignDocumentTemplateOrganizationFormScheme'

class ESignDocumentTemplateFileUploadFormValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }
}

export default ESignDocumentTemplateFileUploadFormValidator
