import { useEffect } from 'react'

import { map, where } from 'underscore'

import { useSelector } from 'react-redux'

import {
    useStore,
    useDirectoryData,
    useLocationState
} from 'hooks/common'

import {
    useBoundActions,
    usePrimaryFilter
} from 'hooks/common/redux'

import listActions from 'redux/incident/report/list/incidentReportListActions'

import { NAME } from 'containers/IncidentReports/IncidentReportPrimaryFilter/IncidentReportPrimaryFilter'
import { NAME as SECOND_FILTER_NAME } from 'containers/IncidentReports/IncidentReportFilter/IncidentReportFilter'

import { isEmpty } from 'lib/utils/Utils'

import { useCommunitiesQuery } from './index'

function filterCommunities(data) {
    return where(data, { canViewOrHasAccessibleClient: true })
}

export default function useIncidentReportPrimaryFilter() {
    const fields = useSelector(state => (
        state.incident.report.list.dataSource.filter
    ))

    const [{ client } = {}] = useLocationState()

    const { organizationId, communityIds } = fields

    const actions = useBoundActions(listActions)

    const {
        changeFilter: change,
        changeFilterField: changeField
    } = actions

    const store = useStore()

    const isSaved = !!store.get(SECOND_FILTER_NAME)

    const config = usePrimaryFilter(
        NAME, fields, actions, {
            onRestored: () => {
                !isSaved && change({})
            },
            getInitialData: data => client ? ({
                clientId: client.id ?? null,
                communityIds: [client.communityId],
                organizationId: client.organizationId || data.organizationId,
            }) : data
        }
    )

    const { organizations } = useDirectoryData({
        organizations: [ 'organization' ]
    })

    const communities = useSelector(state => (
            filterCommunities(
                state.incident.report.community.list.dataSource.data
            )
        )
    )

    useCommunitiesQuery(
        { organizationId },
        {
            onSuccess: ({ data }) => {
                changeField(
                    'communityIds',
                    isEmpty(communityIds) ? (
                        map(filterCommunities(data), o => o.id)
                    ) : communityIds,
                    false,
                    true
                )
            }
        }
    )

    return {
        ...config,
        communities,
        organizations
    }
}