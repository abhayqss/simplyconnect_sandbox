import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectMedicalContactService'

const fetch = (params) => service.find(params)

function useMedicalContactsQuery(params, options) {
    return useQuery(['Prospect.MedicalContacts', params], () => fetch(params), options)
}

export default useMedicalContactsQuery