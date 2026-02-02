import ReferralInfoRequestScheme from 'schemes/ReferralInfoRequestScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class RequestInfoFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ReferralInfoRequestScheme)
    }
}

export default RequestInfoFormValidator
