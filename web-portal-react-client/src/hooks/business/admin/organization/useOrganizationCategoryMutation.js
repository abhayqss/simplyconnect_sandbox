import { useMutation } from '@tanstack/react-query'

import service from 'services/CategoryService'

export default function useOrganizationCategoryMutation(options) {
    return useMutation((data) => service.save(data), options)
}
