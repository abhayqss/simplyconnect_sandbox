import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './insuranceNetworkAggregatedListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: (...args) => service.findInsuranceNetworkAggregatedNames(...args)
})