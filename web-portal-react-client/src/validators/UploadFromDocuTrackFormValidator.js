import UploadFromDocuTrackScheme from 'schemes/UploadFromDocuTrackScheme'

import BaseSchemeValidator from './BaseSchemeValidator'

class SendDocToDocuTrackFormValidator extends BaseSchemeValidator {
    constructor() {
        super(UploadFromDocuTrackScheme)
    }
}

export default SendDocToDocuTrackFormValidator
