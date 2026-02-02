import { map, where } from 'underscore'

import { useSelector } from 'react-redux'

import { useStore, useDirectoryData } from 'hooks/common'
import { useBoundActions, usePrimaryFilter } from 'hooks/common/redux'

import listActions from 'redux/lab/research/order/list/labOrderListActions'

import { NAME } from 'containers/Labs/LabOrderPrimaryFilter/LabOrderPrimaryFilter'
import { NAME as SECOND_FILTER_NAME } from 'containers/Labs/LabResearchOrderFilter/LabResearchOrderFilter'

import { isEmpty } from 'lib/utils/Utils'

import { useCommunitiesQuery } from './index'

function filterCommunities(data) {
    return where(data, { canViewOrHasAccessibleClient: true })
}

export default function useIncidentReportPrimaryFilter(options) {
    const fields = useSelector(state => (
        state.lab.research.order.list.dataSource.filter
    ))

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
            ...options
        }
    )

    const { organizations } = useDirectoryData({
        organizations: [ 'organization' ]
    })

    const communities = useSelector(state => (
            filterCommunities(
                state.lab.research.order.community.list.dataSource.data
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