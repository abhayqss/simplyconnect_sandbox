import { useEffect } from 'react'

import structuredClone from '@ungap/structured-clone'

import { isPrimitive } from 'lib/utils/Utils'

import { useRefCurrent } from './'

export default function useMemoEffect(effect, deps) {
	const store = useRefCurrent()

	function memo(v) {
		if (v !== undefined) {
			store.memoized = v
		}

		return isPrimitive(store.memoized) ? store.memoized : structuredClone(store.memoized)
	}

	useEffect(effect.bind(null, memo), deps)
}