import { first } from 'underscore'

import UniqInOrganizationScheme from 'schemes/UniqInOrganizationScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class UniqWithinOrganizationValidator extends BaseSchemeValidator {
    constructor() {
        super(UniqInOrganizationScheme)
    }

    formatErrors({ inner }) {
        let validationError = first(inner)

        return {
            message: first(validationError.errors)
        }
    }

    validate(data, options) {
        let { fieldToValidate, errorMessage } = options

        return new Promise((resolve, reject) => {
            this.scheme(fieldToValidate, errorMessage)
                .validate(data, { strict: true, abortEarly: false })
                .then(() => resolve(true))
                .catch(errors => reject(this.formatErrors(errors)))
        })
    }
}

export default UniqWithinOrganizationValidator
