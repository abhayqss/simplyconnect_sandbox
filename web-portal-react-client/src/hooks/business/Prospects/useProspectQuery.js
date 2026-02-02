import { useQuery } from '@tanstack/react-query'

import service from 'services/ProspectService'

const fetch = ({ prospectId, ...params }) => service.findById(prospectId, params)

function useProspectQuery(params, options) {
	return useQuery(['Prospect', params], () => fetch(params), options)
}

export default useProspectQuery
