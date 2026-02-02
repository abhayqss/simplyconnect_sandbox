import {
	useMemo,
	useCallback
} from 'react'

import {
	useHistory,
	useLocation
} from 'react-router-dom'

function useLocationSearchParams() {
	const history = useHistory()
	const location = useLocation()

	const searchParams = useMemo(
		() => new URLSearchParams(location.search),
		[location.search]
	)

	const setSearchParams = useCallback(search => {
		history.replace({ search })
	}, [history])

	return [searchParams, setSearchParams]
}

export default useLocationSearchParams
