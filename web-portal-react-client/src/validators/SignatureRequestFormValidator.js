import Scheme from 'schemes/SignatureRequestScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class SignatureRequestFormValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }
}

export default SignatureRequestFormValidator
