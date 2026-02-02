import BaseSchemeValidator from './BaseSchemeValidator'
import Scheme from 'schemes/ESignDocumentTemplateFieldRuleScheme'

class ESignDocumentTemplateFieldRuleValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }
}

export default ESignDocumentTemplateFieldRuleValidator
