import BaseSchemeValidator from './BaseSchemeValidator'
import UserManualScheme from 'schemes/UserManualScheme'

class UserManualFormValidator extends BaseSchemeValidator {
    constructor() {
        super(UserManualScheme)
    }
}

export default UserManualFormValidator
