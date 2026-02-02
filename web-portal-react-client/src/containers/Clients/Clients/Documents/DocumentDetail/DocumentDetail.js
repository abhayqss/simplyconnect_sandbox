import React, { useCallback } from 'react'

import cn from 'classnames'

import {
    useDownloadingStatusInfoToast
} from 'hooks/common'

import { useBoundActions } from 'hooks/common/redux'

import { DocumentDetail as Detail } from 'components/business/Documents'

import detailsActions from 'redux/client/document/details/clientDocumentDetailsActions'

import { ALLOWED_FILE_FORMAT_MIME_TYPES } from 'lib/Constants'

import { lc } from 'lib/utils/Utils'
import { path } from 'lib/utils/ContextUtils'

import './DocumentDetail.scss'

export default function DocumentDetail(props) {
    const {
        id,
        title,
        format,
        mimeType,
        clientId,
        clientName,
        className,
    } = props

    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const { download } = useBoundActions(detailsActions)

    const onView = useCallback(() => {
        window.open(path(
            `/clients/${clientId}/documents/${id}-${clientName.replace(' ', '_')}.${lc(format)}`
        ))
    }, [ id, format, clientId, clientName ])

    const onDownload = useCallback(() => {
            withDownloadingStatusInfoToast(() => download({
                clientId,
                documentId: id,
                mimeType: mimeType ?? ALLOWED_FILE_FORMAT_MIME_TYPES[format]
            }))
        },
        [id, format, download, clientId, mimeType, withDownloadingStatusInfoToast]
    )

    return (
        <Detail
            {...props}
            name={title}
            onView={onView}
            onDownload={onDownload}
            className={cn('ClientDocumentDetail', className)}
        />
    )
}
