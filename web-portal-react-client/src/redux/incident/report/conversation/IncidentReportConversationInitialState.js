import Join from './join/IncidentReportConversationJoinInitialState'

const { Record } = require('immutable');

export default Record({
    join: Join()
});