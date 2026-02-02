import React, {
    memo,
    useMemo
} from 'react'

import Context from './SignatureRequestContext'

function SignatureRequestContextProvider(
    {
        step,
        requestData,
        templateIds,
        templateData,
        children
    }
) {
    const value = useMemo(
        () => ({ step, requestData, templateIds, templateData }),
        [step, requestData, templateIds, templateData]
    )

    return (
        <Context.Provider value={value}>
            {children}
        </Context.Provider>
    )
}

export default memo(SignatureRequestContextProvider)