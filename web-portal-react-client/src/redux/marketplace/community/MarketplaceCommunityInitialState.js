import List from './list/MarketplaceCommunityListInitialState'
import Saved from './saved/SavedMarketplaceCommunityInitialState'
import Filter from './filter/MarketplaceCommunityFilterInitialState'
import Saving from './saving/MarketplaceCommunitySavingInitialState'
import Details from './details/MarketplaceCommunityDetailsInitialState'
import Removing from './removing/MarketplaceCommunityRemovingInitialState'
import Location from './location/MarketplaceCommunityLocationInitialState'
import Appointment from './appointment/MarketplaceCommunityAppointmentInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    list: new List(),
    saved: new Saved(),
    filter: new Filter(),
    saving: new Saving(),
    details: new Details(),
    removing: new Removing(),
    location: Location(),
    appointment: new Appointment()
})

export default InitialState