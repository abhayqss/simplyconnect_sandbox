import { first } from 'underscore'

import UniqEmailScheme from 'schemes/UniqEmailScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class RequisitionNumberValidator extends BaseSchemeValidator {
    constructor() {
        super(UniqEmailScheme)
    }

    formatErrors({ inner }) {
        let validationError = first(inner)

        return {
            message: first(validationError.errors)
        }
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

export default RequisitionNumberValidator
