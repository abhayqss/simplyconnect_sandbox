import useBoundActions from './useBoundActions'

import * as actions from 'redux/sidebar/sideBarActions'

function useSideBarUpdate() {
    return useBoundActions(actions.update)
}

export default useSideBarUpdate
