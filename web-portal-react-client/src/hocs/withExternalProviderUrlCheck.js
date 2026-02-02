import React from 'react'

import { useExternalProviderUrlCheck } from 'hooks/business/external'

const withExternalProviderUrlCheck = (Component) => {
    return function(props) {
        const isExternalProviderUrl = useExternalProviderUrlCheck()

        return (
            <Component
                {...props}
                isExternalProviderUrl={isExternalProviderUrl}
            />
        )
    }

}

export default withExternalProviderUrlCheck
