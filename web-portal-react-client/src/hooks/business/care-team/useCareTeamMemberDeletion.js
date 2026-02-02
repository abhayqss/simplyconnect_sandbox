import { useMutation } from '@tanstack/react-query'
import service from 'services/CareTeamMemberService'

function fetch({ id, ...params }) {
    return service.deleteById(id, params)
}

export default function useCareTeamMemberDeletion(options) {
    return useMutation(fetch, options)
}