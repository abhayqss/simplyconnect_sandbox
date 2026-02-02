import scheme from 'schemes/ClientRecordFilterScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class ClientRecordFilterValidator extends BaseSchemeValidator {
    constructor() {
        super(scheme)
    }
}

export default ClientRecordFilterValidator
