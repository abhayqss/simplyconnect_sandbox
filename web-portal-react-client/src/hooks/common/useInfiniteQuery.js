import {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import { usePaginatedQuery } from 'hooks/common/index'

function useInfiniteQuery(name, params, options) {
    const [data, setData] = useState([])
    const [page, setPage] = useState(params.page ?? 0)
    
    let size = params.size ?? 10

    const {
        error,
        clear,
        remove,
        refetch,
        isFetching,
        isFetchingMore,
        data: {
            totalCount,
            data: pageData
        } = {},
    } = usePaginatedQuery(
        name,
        { ...params, page },
        {
            ...options,
            staleTime: 0
        }
    )

    const totalPages = Math.ceil(totalCount / size)
    const isLastPage = page === (totalPages - 1)

    const pagination = useMemo(() => ({
        page, size, totalCount
    }), [page, size, totalCount])

    const fetchMore = useCallback(() => {
        if (!isFetching && !isLastPage) {
            setPage(page => page + 1)
        }
    }, [isFetching, isLastPage])

    useEffect(function onPopulateData() {
        if (pageData) {
            setData(data => [...data, ...pageData])
        }
    }, [pageData])

    return {
        error,
        data,
        clear,
        remove,
        refetch,
        fetchMore,
        isFetching,
        pagination,
        isFetchingMore
    }
}

export default useInfiniteQuery
