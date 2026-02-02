import BaseService from './BaseService'

export class HIEConsentPolicyService extends BaseService {
	save({ clientId, ...data }) {
		return super.request({
			method: data.id ? 'PUT' : 'POST',
			url: `/clients/${clientId}/hie-consent-policy`,
			body: data
		})
	}
}

export default new HIEConsentPolicyService()

