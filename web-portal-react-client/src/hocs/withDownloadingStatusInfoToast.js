import React from 'react'

import {
    useDownloadingStatusInfoToast
} from 'hooks/common'

export default function withDownloadingStatusInfoToast(Component) {
    return function(props) {
        const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()
        
        return (
            <Component
                withDownloadingStatusInfoToast={withDownloadingStatusInfoToast}
                {...props}
            />
        )
    }
}