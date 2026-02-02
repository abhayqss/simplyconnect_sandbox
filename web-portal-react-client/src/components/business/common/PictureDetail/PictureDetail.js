import React, {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { noop, compact } from 'underscore'

import { Image } from 'react-bootstrap'
import { Button, Row, UncontrolledTooltip as Tooltip } from 'reactstrap'

import { Col } from 'components/layout'
import { IconButton, Modal } from 'components/index'

import {
    useDownloadingStatusInfoToast
} from 'hooks/common'

import Converter from 'lib/converters/Converter'
import factory from 'lib/converters/ConverterFactory'

import { getDataUrl } from 'lib/utils/Utils'
import { download } from 'lib/utils/AjaxUtils'

import { ReactComponent as View } from 'images/view.svg'
import { ReactComponent as ZoomIn } from 'images/zoom-in.svg'
import { ReactComponent as Download } from 'images/download.svg'

import './PictureDetail.scss'
import { saveAs } from 'file-saver'


const LAYOUT_SECTION_DIMENSIONS = {
    left: {
        summary: { xs: 6, sm: 6, md: 5, lg: 3 },
        actions: { xs: 6, sm: 6, md: 7, lg: 9 }
    },
    stretch: {
        summary: { xs: 7, sm: 8, md: 7, lg: 6 },
        actions: { xs: 5, sm: 4, md: 5, lg: 6 }
    }
}

const binToBase64converter = factory.getConverter(Converter.types.BINARY_TO_BASE_64)
const dataUrlToFileconverter = factory.getConverter(Converter.types.DATA_URL_TO_FILE)

export default function PictureDetail(
    {
        id,
        path,
        name,
        format,
        date,
        mimeType,

        hasViewBtn,
        canDownload,

        layout,
        renderActions,

        viewHint,
        downloadHint,

        onDownload: onDownloadCb,

        className
    }
) {
    const [dataUrl, setDataUrl] = useState(null)
    const [isViewerOpen, toggleViewer] = useState(false)

    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const dimensions = LAYOUT_SECTION_DIMENSIONS[layout]

    const onDownload = useCallback(() => {
        if (onDownloadCb) {
            onDownloadCb({
                id,
                name,
                format,
                date,
                mimeType
            })
        } else {
            withDownloadingStatusInfoToast(
                () => download({ path, mimeType }).then(({ data } = {}) => {
                    saveAs(dataUrlToFileconverter.convert(
                        getDataUrl(data, mimeType), name
                    ))
                })
            )
        }
    }, [
        id,
        path,
        name,
        format,
        date,
        mimeType,
        onDownloadCb,
        withDownloadingStatusInfoToast
    ])

    const actions = useMemo(() => compact(
        [
            hasViewBtn && (
                <IconButton
                    key={`picture-${id}_view`}
                    name={`picture-${id}_view`}
                    Icon={View}
                    onClick={() => toggleViewer(true)}
                    className="PictureDetail-Action"
                    tipText={viewHint || `View ${name}`}
                />
            ),
            canDownload && (
                <IconButton
                    key={`picture-${id}_download`}
                    name={`picture-${id}_download`}
                    Icon={Download}
                    onClick={onDownload}
                    className="PictureDetail-Action"
                    tipText={downloadHint || `Download ${name}`}
                />
            )
        ]
    ), [
        id,
        name,
        hasViewBtn,
        canDownload,
        onDownload,
        viewHint,
        downloadHint
    ])

    const onCloseViewer = useCallback(() => { toggleViewer(false) }, [])

    useEffect(() => {
        if (path) {
            download({ path, mimeType }).then(({ data } = {}) => {
                setDataUrl(getDataUrl(
                    binToBase64converter.convert(data), mimeType
                ))
            })
        }
    }, [path, name, mimeType])

    return (
        <div className={cn('PictureDetail', className)}>
            <Row>
                <Col {...dimensions.summary} className="PictureDetail-Summary">
                    <div
                        id={`picture-preview-${id}`}
                        className="PictureDetail-PicturePreview"
                        onClick={() => { toggleViewer(true) }}
                    >
                        <Image
                            src={dataUrl}
                            className={cn('PictureDetail-PreviewPicture', className)}
                        />
                        <ZoomIn className="PictureDetail-PicturePreviewZoomIn"/>
                    </div>
                    <Tooltip
                        placement="top"
                        target={`picture-preview-${id}`}
                    >
                        Click to zoom in
                    </Tooltip>
                </Col>
                <Col {...dimensions.actions} className="PictureDetail-Actions">
                    {renderActions ? renderActions(actions) : actions}
                </Col>
            </Row>
            {isViewerOpen && (
                <Modal
                    isOpen
                    title={name}
                    hasFooter={false}
                    className="PictureDetail-PictureViewer"
                    onClose={onCloseViewer}
                >
                    <Image
                        src={dataUrl}
                        className={cn('PictureDetail-PictureViewerPicture', className)}
                    />
                </Modal>
            )}
        </div>
    )
}

PictureDetail.propTypes = {
    name: PTypes.string,
    title: PTypes.string,
    hasViewBtn: PTypes.bool,
    layout: PTypes.oneOf(['left', 'stretch']),
    canDownload: PTypes.bool,
    viewHint: PTypes.string,
    downloadHint: PTypes.string,
    onDownload: PTypes.func,
    className: PTypes.string
}

PictureDetail.defaultProps = {
    layout: 'left',
    hasViewBtn: true,
    canDownload: true
}