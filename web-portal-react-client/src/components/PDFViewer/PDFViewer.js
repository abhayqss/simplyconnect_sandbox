import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { max } from 'underscore'

import { Document, pdfjs } from 'react-pdf'

import Loader from 'components/Loader/Loader'

import { noop } from 'lib/utils/FuncUtils'

import Pages from './Pages/Pages'
import SinglePage from './SinglePage/SinglePage'

import './PDFViewer.scss'

// Here we manually connect external worker to the rendering process as Create React App don't expose it's webpack API
// https://www.npmjs.com/package/react-pdf#create-react-app
pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`

function PDFViewer(
    {
        src,
        width,
        innerRef,
        className,
        isSinglePageMode,
        onRenderSuccess,
        onPageRenderSuccess,
    }
) {
    const [pages, setPages] = useState([])
    const [pageCount, setPageCount] = useState(0)

    const [isLoading, setLoading] = useState(Boolean(src))
    const [isRendering, setIsRendering] = useState(Boolean(src))

    const _onDocumentLoadSuccess = useCallback(({ numPages }) => {
        setLoading(false)
        setPageCount(numPages)
    }, [])

    const _onPageRenderSuccess = useCallback(page => {
        onPageRenderSuccess(page)

        if (isSinglePageMode) {
            setPages(() => [page])
        } else setPages(prev => [...prev, page])
    }, [isSinglePageMode, onPageRenderSuccess])

    const pageProps = useMemo(() => ({
        pageWidth: width,
        pageCount,
        onPageRenderSuccess: _onPageRenderSuccess
    }), [width, pageCount, _onPageRenderSuccess])

    useEffect(() => {
        const areAllPagesRendered = (
            isSinglePageMode ? pages.length === 1 : pages.length === pageCount
        )

        if (!isLoading && areAllPagesRendered) {
            setIsRendering(false)

            const width = max(
                pages.map(o => o.width)
            )

            const originalWidth = max(
                pages.map(o => o.originalWidth)
            )

            const height = pages.reduce(
                (memo, o) => memo + Math.floor(o.height), 0
            )

            const originalHeight = pages.reduce(
                (memo, o) => memo + Math.floor(o.originalHeight), 0
            )

            onRenderSuccess({
                pages,
                width,
                height,
                pageCount,
                originalWidth,
                originalHeight
            })
        }
    }, [
        pages,
        pageCount,
        isLoading,
        onRenderSuccess,
        isSinglePageMode
    ])

    return (
        <Document
            file={src}
            inputRef={innerRef}

            className={cn('PDFViewer', className)}
            onLoadSuccess={_onDocumentLoadSuccess}
        >
            {isRendering && <Loader hasBackdrop />}
            {isSinglePageMode ? (
                <SinglePage {...pageProps} />
            ) : (
                <Pages {...pageProps} />
            )}
        </Document>
    )
}

PDFViewer.propTypes = {
    src: PTypes.oneOfType([PTypes.string, PTypes.object]),
    width: PTypes.oneOfType([PTypes.number, PTypes.string]),
    innerRef: PTypes.any,
    className: PTypes.string,
    isSinglePageMode: PTypes.bool,
    onRenderSuccess: PTypes.func,
    onPageRenderSuccess: PTypes.func
}

PDFViewer.defaultProps = {
    isSinglePageMode: false,
    onRenderSuccess: noop,
    onPageRenderSuccess: noop
}

export default memo(PDFViewer)
