import ReferralStatusScheme from 'schemes/ReferralStatusScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class ReferralStatusFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ReferralStatusScheme)
    }
}

export default ReferralStatusFormValidator
