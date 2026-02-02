import ReportingSetting from './reporting-setting/IncidentLevelReportingSettingInitialState'

const { Record } = require('immutable')

export default Record({
    reportingSetting: ReportingSetting(),
})