import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentService'

const fetch = ({ communityId, folderId }) => service.canAdd({ communityId, folderId })

export default function useCanAddDocumentsQuery(params, options) {
    return useQuery(['CanAddDocuments', params], () => fetch(params), options)
}