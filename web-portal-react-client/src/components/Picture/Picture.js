import React, {
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { Image } from 'react-bootstrap'

import { Modal } from 'components'

import { getDataUrl } from 'lib/utils/Utils'
import { download } from 'lib/utils/AjaxUtils'

import Factory from 'lib/converters/ConverterFactory'
import Converter from 'lib/converters/Converter'

import './Picture.scss'
import ClientSummaryFallback
    from '../../containers/Clients/Clients/ClientDashboard/ClientSummaryFallback/ClientSummaryFallback'

const binToBase64converter = Factory.getConverter(Converter.types.BINARY_TO_BASE_64)

export default function Picture(
    {
        name,
        path,
        mimeType,
        className,
        hasViewer,
        renderFallback,
        ...props
    }
) {
    const [dataUrl, setDataUrl] = useState(null)
    const [isViewerOpen, toggleViewer] = useState(false)

    const onOpenViewer = useCallback(() => { toggleViewer(true) }, [])

    const onCloseViewer = useCallback(() => { toggleViewer(false) }, [])

    useEffect(() => {
        if (path) {
            download({ path, mimeType }).then(({ data, mediaType } = {}) => {
                data && setDataUrl(getDataUrl(
                    binToBase64converter.convert(data), mimeType || mediaType
                ))
            }).catch(e => {
                console.log('Cannot download picture.', e)
            })
        }
    }, [path, mimeType])

    return (
        <>
            {dataUrl && (
                <Image
                    src={dataUrl}
                    className={cn(
                        'Picture',
                        { 'cursor-zoom-in': hasViewer },
                        className
                    )}
                    onClick={onOpenViewer}
                    {...props}
                />
            )}

            {!dataUrl && renderFallback && (
                <div className="Picture-Fallback">
                    {renderFallback()}
                </div>
            )}

            {hasViewer && isViewerOpen && (
                <Modal
                    isOpen
                    title={name}
                    hasFooter={false}
                    className="Picture-Viewer"
                    onClose={onCloseViewer}
                >
                    <Image
                        src={dataUrl}
                        className={cn('Picture-ViewerPicture')}
                    />
                </Modal>
            )}
        </>
    )
}