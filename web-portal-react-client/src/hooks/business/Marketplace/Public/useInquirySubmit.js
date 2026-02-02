import { useMutation } from 'hooks/common'

import service from 'services/PublicMarketplaceCommunityService'

const submit = data => {
	return service.saveInquiry(data)
}

function useInquirySubmit(params, options) {
	return useMutation(params, submit, options)
}

export default useInquirySubmit
