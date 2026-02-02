import { useMemo } from 'react'

import { useSelector } from 'react-redux'

function useParticipants(conversation) {
    const users = useSelector(state => state.conversations.users.data)

    const participants = useMemo(() => {
        return conversation
            ?.participantIdentities
            .map(identity => users.get(identity))
            .filter(o => o) || []
    }, [conversation, users])

    return participants
}

export default useParticipants
