import React, {
    memo,
    useMemo
} from 'react'

import Context from './ESignDocumentTemplateContext'

function ESignDocumentTemplateContextProvider(
    {
        step,
        templateId,
        templateData,
        children
    }
) {
    const value = useMemo(
        () => ({ step, templateId, templateData }),
        [step, templateId, templateData]
    )

    return (
        <Context.Provider value={value}>
            {children}
        </Context.Provider>
    )
}

export default memo(ESignDocumentTemplateContextProvider)