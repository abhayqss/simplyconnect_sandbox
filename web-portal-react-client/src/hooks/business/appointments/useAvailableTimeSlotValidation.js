import { useCallback } from 'react'

import {
	useRefCurrent,
	useValidation
} from 'hooks/common'

import AvailableTimeSlotValidator from 'validators/AvailableTimeSlotValidator'

function useAvailableTimeSlotValidation(params) {
	params = useRefCurrent(params)

	const [_validate, errors, setErrors] = useValidation(AvailableTimeSlotValidator)

	const validate = useCallback(
		o => _validate({ ...params, ...o }, {}),
		[params, _validate]
	)

	return [validate, errors, setErrors]
}

export default useAvailableTimeSlotValidation
