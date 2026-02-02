import BaseSchemeValidator from './BaseSchemeValidator'
import DocumentFolderScheme from 'schemes/DocumentFolderScheme'

class DocumentFolderFormValidator extends BaseSchemeValidator {
    constructor() {
        super(DocumentFolderScheme)
    }
}

export default DocumentFolderFormValidator
