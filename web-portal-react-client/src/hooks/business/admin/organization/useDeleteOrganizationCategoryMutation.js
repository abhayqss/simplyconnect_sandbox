import { useMutation } from '@tanstack/react-query'

import service from 'services/CategoryService'

export default function useDeleteOrganizationCategoryMutation(options) {
    return useMutation((id) => service.remove(id), options)
}
