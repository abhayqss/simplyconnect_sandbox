import InitialState from './СanInTuneReportInitialState'

import generateReducer from './generate/canGenerateInTuneReportReducer'
import downloadReducer from './download/canDownloadInTuneReportReducer'

const initialState = InitialState()

export default function canAssessmentReducer(state = initialState, action) {
    let nextState = state

    const generate = generateReducer(state.generate, action)
    if (generate !== state.generate) nextState = nextState.setIn(['generate'], generate)
    
    const download = downloadReducer(state.download, action)
    if (download !== state.download) nextState = nextState.setIn(['download'], download)

    return nextState
}
