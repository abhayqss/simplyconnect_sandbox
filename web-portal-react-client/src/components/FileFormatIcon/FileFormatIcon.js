import React from 'react'

import { ALLOWED_FILE_FORMATS } from 'lib/Constants'
import { getFileFormatByMimeType } from 'lib/utils/Utils'

import { ReactComponent as PdfIcon } from 'images/pdf.svg'
import { ReactComponent as PngIcon } from 'images/png.svg'
import { ReactComponent as JpgIcon } from 'images/jpg.svg'
import { ReactComponent as GifIcon } from 'images/gif.svg'
import { ReactComponent as TxtIcon } from 'images/txt.svg'
import { ReactComponent as DocIcon } from 'images/doc.svg'
import { ReactComponent as XlsIcon } from 'images/xls.svg'
import { ReactComponent as ZipIcon } from 'images/zip.svg'
import { ReactComponent as XmlIcon } from 'images/xml.svg'
import {ReactComponent as FolderIcon} from "images/folder-3.svg";

import { ReactComponent as UnknownFileIcon } from 'images/file.svg'

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
    XML,
    FOLDER
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
    [XML]: XmlIcon,
    [FOLDER]:FolderIcon
}

export default function FileFormatIcon({ format, mimeType, ...props }) {
    let Icon = UnknownFileIcon

    if (format) {
        Icon = FORMAT_ICONS[format] || UnknownFileIcon
    }

    if (mimeType) {
        Icon = FORMAT_ICONS[getFileFormatByMimeType(mimeType)] || UnknownFileIcon
    }

    return <Icon data-testid={props.name} {...props} />
}
