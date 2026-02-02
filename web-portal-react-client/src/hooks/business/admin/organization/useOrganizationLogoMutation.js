import { useMutation } from '@tanstack/react-query'
import service from 'services/OrganizationService'

function fetch({ organizationId }) {
    return service.downloadLogoById(organizationId)
}

export default function useOrganizationLogoMutation(options) {
    return useMutation(fetch, options)
}