import React, {
    memo,
    useMemo
} from 'react'

import Context from './PaperlessHealthcareThemeContext'

function PaperlessHealthcareThemeContextProvider(
    {
        theme,
        children
    }
) {
    const value = useMemo(
        () => ({ theme }),
        [theme]
    )

    return (
        <Context.Provider value={value}>
            {children}
        </Context.Provider>
    )
}

export default memo(PaperlessHealthcareThemeContextProvider)