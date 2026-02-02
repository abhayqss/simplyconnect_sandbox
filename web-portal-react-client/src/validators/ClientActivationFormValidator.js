import BaseSchemeValidator from './BaseSchemeValidator'
import ClientActivationFormScheme from 'schemes/ClientActivationFormScheme'

class ClientActivationFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ClientActivationFormScheme)
    }
}

export default ClientActivationFormValidator
