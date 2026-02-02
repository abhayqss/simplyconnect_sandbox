import { first } from 'underscore'

import UniqOrganizationCategoryScheme from 'schemes/UniqOrganizationCategoryScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class UniqWithinOrganizationValidator extends BaseSchemeValidator {
    constructor() {
        super(UniqOrganizationCategoryScheme)
    }

    formatErrors({ inner }) {
        let validationError = first(inner)

        return {
            message: first(validationError.errors)
        }
    }

    validate(data, options) {
        let { fieldToValidate } = options

        return new Promise((resolve, reject) => {
            this.scheme(fieldToValidate)
                .validate(data, { strict: true, abortEarly: false })
                .then(() => resolve(true))
                .catch(errors => reject(this.formatErrors(errors)))
        })
    }
}

export default UniqWithinOrganizationValidator
