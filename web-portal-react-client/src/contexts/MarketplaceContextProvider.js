import React, { memo, useRef } from 'react'

import { noop } from 'underscore'

import MarketplaceContext from './MarketplaceContext'

function MarketplaceContextProvider({ children }) {
    const ref = useRef({
        updateList: noop
    })

    return (
        <MarketplaceContext.Provider value={ref}>
            {children}
        </MarketplaceContext.Provider>
    )
}

export default memo(MarketplaceContextProvider)