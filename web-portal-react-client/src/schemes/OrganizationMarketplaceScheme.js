import { Shape, string, ListOf } from './types'

import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

const { EMPTY_FIELD } = VALIDATION_ERROR_TEXTS

const OrganizationMarketplaceScheme = Shape({
	marketplace: Shape({
		serviceCategoryIds: ListOf().min(1, EMPTY_FIELD),
		serviceIds: ListOf().min(1, EMPTY_FIELD),
		servicesSummaryDescription: string().required().max(5000)
	})
})

export default OrganizationMarketplaceScheme
