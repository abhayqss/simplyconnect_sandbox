import scheme from 'schemes/AuditLogFilterScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class AuditLogFilterValidator extends BaseSchemeValidator {
    constructor() {
        super(scheme)
    }
}

export default AuditLogFilterValidator
