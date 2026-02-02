import {
	useState,
	useCallback
} from 'react'

import {
	useMutation
} from 'hooks/common'

import service from 'services/ProspectService'

const UNIQ_EMAIL_ERROR_MESSAGE = 'The email must be unique within the organization.'

function fetch(data = {}) {
	return service.validateUniqInOrganization(data)
}

export default function useUniqEmailInOrganizationValidation(params, options) {
	const [error, setError] = useState(null)

	const { mutateAsync: validate } = useMutation(
		fetch, params, {
			...options,
			throwOnError: true,
			onError: setError,
			onSuccess: data => {
				if (!data.email) {
					const e = { message: UNIQ_EMAIL_ERROR_MESSAGE }
					setError(e)
					throw e
				} else setError(null)
			}
		}
	)

	return [validate, error]
}