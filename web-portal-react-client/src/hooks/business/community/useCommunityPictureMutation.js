import { useMutation } from '@tanstack/react-query'

import service from 'services/CommunityService'

function fetch({ pictureId, communityId, organizationId }) {
    return service.findPictureById(pictureId, { organizationId, communityId })
}

export default function useCommunityPictureMutation(options) {
    return useMutation(fetch, options)
}
