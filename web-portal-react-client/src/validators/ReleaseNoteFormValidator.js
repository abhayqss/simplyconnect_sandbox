import BaseSchemeValidator from './BaseSchemeValidator'
import ReleaseNoteScheme from 'schemes/ReleaseNoteScheme'

class ReleaseNoteFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ReleaseNoteScheme)
    }
}

export default ReleaseNoteFormValidator
