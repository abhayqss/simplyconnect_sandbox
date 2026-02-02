import { useMutation } from '@tanstack/react-query'
import service from 'services/CommunityService'

function fetch({ communityId, organizationId }) {
    return service.downloadLogoById(communityId, { organizationId })
}

export default function useCommunityLogoMutation(options) {
    return useMutation(fetch, options)
}