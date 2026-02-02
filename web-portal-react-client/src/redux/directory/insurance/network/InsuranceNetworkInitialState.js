import List from './list/InsuranceNetworkListInitialState'
import Aggregated from './aggregated/InsuranceNetworkAggregatedInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    aggregated: Aggregated(),
})