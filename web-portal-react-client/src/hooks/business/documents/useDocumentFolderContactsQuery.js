import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentFolderService'

const fetch = ({ folderId, communityId, parentFolderId }) => {
    return service.findContacts({ folderId, communityId, parentFolderId })
}

function useDocumentFolderContactsQuery(params, options) {
    return useQuery(['Document.Folder.Contacts', params], () => fetch(params), options)
}

export default useDocumentFolderContactsQuery
