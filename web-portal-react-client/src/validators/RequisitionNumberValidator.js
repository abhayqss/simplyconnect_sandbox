import { first } from 'underscore'

import RequisitionNumberScheme from 'schemes/RequisitionNumberScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class RequisitionNumberValidator extends BaseSchemeValidator {
    constructor() {
        super(RequisitionNumberScheme)
    }

    formatErrors({ inner }) {
        let validationError = first(inner)

        return {
            message: first(validationError.errors)
        }
    }

    validate(data, options) {
        let { validateRemotely } = options
        let scheme = this.scheme(validateRemotely)

        return new Promise((resolve, reject) => {
            scheme.validate(data, {
                abortEarly: false,
                context: options
            })
                .then(() => resolve(true))
                .catch(errors => reject(this.formatErrors(errors)))
        })
    }
}

export default RequisitionNumberValidator
