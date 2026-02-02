import VendorScheme from 'schemes/VendorScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class VendorFormSchemeValidator extends BaseSchemeValidator {
    constructor() {
        super(VendorScheme)
    }
}

export default VendorFormSchemeValidator
