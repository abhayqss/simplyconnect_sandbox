import scheme from 'schemes/AddToGroupConversationScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class AddToGroupConversationFormValidator extends BaseSchemeValidator {
    constructor() {
        super(scheme)
    }
}

export default AddToGroupConversationFormValidator
