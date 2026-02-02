import {
    useGendersQuery,
    useProspectStatusesQuery,
} from 'hooks/business/directory/query'

export default function useProspectFilterDirectory() {
    const {
        data: genders
    } = useGendersQuery()

    const {
        data: statuses
    } = useProspectStatusesQuery()

    return {
       genders,
       statuses
    }
}