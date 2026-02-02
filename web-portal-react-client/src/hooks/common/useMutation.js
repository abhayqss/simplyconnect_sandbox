import { useMutation as useBaseMutation } from '@tanstack/react-query'

function useMutation(params, fetch, options) {
	return useBaseMutation({
		mutationFn: data => fetch({ ...params, ...data }),
		...options
	})
}

export default useMutation
