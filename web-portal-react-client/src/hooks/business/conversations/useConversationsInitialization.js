import {
    useEffect,
    useCallback
} from 'react'

import { useSelector } from 'react-redux'

import {
    User,
    Conversation,
    ConversationService
} from 'factories'

import { useBoundActions } from 'hooks/common/redux'

import { useTracking } from 'hooks/business/tracking'

import { conversationsActions } from 'redux/index'

import { defer } from 'lib/utils/Utils'

import { selectors } from './useConversationsState'

const { List } = require('immutable')

const { sortByUpdated } = selectors

const service = ConversationService()

const selectUser = state => state.auth.login.user.data
const selectIsReady = state => state.conversations.isReady
const selectCurrentUser = state => state.conversations.currentUser

export default function useConversationsInitialization() {
    const authUser = useSelector(selectUser)
    const isReady = useSelector(selectIsReady)
    const user = useSelector(selectCurrentUser)

    const {
        catchMessage
    } = useTracking()

    const actions = useBoundActions(conversationsActions)

    const checkNewMessages = useCallback(async () => {
        let conversations = sortByUpdated(List(await service.getAll()))

        if (!conversations.isEmpty()) {
            let firstConversation = Conversation(conversations.first())
            let { lastReadMessageIndex } = firstConversation

            let [message] = await service.getMessages(firstConversation, { pageSize: 1 })

            if (message) {
                return message.index > lastReadMessageIndex ? message : false
            } else {
                return false
            }
        } else {
            return false
        }
    }, [])

    const onUserUpdated = useCallback(({ user, updateReasons }) => {
        if (updateReasons.includes('attributes')) {
            const { activeCallConversationSids } = User(user)

            actions.updateLiveConversations(activeCallConversationSids)
        }
    }, [actions])

    useEffect(() => {
        if (isReady) {
            service.on('connectionStateChanged', actions.setConnectionStatus)

            return () => {
                service.off('connectionStateChanged', actions.setConnectionStatus)
            }
        }
    }, [isReady, actions])

    useEffect(() => {
        if (authUser) {
            actions.init()

            return () => {
                defer().then(() => {
                    catchMessage('Destroy Conversations')
                    actions.destroy()
                })
            }
        }
    }, [actions, authUser, catchMessage])

    

    useEffect(() => {
        if (isReady) {
            checkNewMessages().then(message => {
                if (message) {
                    actions.setLastMessage(message)
                }
            })
        }
    }, [actions, checkNewMessages, isReady])

    useEffect(() => {
        if (user) {
            user.on('updated', onUserUpdated)

            return () => {
                user.off('updated', onUserUpdated)
            }
        }
    }, [user, onUserUpdated])
}