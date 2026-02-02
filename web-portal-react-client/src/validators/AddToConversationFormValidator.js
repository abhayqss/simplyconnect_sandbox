import scheme from 'schemes/AddToConversationScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class AddToConversationFormValidator extends BaseSchemeValidator {
    constructor() {
        super(scheme)
    }
}

export default AddToConversationFormValidator
