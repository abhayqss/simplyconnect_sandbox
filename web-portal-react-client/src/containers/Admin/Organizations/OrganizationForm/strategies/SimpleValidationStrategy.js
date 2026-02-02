import BaseValidationStrategy from './BaseValidationStrategy'

import { STEP } from '../Constants'

class SimpleValidationStrategy extends BaseValidationStrategy {
    execute(context) {
        const { step, validate, validateAsync } = context

        let validationsToExecute = [validate({ step })]

        if (step === STEP.LEGAL_INFO) {
            validationsToExecute.push(validateAsync())
        }

        const performValidation = () => Promise.all(validationsToExecute)

        return performValidation()
            .then(() => ({ isValid: true }))
            .catch(() => ({ isValid: false, step }))
    }
}

export default SimpleValidationStrategy
