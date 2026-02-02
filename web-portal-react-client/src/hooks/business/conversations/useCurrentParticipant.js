import { useState, useMemo, useEffect, useCallback } from 'react'

import { useSelector } from 'react-redux'

import { useAuthUser, useAbortable } from 'hooks/common'

import { Participant } from 'factories'

import useConversations from './useConversations'

function useCurrentParticipant(conversation) {
    const authUser = useAuthUser()
    const users = useSelector(state => state.conversations.users.data)

    const [currentParticipant, setCurrentParticipant] = useState(null)

    const { getConversationParticipant } = useConversations()

    const participants = useMemo(() => {
        return conversation
            ?.participantIdentities
            .map(identity => users.get(identity))
    }, [conversation, users])

    const currentIdentity = useMemo(() => (
        participants?.find(o => o && o.employeeId === authUser.id)?.identity
    ), [authUser, participants])

    const [fetch, abortFetching] = useAbortable(useCallback(async () => {
        let participant = await getConversationParticipant(conversation, currentIdentity)

        if (participant) {
            setCurrentParticipant(Participant(participant))
        }
    }, [conversation, currentIdentity, getConversationParticipant]))

    useEffect(() => {
        if (conversation && currentIdentity) {
            fetch()

            return () => {
                abortFetching()
                setCurrentParticipant(null)
            }
        }
    }, [abortFetching, conversation, currentIdentity, fetch])

    return currentParticipant
}

export default useCurrentParticipant
