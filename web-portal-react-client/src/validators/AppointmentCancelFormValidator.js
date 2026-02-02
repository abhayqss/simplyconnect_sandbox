import BaseSchemeValidator from './BaseSchemeValidator'
import AppointmentCancelFormScheme from 'schemes/AppointmentCancelFormScheme'

class AppointmentCancelFormValidator extends BaseSchemeValidator {
    constructor() {
        super(AppointmentCancelFormScheme)
    }
}

export default AppointmentCancelFormValidator
