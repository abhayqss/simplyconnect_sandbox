import OptInOutPolicyFormScheme from 'schemes/OptInOutPolicyFormScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class HIEConsentPolicyFormValidator extends BaseSchemeValidator {
	constructor() {
		super(OptInOutPolicyFormScheme)
	}
}

export default HIEConsentPolicyFormValidator
