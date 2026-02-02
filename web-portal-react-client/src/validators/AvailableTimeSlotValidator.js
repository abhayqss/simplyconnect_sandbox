import Scheme from 'schemes/AvailableTimeSlotScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class AvailableTimeSlotValidator extends BaseSchemeValidator {
    constructor() {
        super(Scheme)
    }

    formatErrors(errors) {
        return errors.message
    }

    validate(data) {
        return new Promise((resolve, reject) => {
            this.scheme.validate(data, {
                strict: true,
                abortEarly: false,
            })
                .then(() => resolve(true))
                .catch(errors => reject(this.formatErrors(errors)))
        })
    }
}

export default AvailableTimeSlotValidator
