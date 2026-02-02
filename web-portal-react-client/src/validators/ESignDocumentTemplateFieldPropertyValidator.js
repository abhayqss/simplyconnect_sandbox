import BaseSchemeValidator from './BaseSchemeValidator'
import Scheme from 'schemes/ESignDocumentTemplateFieldPropertyScheme'

class ESignDocumentTemplateFieldPropertyValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }
}

export default ESignDocumentTemplateFieldPropertyValidator
