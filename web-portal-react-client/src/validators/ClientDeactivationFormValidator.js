import BaseSchemeValidator from './BaseSchemeValidator'
import ClientDeactivationFormScheme from 'schemes/ClientDeactivationFormScheme'

class ClientDeactivationFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ClientDeactivationFormScheme)
    }
}

export default ClientDeactivationFormValidator
