import React, {
    memo,
    useCallback
} from 'react'

import {
    Loader,
    PDFViewer
} from 'components'

import { noop } from 'lib/utils/FuncUtils'

export function DocumentTemplateFilePreviewer(
    {
        data,
        width,
        dataUrl,
        innerRef,
        isLoading,
        
        onRenderSuccess,
    }
) {

    const _onRenderSuccess = useCallback(o => {
        onRenderSuccess(o, data)
    }, [data, onRenderSuccess])

    return (
        <div className="DocumentTemplateFilePreview">
            {isLoading && (
                <Loader isCentered/>
            )}
            {dataUrl && (
                <PDFViewer
                    width={width}
                    src={dataUrl}
                    innerRef={innerRef}
                    onRenderSuccess={_onRenderSuccess}
                    className="DocumentTemplateFilePreview-Pdf"
                />
            )}
        </div>
    )
}

export default memo(DocumentTemplateFilePreviewer)

DocumentTemplateFilePreviewer.defaultProps = {
    onRenderSuccess: noop,
    onDownloadSuccess: noop,
    onDownloadFailure: noop
}