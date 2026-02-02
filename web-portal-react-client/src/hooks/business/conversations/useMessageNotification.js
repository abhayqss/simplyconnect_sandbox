import {
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    map,
    noop,
    compact
} from 'underscore'

import { useSelector } from 'react-redux'

import {
    Message,
    Conversation,
    ConversationService
} from 'factories'

import {
    useAuthUser,
    useBoundActions
} from 'hooks/common/redux'

import { useConversations } from 'hooks/business/conversations/index'

import conversationsActions from 'redux/conversations/conversationsActions'

const service = ConversationService()

export default function useMessageNotification({ shouldIgnore = noop() } = {}) {
    const [notification, setNotification] = useState(null)

    const authUser = useAuthUser()

    const {
        isReady, users
    } = useSelector(state => state.conversations)

    const actions = useBoundActions(conversationsActions)

    const {
        getParticipants
    } = useConversations()

    const onMessageAdded = useCallback(o => {
        function isCurrentUser(user) {
            return user?.employeeId === authUser?.id
        }

        const message = Message(o)

        if (!shouldIgnore(message)) {
            const conversation = Conversation(o.conversation)

            const data = {
                message, conversation: {
                    friendlyName: conversation.friendlyName
                }
            }

            actions.setLastMessage(o)

            getParticipants(conversation).then(participants => {
                const mapped = compact(map(
                    participants, p => {
                        const user = (
                            users.data.get(p.identity)
                        )

                        return user && {
                            ...user, isCurrentUser: isCurrentUser(user)
                        }
                    }
                ))

                if (participants.length === mapped.length) {
                    data.conversation.participants = mapped
                    setNotification(data)
                } else {
                    actions.loadUsers({
                        conversationSids: [message.conversationSid]
                    }).then(users => {
                        data.conversation.participants = map(
                            users, user => {
                                return {
                                    ...user, isCurrentUser: isCurrentUser(user)
                                }
                            }
                        )
                        setNotification(data)
                    })
                }
            })
        }
    }, [users, actions, authUser, shouldIgnore, getParticipants])

    useEffect(() => {
        if (isReady) {
            service.on('messageAdded', onMessageAdded)

            return () => {
                setNotification(null)
                service.off('messageAdded', onMessageAdded)
            }
        }
    }, [isReady, onMessageAdded])

    return notification
}