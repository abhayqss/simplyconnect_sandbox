import { useCallback } from 'react'

import { useSelector } from 'react-redux'

import { map } from 'underscore'

import { useStrategy } from 'hooks/common'
import { useConversations } from 'hooks/business/conversations/index'

import { Conversation, Message } from 'factories'

import {
    OnlineCallGetterStrategy,
    IncomingCallSearchStrategy,
} from 'strategies/conversations'

const { List } = require('immutable')

const selectUser = state => state.conversations.currentUser

function useCallSearchContext() {
    const user = useSelector(selectUser)

    const {
        getBySid,
        getMessages
    } = useConversations()

    const { execute: executeCallSearch } = useStrategy(IncomingCallSearchStrategy)
    const { execute: executeOnlineCallSearch } = useStrategy(OnlineCallGetterStrategy)

    const getConversationMessages = useCallback(async (sid) => {
        const conversation = Conversation(
            await getBySid(sid)
        )

        const messages = List(map(
            await getMessages(conversation),
            Message
        ))

        return messages
    }, [getBySid, getMessages])

    const searchIncomingCallBySid = useCallback(async (sid) => {
        const messages = await getConversationMessages(sid)

        return executeCallSearch({ messages: messages.reverse(), userIdentity: user.identity })
    }, [user, executeCallSearch, getConversationMessages])

    const getOnlineCall = useCallback(async (sid, targetSid) => {
        const messages = await getConversationMessages(sid)

        return executeOnlineCallSearch({ messages: messages.reverse(), conversationSid: targetSid })
    }, [executeOnlineCallSearch, getConversationMessages])

    return {
        getOnlineCall,
        searchIncomingCallBySid
    }
}

export default useCallSearchContext
