import { useQuery as useBaseQuery } from '@tanstack/react-query'

export default function useQuery(
    name,
    {
        filter = {},
        sorting = {},
        ...other
    } = {},
    {
        fetch,
        ...options
    }
) {
    const {
        field = null,
        order = null
    } = sorting ?? {}

    let params = {
        ...other,
        ...filter,
        ...(field ? {
            sort: `${field},${order}`
        } : null)
    }

    return useBaseQuery([name, params], () => fetch(params), { ...options })
}