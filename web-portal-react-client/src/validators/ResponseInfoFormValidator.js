import ReferralInfoResponseScheme from 'schemes/ReferralInfoResponseScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class RequestInfoFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ReferralInfoResponseScheme)
    }
}

export default RequestInfoFormValidator
