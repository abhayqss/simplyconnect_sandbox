import { useCallback } from 'react'

import { useQueryClient } from '@tanstack/react-query'

import { isEqual } from 'underscore'

function useQueryRemoving() {
    const queryClient = useQueryClient()

    const remove = useCallback((key, params) => {
        let queryKey = key.split('.')

        queryClient.removeQueries(query => {
            let [name, paramsKey] = query.queryKey
            let mainKey = name.split('.')

            return (
                queryKey.every((k, i) => mainKey[i] === k)
                && (params ? isEqual(paramsKey, params) : true)
            )
        })
    }, [queryClient])

    return remove
}

export default useQueryRemoving
