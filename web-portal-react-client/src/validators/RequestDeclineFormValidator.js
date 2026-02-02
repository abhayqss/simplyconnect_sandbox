import RequestDeclineScheme from 'schemes/RequestDeclineScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class RequestDeclineFormValidator extends BaseSchemeValidator {
    constructor() {
        super(RequestDeclineScheme)
    }
}

export default RequestDeclineFormValidator
