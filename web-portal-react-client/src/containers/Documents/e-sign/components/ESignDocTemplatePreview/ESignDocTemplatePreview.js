import React, {
    memo,
} from 'react'

import {
    Loader,
    PDFViewer
} from 'components'

import { noop } from 'lib/utils/FuncUtils'

export function ESignDocTemplatePreview(
    {
        width,
        dataUrl,
        innerRef,
        isLoading,
        isSinglePageMode,

        onRenderSuccess,
    }
) {
    return (
        <div className="ESignDocTemplatePreview">
            {isLoading && (
                <Loader isCentered/>
            )}
            {dataUrl && (
                <PDFViewer
                    width={width}
                    src={dataUrl}
                    innerRef={innerRef}
                    onRenderSuccess={onRenderSuccess}
                    isSinglePageMode={isSinglePageMode}
                    className="ESignDocTemplatePreview-Pdf"
                />
            )}
        </div>
    )
}

export default memo(ESignDocTemplatePreview)

ESignDocTemplatePreview.defaultProps = {
    onRenderSuccess: noop,
    onDownloadSuccess: noop,
    onDownloadFailure: noop
}