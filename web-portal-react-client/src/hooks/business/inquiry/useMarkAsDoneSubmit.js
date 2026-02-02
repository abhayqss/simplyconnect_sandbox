import { useMutation } from 'hooks/common'

import service from 'services/InquiryService'

function submit({ inquiryId, ...data } = {}) {
	return service.markAsDone(data, { inquiryId })
}

function useMarkAsDoneSubmit(params, options) {
	return useMutation(params, submit, options)
}

export default useMarkAsDoneSubmit
