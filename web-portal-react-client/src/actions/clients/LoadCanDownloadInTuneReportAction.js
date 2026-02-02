import Factory from '../ActionFactory'

import actions from 'redux/client/assessment/report/in-tune/can/download/canDownloadInTuneReportActions'

export default Factory(actions, {
    action: ({ clientId }, actions) => actions.load({ clientId })
})