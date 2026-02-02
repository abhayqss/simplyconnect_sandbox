import List from './list/IncidentLevelReportingSettingListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
})