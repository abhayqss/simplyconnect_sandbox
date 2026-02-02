import Scheme from 'schemes/SignInScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class SignInFormValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }
}

export default SignInFormValidator
