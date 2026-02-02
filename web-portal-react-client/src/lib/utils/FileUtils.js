import {
	ALLOWED_FILE_FORMATS,
	ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import { ReactComponent as PdfIcon } from 'images/pdf.svg'
import { ReactComponent as PngIcon } from 'images/png.svg'
import { ReactComponent as JpgIcon } from 'images/jpg.svg'
import { ReactComponent as GifIcon } from 'images/gif.svg'
import { ReactComponent as TxtIcon } from 'images/txt.svg'
import { ReactComponent as DocIcon } from 'images/doc.svg'
import { ReactComponent as XlsIcon } from 'images/xls.svg'
import { ReactComponent as ZipIcon } from 'images/zip.svg'
import { ReactComponent as XmlIcon } from 'images/xml.svg'
import _ from 'underscore'

const NAME_RG = /(.+)\.(\w+)$/
const match = fileName => NAME_RG.exec(fileName) ?? ['', fileName ?? '', '']

export function getFileFormatByMimeType(mime) {
	return _.findKey(ALLOWED_FILE_FORMAT_MIME_TYPES, v => v === mime)
}

export const getFileBaseName = name => match(name)[1]
export const getFileExtension = name => match(name)[2]

export function convertBlobToFile(blob, fileBaseName, mimeType) {
	const type = blob.type || mimeType
	const format = getFileFormatByMimeType(mimeType)
	const fileName = `${getFileBaseName(fileBaseName)}.${format?.toLowerCase()}`
	return new File([blob], fileName, { type })
}

export default {
	getFileBaseName,
	getFileExtension,
}

const {
	PDF,
	PNG,
	JPG,
	JPEG,
	TXT,
	DOC,
	DOCX,
	XLS,
	XLSX,
	GIF,
	ZIP,
	XML
} = ALLOWED_FILE_FORMATS

const FORMAT_ICONS = {
	[PDF]: PdfIcon,
	[PNG]: PngIcon,
	[JPG]: JpgIcon,
	[JPEG]: JpgIcon,
	[GIF]: GifIcon,
	[TXT]: TxtIcon,
	[DOC]: DocIcon,
	[DOCX]: DocIcon,
	[XLS]: XlsIcon,
	[XLSX]: XlsIcon,
	[ZIP]: ZipIcon,
	[XML]: XmlIcon
}

//@deprecated
export function getIconComponent({ format, mimeType }) {
	switch (true) {
		case !!format:
			return FORMAT_ICONS[format]

		case !!mimeType:
			return FORMAT_ICONS[getFileFormatByMimeType(mimeType)]
	}
}