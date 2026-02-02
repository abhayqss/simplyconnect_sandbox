import ReferralRequestAcceptScheme from 'schemes/ReferralRequestAcceptScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class ReferralRequestAcceptValidator extends BaseSchemeValidator {
    constructor() {
        super(ReferralRequestAcceptScheme)
    }

    validate(data, options) {
        return new Promise((resolve, reject) => {
            this.scheme(data).validate(data, {
                abortEarly: false,
                context: options
            })
                .then(() => resolve(true))
                .catch(errors => reject(this.formatErrors(errors)))
        })
    }
}

export default ReferralRequestAcceptValidator
