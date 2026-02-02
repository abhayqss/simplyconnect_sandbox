import { object } from 'yup'

import {
	ALLOWED_FILE_FORMATS,
	ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import { Shape } from './types'

import FileScheme from './FileScheme'

const { DOC, DOCX, PDF } = ALLOWED_FILE_FORMATS

const ALLOWED_FILE_MIME_TYPES = [
	ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
	ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
	ALLOWED_FILE_FORMAT_MIME_TYPES[PDF]
]

const Scheme = Shape({
	file: object().when(
		['$included'], (included, scheme) => (
			included.isEditing ? scheme.nullable() : FileScheme({
				maxMB: 20,
				allowedTypes: ALLOWED_FILE_MIME_TYPES
			}).required()
		)
	)
})

export default Scheme
