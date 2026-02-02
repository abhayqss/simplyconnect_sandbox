import { first } from 'underscore'

import UniqInCommunityScheme from 'schemes/UniqInCommunityScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class UniqSSNInCommunityValidator extends BaseSchemeValidator {
    constructor() {
        super(UniqInCommunityScheme)
    }

    formatErrors({ inner }) {
        let validationError = first(inner)

        return {
            message: first(validationError.errors)
        }
    }

    validate(data, options) {
        let scheme = this.scheme(options.field)

        return new Promise((resolve, reject) => {
            scheme.validate(data, {
                strict: true,
                context: options,
                abortEarly: false,
            })
                .then(() => resolve(true))
                .catch(errors => reject(this.formatErrors(errors)))
        })
    }
}

export default UniqSSNInCommunityValidator
