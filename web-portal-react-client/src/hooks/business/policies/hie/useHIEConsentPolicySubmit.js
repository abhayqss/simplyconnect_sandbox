import { useMutation } from '@tanstack/react-query'

import service from 'services/HIEConsentPolicyService'

function useHIEConsentPolicySubmit(options) {
	return useMutation(data => service.save(data), options)
}

export default useHIEConsentPolicySubmit
