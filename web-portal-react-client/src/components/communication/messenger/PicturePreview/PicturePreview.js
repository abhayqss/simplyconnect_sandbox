import React, {
    memo,
    useRef,
    useState,
    useEffect,
} from 'react'

import { saveAs } from 'file-saver'

import cn from 'classnames'

import { UncontrolledTooltip as Tooltip } from 'reactstrap'
import { Image } from 'react-bootstrap'

import { withTooltip } from 'hocs'

import { Modal, Loader } from 'components'

import { ReactComponent as Cross } from 'images/cross.svg'
import { ReactComponent as DownloadIcon } from 'images/download.svg'

import {
    convertImageToBlob
} from 'lib/utils/ConvertationUtils'

import {
    addAbortAllEventListener,
    removeAbortAllEventListener
} from 'lib/utils/AjaxUtils'

import './PicturePreview.scss'

const Download = withTooltip({
    text: 'To download and save an image: Right-click your mouse on the image and select "Save As" option',
    className: 'd-flex'
})(DownloadIcon)

const PictureViewer = ({
    src,
    disabled,

    onClose,
    onDownload
}) => (
    <Modal
        isOpen
        className="PictureViewer"
        renderHeaderButtons={() => {
            return (
                <div className="PictureViewer-Buttons">
                    <Download
                        className='PictureViewer-Button margin-right-20'
                        isTooltipEnabled={disabled}

                        onClick={() => !disabled && onDownload()}
                    />
                    <Cross className="PictureViewer-Button PictureViewer-CrossIcon" onClick={onClose} />
                </div>
            )
        }}
    >
        <Image src={src} />
    </Modal>
)

function PicturePreview({
    url,
    data,
    isLoading,
    className,
    renderIcon,
}) {
    const [isViewerOpen, setIsViewOpen] = useState(false)
    const [isFetching, setIsFetching] = useState(true)

    const [isDownloadable, setDownloadable] = useState(false)

    const imageBlob = useRef()

    useEffect(() => {
        if (url && isViewerOpen)
            convertImageToBlob(url, {
                onSuccess: blob => {
                    setDownloadable(true)

                    imageBlob.current = blob
                }
            })
    }, [url, isViewerOpen])

    let isLoadingInProgress = isFetching || isLoading

    function download() {
        saveAs(imageBlob.current, data.name)
    }

    function onClickPreview(event) {
        event.stopPropagation()

        setIsViewOpen(true)
    }

    return (
        <>
            <div className="PicturePreview-Container" onClick={onClickPreview}>
                <Image
                    src={url}
                    id={data?.sid}
                    className={cn('PicturePreview', className)}
                    onLoad={() => setIsFetching(false)}
                />

                <div className="PicturePreview-AbsolutePositionedIcon">
                    {renderIcon && !isLoadingInProgress && renderIcon()}
                </div>

                {isLoadingInProgress && (
                    <Loader hasBackdrop className="PicturePreview-Loader" />
                )}
            </div>

            {!isLoadingInProgress && data?.sid && (
                <Tooltip
                    placement="top"
                    target={data.sid}
                    trigger="click hover"
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
                    Click to open the file
                </Tooltip>
            )}

            {isViewerOpen && (
                <PictureViewer
                    src={url}
                    onClose={() => setIsViewOpen(false)}
                    onDownload={download}
                    disabled={!isDownloadable}
                />
            )}
        </>
    )
}

export default memo(PicturePreview)
