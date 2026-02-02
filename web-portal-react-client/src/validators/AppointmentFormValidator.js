import AppointmentScheme from 'schemes/AppointmentScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class AppointmentFormValidator extends BaseSchemeValidator {
    constructor() {
        super(AppointmentScheme)
    }
}

export default AppointmentFormValidator
