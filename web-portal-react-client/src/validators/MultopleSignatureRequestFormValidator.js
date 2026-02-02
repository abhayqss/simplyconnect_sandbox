import Scheme from 'schemes/MultipleSignatureRequestScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class MultipleSignatureRequestFormValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }
}

export default MultipleSignatureRequestFormValidator
