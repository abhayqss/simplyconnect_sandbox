import React, { useCallback } from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
    useDownloadingStatusInfoToast
} from 'hooks/common'

import {
    useProspectDocumentDownload
} from 'hooks/business/Prospects/Documents'

import { DocumentDetail as Detail } from 'components/business/Documents'

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
        prospectId,
        prospectName,
        className
    } = props

    const {
        withDownloadingStatusInfoToast
    } = useDownloadingStatusInfoToast()

    const [download] = useProspectDocumentDownload({
        prospectId,
        documentId: id,
        mimeType: mimeType ?? ALLOWED_FILE_FORMAT_MIME_TYPES[format]
    })

    const onView = useCallback(() => {
        window.open(path(
            `/prospects/${prospectId}/documents/${id}-${prospectName.replace(' ', '_')}.${lc(format)}`
        ))
    }, [ id, format, prospectId, prospectName ])

    const onDownload = useCallback(() => {
        withDownloadingStatusInfoToast(() => download())
    }, [download, withDownloadingStatusInfoToast])

    return (
        <Detail
            {...props}
            name={title}
            onView={onView}
            onDownload={onDownload}
            className={cn('ProspectDocumentDetail', className)}
        />
    )
}

DocumentDetail.propTypes = {
    id: PTypes.oneOfType([PTypes.string, PTypes.number]),
    title: PTypes.string,
    format: PTypes.string,
    mimeType: PTypes.string,
    prospectId: PTypes.oneOfType([PTypes.string, PTypes.number]),
    prospectName: PTypes.string,
    className: PTypes.string
}