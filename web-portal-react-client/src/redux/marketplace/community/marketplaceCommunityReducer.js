import InitialState from './MarketplaceCommunityInitialState'

import listReducer from './list/marketplaceCommunityListReducer'
import savedReducer from './saved/savedMarketplaceCommunityReducer'
import filterReducer from './filter/marketplaceCommunityFilterReducer'
import savingReducer from './saving/marketplaceCommunitySavingReducer'
import detailsReducer from './details/marketplaceCommunityDetailsReducer'
import removingReducer from './removing/marketplaceCommunityRemovingReducer'
import locationReducer from './location/marketplaceCommunityLocationReducer'
import appointmentReducer from './appointment/marketplaceCommunityAppointmentReducer'

const initialState = new InitialState()

export default function marketplaceCommunityReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const saved = savedReducer(state.saved, action)
    if (saved !== state.saved) nextState = nextState.setIn(['saved'], saved)

    const filter = filterReducer(state.filter, action)
    if (filter !== state.filter) nextState = nextState.setIn(['filter'], filter)

    const saving = savingReducer(state.saving, action)
    if (saving !== state.saving) nextState = nextState.setIn(['saving'], saving)
    
    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const removing = removingReducer(state.removing, action)
    if (removing !== state.removing) nextState = nextState.setIn(['removing'], removing)

    const location = locationReducer(state.location, action)
    if (location !== state.location) nextState = nextState.setIn(['location'], location)

    const appointment = appointmentReducer(state.appointment, action)
    if (appointment !== state.appointment) nextState = nextState.setIn(['appointment'], appointment)

    return nextState
}