import React, {
  memo,
  useMemo
} from "react";

import { range } from 'underscore'

import { Page } from 'react-pdf'

export default memo(
  function Pages({ pageCount, pageWidth, onPageRenderSuccess }) {
    const pages = useMemo(
      () => range(1, pageCount + 1), [pageCount]
    )

    return (
      <div className="PDFViewer-Pages">
        {pages.map(serialNo => (
          <Page
            key={serialNo}
            width={pageWidth}
            pageNumber={serialNo}
            className="PDFViewer-Page"
            renderTextLayer={false}
            renderAnnotationLayer={false}
            onRenderSuccess={onPageRenderSuccess}
          />
        ))}
      </div>
    )
  }
)
