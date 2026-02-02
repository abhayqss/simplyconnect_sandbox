import { useQuery } from '@tanstack/react-query'

import service from 'services/PublicMarketplaceCommunityService'

const fetch = ({ pictureId, ...params }) => service.downloadPictureById(pictureId, params)

function useProviderPictureQuery(params, options) {
	return useQuery(['Marketplace.Public.Provider.Picture', params], () => fetch(params), options)
}

export default useProviderPictureQuery
