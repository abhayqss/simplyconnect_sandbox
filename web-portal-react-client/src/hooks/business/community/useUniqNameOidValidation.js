import { useMutation } from '@tanstack/react-query'

import service from 'services/CommunityService'

export default function useUniqNameOidValidation({ organizationId, name, oid }, options) {
    return useMutation(() => service.validateUniq(organizationId, { name, oid }), options)
}
