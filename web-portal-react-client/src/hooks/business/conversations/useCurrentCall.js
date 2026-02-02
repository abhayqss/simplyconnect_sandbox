import {
    useMemo,
    useEffect,
    useCallback
} from 'react'

import {
    noop,
} from 'underscore'

import { useSelector } from 'react-redux'

import { Message } from 'factories'

import { useBoundActions } from 'hooks/common/redux'
import {
    useConversations,
    useCallSearchContext,
} from 'hooks/business/conversations/index'

import { change as changeError } from 'redux/error/errorActions'

import { SERVICE_MESSAGE_TYPES } from 'lib/Constants'

const selectAuthUser = state => state.auth.login.user.data
const selectIsReady = state => state.conversations.isReady

const {
    CALL_END,
    INITIATE_CALL,
    CALL_ACCEPTED,
    CALL_MEMBER_JOINED,
    CALL_MEMBER_DECLINED,
    CALL_MEMBERS_TIMEOUT,
    CALL_PENDING_MEMBERS,
    CALL_CONVERSATION_UPDATED,
} = SERVICE_MESSAGE_TYPES

function useCurrentCall(
    {
        onJoinCall,
        onCallReceived,
        onTimeout = noop,
        onMemberJoined,
        onPendingMembers,
        onCallEnded = noop,
        onCallDeclined = noop,
        onCallAccepted = noop,
        onConversationUpdated,
    }
) {
    const user = useSelector(selectAuthUser)
    const isReady = useSelector(selectIsReady)

    const sid = user.serviceConversationSid

    const setError = useBoundActions(changeError)

    const {
        on,
        off,
    } = useConversations()

    const {
        getOnlineCall,
        searchIncomingCallBySid
    } = useCallSearchContext()

    const receiveIncomingCallBySid = useCallback((sid) => {
        searchIncomingCallBySid(sid)
            .then(call => {
                if (call) {
                    onCallReceived(call)
                }
            })
            .catch(setError)

    }, [onCallReceived, searchIncomingCallBySid, setError])

    const endCall = useCallback((data) => {
        onCallEnded(data.roomSid)
    }, [onCallEnded])

    const onJoinOnlineCall = useCallback(async ({ conversationSid }) => {
        const data = await getOnlineCall(sid, conversationSid)

        if (data) {
            onJoinCall(data)
        }
    }, [sid, getOnlineCall, onJoinCall])

    const actions = useMemo(() => new Map([
        [CALL_END, endCall],
        [INITIATE_CALL, (data) => {
            const isCurrentUserCalling = data.caller.employeeId === user.id

            !isCurrentUserCalling && onCallReceived(data)
        }],
        [CALL_ACCEPTED, onCallAccepted],
        [CALL_MEMBER_JOINED, onMemberJoined],
        [CALL_PENDING_MEMBERS, onPendingMembers],
        [CALL_MEMBER_DECLINED, onCallDeclined],
        [CALL_MEMBERS_TIMEOUT, onTimeout],
        [CALL_CONVERSATION_UPDATED, onConversationUpdated]
    ]), [
        user.id,
        endCall,
        onTimeout,
        onMemberJoined,
        onCallReceived,
        onCallAccepted,
        onCallDeclined,
        onPendingMembers,
        onConversationUpdated,
    ])

    useEffect(() => {
        if (sid && isReady) {
            receiveIncomingCallBySid(sid)
        }
    }, [sid, isReady, receiveIncomingCallBySid])

    useEffect(() => {
        function onMessageAdded(o) {
            const message = Message(o)

            if (user.serviceConversationSid === message.conversationSid) {
                const data = JSON.parse(message.text)
                const action = actions.get(data.type) || noop

                action(data)
            }
        }

        if (isReady && user?.serviceConversationSid) {
            on('messageAdded', onMessageAdded)

            return () => {
                off('messageAdded', onMessageAdded)
            }
        }
    }, [
        on, off,
        user, isReady,
        actions
    ])

    useEffect(() => {
        if (isReady) {
            on('joinCall', onJoinOnlineCall)

            return () => {
                off('joinCall', onJoinOnlineCall)
            }
        }
    }, [on, off, isReady, onJoinOnlineCall])
}

export default useCurrentCall
