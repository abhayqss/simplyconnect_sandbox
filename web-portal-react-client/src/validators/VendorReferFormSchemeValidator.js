import VendorReferScheme from 'schemes/VendorReferScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class VendorReferFormSchemeValidator extends BaseSchemeValidator {
    constructor() {
        super(VendorReferScheme)
    }
}

export default VendorReferFormSchemeValidator
