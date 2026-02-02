import { useEffect } from 'react'

import { useDirectoryData } from 'hooks/common'

import useBoundActions from '../../common/redux/useBoundActions'

import * as actions from 'redux/directory/insurance/network/list/insuranceNetworkListActions'

export default function useInsuranceNetworksQuery(shouldReload = false) {
    const { insuranceNetworks } = useDirectoryData({
        insuranceNetworks: ['insurance', 'network']
    })

    const load = useBoundActions(actions.load)

    useEffect(() => {
        if (!insuranceNetworks.length || shouldReload) {
            load()
        }
    }, [load, insuranceNetworks.length, shouldReload])
}
