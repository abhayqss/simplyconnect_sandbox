import BaseSchemeValidator from './BaseSchemeValidator'
import ContactToIndividualFormScheme from 'schemes/ContactToIndividualFormScheme'

class ContactToIndividualFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ContactToIndividualFormScheme)
    }
}

export default ContactToIndividualFormValidator
