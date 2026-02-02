import React, {
    useMemo,
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { noop, compact } from 'underscore'

import { saveAs } from 'file-saver'

import { Row } from 'reactstrap'

import { Col } from 'components/layout'
import { IconButton } from 'components/buttons'

import {
    useDownloadingStatusInfoToast
} from 'hooks/common'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import {
    uc,
    DateUtils,
    getFileFormatByMimeType
} from 'lib/utils/Utils'

import { download } from 'lib/utils/AjaxUtils'
import { convertBase64ToFile } from 'lib/utils/ConvertationUtils'

import { ReactComponent as View } from 'images/view.svg'
import { ReactComponent as Download } from 'images/download.svg'

import { ReactComponent as PdfIcon } from 'images/pdf.svg'
import { ReactComponent as PngIcon } from 'images/png.svg'
import { ReactComponent as JpgIcon } from 'images/jpg.svg'
import { ReactComponent as GifIcon } from 'images/gif.svg'
import { ReactComponent as TxtIcon } from 'images/txt.svg'
import { ReactComponent as DocIcon } from 'images/doc.svg'
import { ReactComponent as XlsIcon } from 'images/xls.svg'
import { ReactComponent as ZipIcon } from 'images/zip.svg'
import { ReactComponent as XmlIcon } from 'images/xml.svg'
import { ReactComponent as FileIcon } from 'images/file.svg'

import './DocumentDetail.scss'

const { format, formats } = DateUtils

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
    XML,
    ZIP,
    X_ZIP,
    X_ZIP_COMPRESSED
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
    [XML]: XmlIcon,
    [ZIP]: ZipIcon,
    [X_ZIP]: ZipIcon,
    [X_ZIP_COMPRESSED]: ZipIcon
}

const LAYOUT_SECTION_DIMENSIONS = {
    left: {
        summary: { sm: 6, md: 5, lg: 3 },
        actions: { sm: 6, md: 7, lg: 9 }
    },
    stretch: {
        summary: { xs: 7, sm: 8, md: 7, lg: 6 },
        actions: { xs: 5, sm: 4, md: 5, lg: 6 }
    }
}

const formatDate = date => format(date, formats.americanMediumDate)

function DocumentDetail(
    {
        id,
        name,
        title,
        format,
        date,
        path,
        mimeType,
        isFormatIconClickable,

        canView,
        canDownload,

        layout,
        renderInfo,
        renderActions,

        viewHint,
        downloadHint,

        onView,
        onDownload: onDownloadCb,

        className
    }
) {
    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    format = format ?? getFileFormatByMimeType(mimeType)
    mimeType = mimeType ?? ALLOWED_FILE_FORMAT_MIME_TYPES[uc(format)]

    const Icon = FORMAT_ICONS[format || getFileFormatByMimeType(mimeType)] || FileIcon

    const dimensions = LAYOUT_SECTION_DIMENSIONS[layout]

    const onDownload = useCallback(() => {
        if (onDownloadCb) {
            onDownloadCb({
                name,
                title,
                format,
                date,
                mimeType
            })
        } else {
            withDownloadingStatusInfoToast(
                download({ path, mimeType }).then(({ data } = {}) => {
                    saveAs(convertBase64ToFile(data, name, mimeType))
                })
            )
        }
    }, [
        path,
        name,
        title,
        format,
        date,
        mimeType,
        onDownloadCb,
        withDownloadingStatusInfoToast
    ])

    const actions = useMemo(() => compact(
        [
            canView && (
                <IconButton
                    key={`doc-${id}_view`}
                    name={`doc-${id}_view`}
                    Icon={View}
                    onClick={onView}
                    className="DocumentDetail-Action"
                    tipText={viewHint || `View ${title || name}`}
                />
            ),
            canDownload && (
                <IconButton
                    key={`doc-${id}_download`}
                    name={`doc-${id}_download`}
                    Icon={Download}
                    onClick={onDownload}
                    className="DocumentDetail-Action"
                    tipText={downloadHint || `Download ${title || name}`}
                />
            )
        ]
    ), [
        id,
        name,
        title,
        canView,
        canDownload,
        onView,
        onDownload,
        viewHint,
        downloadHint
    ])

    return (
        <div
            data-testid={id}
            className={cn('DocumentDetail', className)}
        >
            <Row>
                <Col {...dimensions.summary} className="DocumentDetail-Summary">
                    <div className="d-flex flex-row align-items-center">
                        <Icon
                            {...isFormatIconClickable && {
                                onClick: onDownload,
                                style: { cursor: 'pointer' }
                            }}
                            className='DocumentDetail-FormatIcon'
                        />
                        <div className='flex-1'>
                            {renderInfo ? renderInfo({ id, name, title, format, date, mimeType }) : (
                                <>
                                    <div className='DocumentDetail-Title'>
                                        {title || name}
                                    </div>
                                    {date && (
                                        <div className='DocumentDetail-Date'>
                                            {formatDate(date)}
                                        </div>
                                    )}
                                </>
                            )}
                        </div>
                    </div>
                </Col>
                <Col {...dimensions.actions} className="DocumentDetail-Actions">
                    {renderActions ? renderActions(actions) : actions}
                </Col>
            </Row>
        </div>
    )
}

DocumentDetail.propTypes = {
    id: PTypes.oneOfType([PTypes.number, PTypes.string]),
    name: PTypes.string,
    title: PTypes.string,
    format: PTypes.string,
    mimeType: PTypes.string,
    canView: PTypes.bool,
    layout: PTypes.oneOf(['left', 'stretch']),
    isFormatIconClickable: PTypes.bool,
    canDownload: PTypes.bool,
    viewHint: PTypes.string,
    downloadHint: PTypes.string,
    onView: PTypes.func,
    onDownload: PTypes.func,
    className: PTypes.string
}

DocumentDetail.defaultProps = {
    layout: 'left',
    canView: true,
    onView: noop,
    canDownload: true,
    isFormatIconClickable: false
}

export default DocumentDetail
