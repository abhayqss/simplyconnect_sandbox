import React, { useState, useEffect, useCallback } from 'react'

import PropTypes from 'prop-types'

import cn from 'classnames'

import { isNumber } from 'underscore'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Image } from 'react-bootstrap'

import { useResponse } from 'hooks/common'

import {
    Button,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { Modal, PDFViewer } from 'components'

import actions from 'redux/incident/picture/details/incidentPictureDetailsActions'

import { getDataUrl } from 'lib/utils/Utils'
import Converter from 'lib/converters/Converter'
import factory from 'lib/converters/ConverterFactory'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import { ReactComponent as ZoomIn } from 'images/zoom-in.svg'
import { ReactComponent as PdfIcon } from 'images/pdf.svg'
import { ReactComponent as UnknownFileIcon } from 'images/file.svg'

import './IncidentPicture.scss'

const { PDF, TIFF } = ALLOWED_FILE_FORMATS

const converter = factory.getConverter(Converter.types.BINARY_TO_BASE_64)

function isImageMimeType(mimeType) {
    return ![
        ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
        ALLOWED_FILE_FORMAT_MIME_TYPES[TIFF],
    ].includes(mimeType)
}

function isPdfMimeType(mimeType) {
    return mimeType === ALLOWED_FILE_FORMAT_MIME_TYPES[PDF]
}

function isTiffMimeType(mimeType) {
    return mimeType === ALLOWED_FILE_FORMAT_MIME_TYPES[TIFF]
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch),
    }
}

const propTypes = {
    id: PropTypes.number,
    name: PropTypes.string,
    className: PropTypes.string
}

function IncidentPicture(
    {
        id,
        name,
        actions,
        style,
        className
    }
) {
    const [url, setUrl] = useState(null)
    const [mimeType, setMimeType] = useState(null)
    const [isViewerOpen, toggleViewer] = useState(false)

    const onResponse = useResponse({
        onSuccess: useCallback(({ data, mediaType }) => {
            setMimeType(mediaType)
            setUrl(getDataUrl(converter.convert(data), mediaType))
        }, [])
    })

    const onCloseViewer = useCallback(() => { toggleViewer(false) }, [])

    useEffect(() => {
        if (isNumber(id)) {
            actions.load(id).then(onResponse)
        }
    }, [id, actions, onResponse])

    return (
        <>
            <div
                id={`incident-picture-preview-${id}`}
                className="IncidentPicturePreview"
                onClick={() => { toggleViewer(true) }}
            >
                {isPdfMimeType(mimeType) && (
                    <>
                        <PdfIcon
                            style={style}
                            className={cn('IncidentPicture', className)}
                        />
                        <ZoomIn className="IncidentPicturePreview-ZoomIn"/>
                    </>
                )}
                {isTiffMimeType(mimeType) && (
                    <>
                        <UnknownFileIcon
                            style={style}
                            className={cn('IncidentPicture', className)}
                        />
                    </>
                )}
                {isImageMimeType(mimeType) && (
                    <>
                        <Image
                            src={url}
                            style={style}
                            className={cn('IncidentPicture', className)}
                        />
                        <ZoomIn className="IncidentPicturePreview-ZoomIn"/>
                    </>
                )}
            </div>
            <Tooltip
                placement="top"
                target={`incident-picture-preview-${id}`}
                modifiers={[
                    {
                        name: 'offset',
                        options: { offset: [0, 6] }
                    },
                    {
                        name: 'preventOverflow',
                        options: { boundary: document.body }
                    }
                ]}
            >
                Click to zoom in
            </Tooltip>
            {isViewerOpen && (
                <Modal
                    isOpen
                    title={name}
                    hasCloseBtn={false}
                    className="IncidentPictureViewer"
                    renderFooter={() => (
                        <Button
                            color="success"
                            onClick={onCloseViewer}
                        >
                            Close
                        </Button>
                    )}
                >
                    {mimeType === ALLOWED_FILE_FORMAT_MIME_TYPES[PDF] ? (
                        <PDFViewer src={url} />
                    ) : (
                        <Image
                            src={url}
                            style={{
                                ...style,
                                ...isViewerOpen ? {
                                    width: 'auto',
                                    height: 'auto'
                                } : {}
                            }}
                            className={cn('IncidentPicture', className)}
                        />
                    )}
                </Modal>
            )}
        </>
    )
}

IncidentPicture.propTypes = propTypes

export default connect(null, mapDispatchToProps)(IncidentPicture)
