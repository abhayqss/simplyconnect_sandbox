import {
	useServicesQuery,
	useServiceCategoriesQuery
} from 'hooks/business/directory/query'

import {
	isInteger
} from 'lib/utils/Utils'

export default function useProviderFilterDirectory(
	{ categoryId } = {}
) {
	const {
		data: serviceCategories,
		isFetching: isFetchingServiceCategories
	} = useServiceCategoriesQuery({ isAuthorizedAccess: false })

	const {
		data: services,
		isFetching: isFetchingServices
	} = useServicesQuery(
		{
			isAuthorizedAccess: false,
			serviceCategoryIds: [categoryId]
		},
		{ enabled: isInteger(categoryId) }
	)

	return {
		services,
		serviceCategories,
		isFetchingServices,
		isFetchingServiceCategories
	}
}