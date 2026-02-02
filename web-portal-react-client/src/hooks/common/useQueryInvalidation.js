import { useCallback } from 'react'

import { useQueryClient } from '@tanstack/react-query'

import { isEqual } from 'underscore'

function useQueryInvalidation() {
    const queryClient = useQueryClient()

    const invalidate = useCallback((key, params, options = {}) => {
        const { predicate } = options ?? {}

        return queryClient.invalidateQueries(query => {
            let [name, paramsKey] = query.queryKey
            return predicate ? predicate(query) : (
                key === name && (params ? isEqual(params, paramsKey) : true)
            )
        })
    }, [queryClient])

    return invalidate
}

export default useQueryInvalidation
