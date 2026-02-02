import {
	useMemo,
	useEffect
} from 'react'

import {
	QueryObserver,
	useQueryClient
} from '@tanstack/react-query'

import { noop } from 'lib/utils/FuncUtils'

export default function useQueryWatch(options, listener = noop) {
	const queryClient = useQueryClient()

	const observer = useMemo(() => (
		new QueryObserver(queryClient, { ...options })
	), [options, queryClient])

	useEffect(() => {
		const unsubscribe = observer.subscribe(listener)
		return () => unsubscribe()
	}, [observer, listener])

	return observer
}