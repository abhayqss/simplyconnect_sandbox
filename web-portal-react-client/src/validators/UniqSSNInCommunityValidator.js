import { first } from 'underscore'

import UniqSSNInCommunityScheme from 'schemes/UniqSSNInCommunityScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class UniqInCommunityValidator extends BaseSchemeValidator {
    constructor() {
        super(UniqSSNInCommunityScheme)
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

export default UniqInCommunityValidator
