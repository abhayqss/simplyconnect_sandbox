import { Shape, integer } from './types'

import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

const { NUMBER_FORMAT } = VALIDATION_ERROR_TEXTS

const UploadFromDocuTrackScheme = Shape({
    documentId: integer().typeError(NUMBER_FORMAT).required()
})

export default UploadFromDocuTrackScheme
