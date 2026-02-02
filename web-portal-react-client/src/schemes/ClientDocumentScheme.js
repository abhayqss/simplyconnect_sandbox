import { object } from 'yup'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import { getFileExtension } from 'lib/utils/FileUtils'

import { Shape, string, ListOf, stringMax } from './types'

import FileScheme from './FileScheme'

const { DOC, DOCX, PDF, XLS, XLSX, TXT, JPEG, JPG, PNG, TIFF, GIF } = ALLOWED_FILE_FORMATS

const ALLOWED_FILE_MIME_TYPES = [
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
    ALLOWED_FILE_FORMAT_MIME_TYPES[XLS],
    ALLOWED_FILE_FORMAT_MIME_TYPES[XLSX],
    ALLOWED_FILE_FORMAT_MIME_TYPES[TXT],
    ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
    ALLOWED_FILE_FORMAT_MIME_TYPES[JPEG],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
    ALLOWED_FILE_FORMAT_MIME_TYPES[TIFF],
    ALLOWED_FILE_FORMAT_MIME_TYPES[GIF]
]

const ALLOWED_FILE_FORMAT_LIST = [DOC, DOCX, PDF, XLS, XLSX, TXT, JPEG, JPG, PNG, TIFF, GIF]

const specialSymbolsRegex = /\\|\/|:|\*|\?|"|<|>|\|/
const errorMessage = `A file name can’t contain any of the following characters: \\ / : * ? " < >`

const DocumentScheme = Shape({
    title: stringMax(256).test('no-special-symbols', errorMessage, value => !specialSymbolsRegex.test(value)),
    description: string().nullable(),
    categoryIds: ListOf(string()),
    document: object().when(
        ['$included'], (included, scheme, { value }) => (
            included.isEditing ? scheme.nullable() : FileScheme({
                maxMB: 20,
                format: getFileExtension(value?.name),
                allowedTypes: ALLOWED_FILE_MIME_TYPES,
                allowedFormats: ALLOWED_FILE_FORMAT_LIST,
            }).required()
        )
    ),
    sharingOption: string().when(
        ['$included'], (included, scheme) => (
            included.isEditing ? scheme : scheme.required()
        )
    )
})

export default DocumentScheme
