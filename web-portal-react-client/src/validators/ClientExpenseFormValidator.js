import BaseSchemeValidator from './BaseSchemeValidator'
import ClientExpenseFormScheme from 'schemes/ClientExpenseFormScheme'

class ClientExpenseFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ClientExpenseFormScheme)
    }
}

export default ClientExpenseFormValidator
