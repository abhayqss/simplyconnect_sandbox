import BaseValidationStrategy from './BaseValidationStrategy'

import { STEP } from '../Constants'

class RegularValidationStrategy extends BaseValidationStrategy {
    execute(context) {
        const { included, validate, validateAsync } = context
        const { step } = included

        const performValidation = () => validate({ included })
            .then(() => {
                if (step === STEP.SETTINGS) {
                    return validateAsync({ included })
                } else {
                    return true
                }
            })

        return performValidation()
            .then(() => ({ isValid: true }))
            .catch(() => ({ isValid: false, step }))
    }
}

export default RegularValidationStrategy
