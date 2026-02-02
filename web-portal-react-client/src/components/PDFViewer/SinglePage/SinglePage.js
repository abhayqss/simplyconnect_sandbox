import React, {
    memo,
    useState,
    useMemo,
    useEffect,
    useCallback
} from 'react'

import { Page } from 'react-pdf'

import IconButton from 'components/IconButton/IconButton'

import { ReactComponent as Prev } from 'images/arrow-prev.svg'
import { ReactComponent as Next } from 'images/arrow-next.svg'

import './SinglePage.scss'

function SinglePage({ pageCount, pageWidth, onPageRenderSuccess }) {
    const [serialNo, setSerialNo] = useState(1)

    const isNextPageDisabled = useMemo(() => {
        return serialNo >= pageCount
    }, [serialNo, pageCount])

    const isPreviousPageDisabled = useMemo(() => {
        return serialNo <= 1
    }, [serialNo])

    const previousPage = useCallback(() => {
        if (!isPreviousPageDisabled) {
            setSerialNo(o => o - 1)
        }
    }, [isPreviousPageDisabled])

    const nextPage = useCallback(() => {
        if (!isNextPageDisabled) {
            setSerialNo(o => o + 1)
        }
    }, [isNextPageDisabled])

    useEffect(() => {
        setSerialNo(1)
    }, [pageCount])

    return pageCount ? (
        <div className="SinglePage">
            <Page
                key={serialNo}
                width={pageWidth}
                pageNumber={serialNo}
                renderTextLayer={false}
                renderAnnotationLayer={false}
                onRenderSuccess={onPageRenderSuccess}
            />
            <div className="Pagination">
                <IconButton
                    size={22}
                    Icon={Prev}
                    disabled={isPreviousPageDisabled}
                    shouldHighLight={false}
                    name="previous"
                    tipText="Previous Page"
                    onClick={previousPage}
                    tipTrigger="hover"
                    className="Pagination-Previous"
                />
                <p className="Pagination-Text">
                    Page {serialNo || (pageCount ? 1 : '--')} / {pageCount || '--'}
                </p>
                <IconButton
                    size={22}
                    Icon={Next}
                    disabled={isNextPageDisabled}
                    shouldHighLight={false}
                    name="next"
                    tipText="Next page"
                    onClick={nextPage}
                    tipTrigger="hover"
                    className="Pagination-Next"
                />

            </div>
        </div>
    ) : null
}

export default memo(SinglePage)
