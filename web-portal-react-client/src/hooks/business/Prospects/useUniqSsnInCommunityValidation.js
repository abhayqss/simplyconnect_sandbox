import {
	useState
} from 'react'

import {
	useMutation
} from 'hooks/common'

import service from 'services/ProspectService'

const UNIQ_SSN_ERROR_MESSAGE = 'The prospect with the SSN entered already exists in the community.'

function fetch(data = {}) {
	return service.validateUniqInCommunity(data)
}

export default function useUniqSsnInCommunityValidation(params, options) {
	const [error, setError] = useState(null)

	const { mutateAsync: validate } = useMutation(
		fetch, params, {
			...options,
			throwOnError: true,
			onError: setError,
			onSuccess: data => {
				if (!data.ssn) {
					const e = { message: UNIQ_SSN_ERROR_MESSAGE }
					setError(e)
					throw e
				} else setError(null)
			}
		}
	)

	return [validate, error]
}