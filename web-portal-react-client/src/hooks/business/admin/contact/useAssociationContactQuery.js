import { useQuery } from '@tanstack/react-query'

import service from 'services/AssociationsService'

const fetch = ({ contactId }) => service.getAssociationContactData(
    contactId, { response: { extractDataOnly: true } }
)

export default function useAssociationContactQuery(params, options) {
    return useQuery(['AssociationContact', params], () => fetch(params), options)
}