import React, {
    useRef,
    useState,
    useEffect,
} from 'react'

import PTypes from 'prop-types'

import { saveAs } from 'file-saver'

import { any, noop } from 'underscore'

import { remote } from 'config'

import { Button } from 'reactstrap'
import { Image } from 'react-bootstrap'

import { 
    Loader,
    PDFViewer 
} from 'components'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import { download } from 'lib/utils/AjaxUtils'

import { ReactComponent as Download } from 'images/download.svg'

import './FilePreviewer.scss'

const {
    PDF,
    JPG,
    JPEG,
    PJPG,
    PNG,
    GIF,
} = ALLOWED_FILE_FORMATS

function isPdfMimeType(type) {
    return type === ALLOWED_FILE_FORMAT_MIME_TYPES[PDF]
}

function isImageMimeType(type) {
    return any(
        [JPG, JPEG, PJPG, PNG, GIF],
        format => ALLOWED_FILE_FORMAT_MIME_TYPES[format] === type
    )
}

function isUnsupportedMimeType(type) {
    return !(isPdfMimeType(type) || isImageMimeType(type))
}

function getDataUrl(file) {
    return new Promise((resolve) => {
        const reader = new FileReader()
        reader.onload = () => {
            resolve(reader.result)
        }
        reader.readAsDataURL(file)
    })
}

function FilePreviewer({
    src = {},
    innerRef,
    onRenderSuccess,
    onDownloadSuccess,
    onDownloadFailure,
}) {
    const ref = useRef()

    const [file, setFile] = useState(null)
    const [dataUrl, setDataUrl] = useState(null)
    const [isFetching, setFetching] = useState(false)

    const { path, name, mimeType } = src
    
    let body

    if (isUnsupportedMimeType(mimeType)) {
        body = (
            <div className="FilePreviewer-Fallback">
                <div className="text-center margin-bottom-10">
                    Your browser does not support file with this extension
                </div>
                {file && (
                    <Button
                        outline
                        onClick={() => saveAs(file, name)}
                        className="FilePreviewer-DownloadBtn"
                    >
                        <Download className="FilePreviewer-DownloadBtnIcon"/>&nbsp;Download file
                    </Button>
                )}
            </div>
        )
    } else if (isPdfMimeType(mimeType) && dataUrl) {
        body = (
            <PDFViewer
                width={ref.current?.clientWidth}
                src={dataUrl}
                innerRef={innerRef}
                onRenderSuccess={onRenderSuccess}
                className="FilePreviewer-File FilePreviewer-Pdf"
            />
        )
    } else if (isImageMimeType(mimeType) && dataUrl) {
        body = (
            <Image
                src={dataUrl}
                className="FilePreviewer-File FilePreviewer-Image"
            />
        )
    }

    useEffect(() => {
        if (path && mimeType) {
            setFetching(true)            
            download({ url: remote.url + path, mimeType })
                .then(response => {
                    setFetching(false)
                    setFile(response.body)
                    onDownloadSuccess(response.body)
                })
                .catch(e => {
                    setFetching(false)
                    onDownloadFailure(e)
                })
        }
    }, [
        path,
        mimeType,
        onDownloadSuccess,
        onDownloadFailure
    ])

    useEffect(() => {
        if (file) {
            getDataUrl(file).then(setDataUrl)
        }
    }, [file])

    return (
        <div className="FilePreviewer" ref={ref}>
            {isFetching && (
                <Loader isCentered hasBackdrop/>
            )}
            {body}
        </div>
    )
}

export default FilePreviewer

FilePreviewer.propTypes = {
    className: PTypes.string,
    files: PTypes.arrayOf(PTypes.shape({
        path: PTypes.string,
        url: PTypes.string,
        mimeType: PTypes.string
    }))
}

FilePreviewer.defaultProps = {
    src: {},
    onDownloadSuccess: noop,
    onDownloadFailure: noop
}