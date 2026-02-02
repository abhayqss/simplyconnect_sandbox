import React from 'react'

import { useQueryClient } from '@tanstack/react-query'

const withQueryCache = (Component) => {
    return function(props) {
        const queryClient = useQueryClient()

        return (
            <Component {...props} cache={queryClient} />
        )
    }

}

export default withQueryCache
