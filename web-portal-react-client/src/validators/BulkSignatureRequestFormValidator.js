import Scheme from 'schemes/BulkSignatureRequestScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class BulkSignatureRequestFormValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }
}

export default BulkSignatureRequestFormValidator
