import { first } from 'underscore'

import UniqFolderNameScheme from 'schemes/UniqFolderNameScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class UniqFolderNameValidator extends BaseSchemeValidator {
    constructor() {
        super(UniqFolderNameScheme)
    }

    formatErrors({ inner }) {
        let validationError = first(inner)

        return {
            message: first(validationError.errors)
        }
    }

    validate(data, options) {
        return new Promise((resolve, reject) => {
            this.scheme(options.field)
                .validate(data, { strict: true, abortEarly: false })
                .then(() => resolve(true))
                .catch(errors => reject(this.formatErrors(errors)))
        })
    }
}

export default UniqFolderNameValidator
