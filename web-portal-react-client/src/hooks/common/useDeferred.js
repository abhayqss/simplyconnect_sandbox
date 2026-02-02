export default function useDeferred(fn, params = {}) {
	if (typeof fn !== 'function') return fn

	return function (...args) {
		return new Promise(resolve => {
			return setTimeout(() => {
				resolve(fn.apply(null, args))
			}, params.delay)
		})
	}
}