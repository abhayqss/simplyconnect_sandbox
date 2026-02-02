import BaseSchemeValidator from './BaseSchemeValidator'
import ClientDocumentScheme from 'schemes/ClientDocumentScheme'

class ClientDocumentFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ClientDocumentScheme)
    }
}

export default ClientDocumentFormValidator
