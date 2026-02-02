import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useEventTypesQuery
} from 'hooks/business/directory/query'

import {
    useOldestEventDateQuery,
    useNewestEventDateQuery
} from 'hooks/business/event/query'

import {
    isInteger
} from 'lib/utils/Utils'

export default function useProspectEventFilterDirectory({ prospectId } = {}) {
    const user = useAuthUser()

    const {
        data: types = []
    } = useEventTypesQuery(
        { prospectId }, {
            staleTime: 0,
            enabled: user && isInteger(prospectId)
        }
    )

    const {
        data: oldestDate = []
    } = useOldestEventDateQuery(
        { prospectId }, {
            staleTime: 0,
            enabled: user && isInteger(prospectId)
        }
    )

    const {
        data: newestDate = []
    } = useNewestEventDateQuery(
        { prospectId }, {
            staleTime: 0,
            enabled: user && isInteger(prospectId)
        }
    )

    return {
        types,
        oldestDate,
        newestDate
    }
}