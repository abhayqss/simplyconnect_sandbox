import BaseSchemeValidator from './BaseSchemeValidator'
import Scheme from 'schemes/ESignDocumentTemplateFolderAssignerScheme'

class ESignDocumentTemplateFolderAssignerValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }
}

export default ESignDocumentTemplateFolderAssignerValidator