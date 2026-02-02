import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/incident/level/reporting-setting/list/incidentLevelReportingSettingListActions'

export default function useIncidentLevelReportingSettingsQuery() {
    useQuery(actions, null)
}
