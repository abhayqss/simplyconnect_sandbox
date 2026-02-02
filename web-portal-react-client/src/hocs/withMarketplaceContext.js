import React from 'react'

import { MarketplaceContextProvider } from 'contexts'

function withMarketplaceContext(Component) {
    return function(props) {
        return (
            <MarketplaceContextProvider>
                <Component {...props} />
            </MarketplaceContextProvider>
        )
    }
}

export default withMarketplaceContext