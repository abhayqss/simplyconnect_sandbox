import {
	useMemo,
	useState,
	useEffect,
	useCallback
} from 'react'

import { useMutation } from '@tanstack/react-query'

const DEFAULT_FIRST_PAGE = 1

function defer(delay = 0, ...args) {
	return new Promise(resolve => {
		return setTimeout(() => {
			resolve.apply(null, args)
		}, delay)
	})
}

function useManualInfiniteQuery(params, fetchFn, options) {
	const [page, setPage] = useState(DEFAULT_FIRST_PAGE)
	const [aggregatedData, setAggregatedData] = useState([])

	let size = params.size ?? 15

	const {
		data,
		error,
		mutateAsync: fetch,
		isLoading: isFetching
	} = useMutation(o => fetchFn({
		...params,
		page,
		...o
	}), options)

	const refetch = useCallback((page = DEFAULT_FIRST_PAGE) => {
		setPage(page)
		setAggregatedData([])
		return defer().then(fetch)
	}, [fetch])

	const {
		totalCount,
		data: pageData
	} = data ?? {}

	const totalPages = Math.ceil(totalCount / size)
	const hasNextPage = page < totalPages

	const pagination = useMemo(() => ({
		page, size, totalCount
	}), [page, size, totalCount])

	const isFetchingMore = isFetching && page !== 1

	const fetchMore = useCallback(() => {
		if (!isFetching && hasNextPage) {
			setPage(page => page + 1)
			defer().then(fetch)
		}
	}, [fetch, isFetching, hasNextPage])

	useEffect(function onPopulateData() {
		if (pageData) {
			setAggregatedData(data => [...data, ...pageData])
		}
	}, [pageData])

	return {
		data,
		error,
		fetch,
		refetch,
		fetchMore,
		isFetching,
		pagination,
		hasNextPage,
		aggregatedData,
		isFetchingMore
	}
}

export default useManualInfiniteQuery
