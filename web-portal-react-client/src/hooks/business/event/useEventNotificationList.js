import useList from 'hooks/common/useList'

import service from 'services/EventNoteService'

const options = {
    doLoad: (params) => {
        return service.findEventNotifications(params)
    }
}

function useEventNotificationList(params) {
    return useList('EVENT_NOTIFICATIONS', params, options)
}

export default useEventNotificationList
