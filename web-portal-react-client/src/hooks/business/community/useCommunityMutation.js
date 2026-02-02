import { useMutation } from '@tanstack/react-query'

import service from 'services/CommunityService'

export default function useCommunityMutation(options) {
    return useMutation(({ data, organizationId }) => service.save(data, { organizationId }), options)
}
