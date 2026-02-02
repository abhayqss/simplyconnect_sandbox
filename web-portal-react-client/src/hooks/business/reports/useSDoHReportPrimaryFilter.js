import { useSelector } from 'react-redux'

import { useDirectoryData } from 'hooks/common'
import { useBoundActions, usePrimaryFilter } from 'hooks/common/redux'

import listActions from 'redux/report/sdoh/list/sDoHReportListActions'

const NAME = 'SDOH_REPORT_PRIMARY_FILTER'

export default function useSDoHReportPrimaryFilter() {
    const fields = useSelector(state => (
        state.report.sdoh.list.dataSource.filter
    ))

    const actions = useBoundActions(listActions)

    const { changeFilter: change } = actions

    const config = usePrimaryFilter(NAME, fields, actions, {
        organizations: {
            query: {
                params: { canViewSdohReportsOnly: true }
            }
        },
        onRestored: () => {
            change({})
        }
    })

    const { organizations } = useDirectoryData({
        organizations: [ 'organization' ]
    })

    return { ...config, organizations }
}