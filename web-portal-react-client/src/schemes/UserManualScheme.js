import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import { Shape, stringMax } from './types'

import FileScheme from './FileScheme'

const { DOC, DOCX, PDF } = ALLOWED_FILE_FORMATS

const ALLOWED_FILE_MIME_TYPES = [
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PDF]
]

const specialSymbolsRegex = /\\|\/|:|\*|\?|"|<|>|\|/
const errorMessage = `A file name can’t contain any of the following characters: \\ / : * ? " < >`

const UserManualScheme = Shape({
    title: stringMax(256).test('no-special-symbols', errorMessage, value => !specialSymbolsRegex.test(value)),
    file: FileScheme({ maxMB: 20, allowedTypes: ALLOWED_FILE_MIME_TYPES }).required()
})

export default UserManualScheme
