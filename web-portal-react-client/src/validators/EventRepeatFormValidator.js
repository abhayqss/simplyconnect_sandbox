import EventRepeatScheme from 'schemes/EventRepeatScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class EventRepeatFormValidator extends BaseSchemeValidator {
    constructor() {
        super(EventRepeatScheme)
    }
}

export default EventRepeatFormValidator
