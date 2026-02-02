import { bool } from 'yup'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import FileScheme from './FileScheme'
import { Shape, stringMax } from './types'

const { DOC, DOCX, PDF } = ALLOWED_FILE_FORMATS

const ALLOWED_FILE_MIME_TYPES = [
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PDF]
]

const ReleaseNoteScheme = Shape({
    file: FileScheme({
        maxMB: 20,
        allowedTypes: ALLOWED_FILE_MIME_TYPES
    }).required(),
    description: stringMax(5000).required(),

    isEmailNotificationEnabled: bool(),
    isInAppNotificationEnabled: bool(),

    features: stringMax(5000).required(),
    fixes: stringMax(5000)
})

export default ReleaseNoteScheme
