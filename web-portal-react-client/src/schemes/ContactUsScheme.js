import { Shape, ListOf, string, integer, phoneNumber } from './types'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES,
} from 'lib/Constants'

import FileScheme from './FileScheme'
import { object } from 'yup'
import { getFileExtension } from '../lib/utils/FileUtils'

const {
    PDF,
    PNG,
    JPG,
    JPEG,
    GIF,
    TIFF,
    DOC,
    DOCX,
} = ALLOWED_FILE_FORMATS

const ALLOWED_FILE_FORMAT_LIST = [PDF, PNG, JPG, JPEG, GIF, TIFF, DOC, DOCX]

const ALLOWED_FILE_MIME_TYPES = ALLOWED_FILE_FORMAT_LIST.map(
    type => ALLOWED_FILE_FORMAT_MIME_TYPES[type]
)

const ContactUsScheme = Shape({
    phone: phoneNumber().nullable().required(),
    typeId: integer().required(),
    messageText: string().required(),
    attachmentFiles: ListOf(
        object().when(value => (
                FileScheme({
                    maxMB: 20,
                    format: getFileExtension(value?.name),
                    allowedTypes: ALLOWED_FILE_MIME_TYPES,
                    allowedFormats: ALLOWED_FILE_FORMAT_LIST
                })
            )
        )
    )
})

export default ContactUsScheme
