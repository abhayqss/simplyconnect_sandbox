import BaseSchemeValidator from './BaseSchemeValidator'
import InquiryFormScheme from 'schemes/InquiryFormScheme'

class InquiryFormValidator extends BaseSchemeValidator {
    constructor() {
        super(InquiryFormScheme)
    }
}

export default InquiryFormValidator
