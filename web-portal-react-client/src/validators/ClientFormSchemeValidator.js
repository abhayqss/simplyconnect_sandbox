import ClientScheme from 'schemes/ClientScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class ClientFormSchemeValidator extends BaseSchemeValidator {
    constructor() {
        super(ClientScheme)
    }
}

export default ClientFormSchemeValidator
