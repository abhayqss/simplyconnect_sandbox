import { useQuery } from '@tanstack/react-query'

import { PAGINATION } from 'lib/Constants'

const { MAX_SIZE } = PAGINATION

export default function usePaginatedQuery(
    name,
    {
        filter = {},
        sorting = {},
        pagination = {},
        ...other
    },
    {
        fetch,
        ...options
    }
) {
    const {
        field = null,
        order = null
    } = sorting ?? {}

    const {
        page,
        size = MAX_SIZE
    } = pagination ?? {}

    let params = {
        page,
        size,
        ...other,
        ...filter,
        ...(field ? {
            sort: `${field},${order}`
        } : null)
    }

    return useQuery([name, params], () => fetch(params), { ...options, keepPreviousData: true })
}