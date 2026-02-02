import {
	useServicesQuery,
	useServiceCategoriesQuery
} from 'hooks/business/directory/query'

import {
	isInteger
} from 'lib/utils/Utils'

export default function useCommunityFilterDirectory(
	{ categoryId } = {}
) {
	const {
		data: serviceCategories,
		isFetching: isFetchingServiceCategories
	} = useServiceCategoriesQuery({ isAccessibleOnly: true })

	const {
		data: services,
		isFetching: isFetchingServices
	} = useServicesQuery(
		{
			isAccessibleOnly: true,
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