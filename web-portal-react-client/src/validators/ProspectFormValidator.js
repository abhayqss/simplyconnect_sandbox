import { ProspectScheme } from 'schemes'

import BaseSchemeValidator from './BaseSchemeValidator'

class ProspectFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ProspectScheme)
    }
}

export default ProspectFormValidator
