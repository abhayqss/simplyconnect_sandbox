import {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import { usePaginatedQuery } from 'hooks/common/index'

export default function useListQuery(
    name,
    params,
    options
) {
    const [page, setPage] = useState(1)
    const [totalCount, setTotalCount] = useState(0)

    const [sorting, setSorting] = useState({
        field: params?.sorting?.field,
        order: params?.sorting?.order || 'asc'
    })

    const pagination = useMemo(() => ({
        page: page,
        size: params.size,
        totalCount
    }), [page, params.size, totalCount])

    const queryResult = usePaginatedQuery(name, { ...params, pagination, sorting }, options)

    useEffect(function () {
        if (queryResult.data?.totalCount != null) {
            setTotalCount(queryResult.data.totalCount)
        }
    }, [queryResult.data])

    return {
        pagination,
        refresh: useCallback((page = 1) => setPage(page ?? 1), []),
        sort: useCallback((field, order) => setSorting({ field, order }), []),
        ...queryResult
    }
}