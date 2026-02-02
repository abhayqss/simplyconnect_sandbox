import { omit } from 'underscore'

import { FAILED_SEARCH_REASONS } from './Constants'

function ConversationSearchStrategy(context) {
    return new Promise((resolve, reject) => {
        try {
            let {
                queryParams,
                isMobileView,
                locationState,
                conversations,
                sidOfLastSelected,
                selectedConversation,
            } = context

            const conversationSid = (
                queryParams?.conversationSid
                || locationState?.conversationSid
            )

            const shouldSelectConversation = locationState?.shouldSelectConversation

            const {
                employeeIds,
            } = locationState || {}

            if (!selectedConversation || shouldSelectConversation) {
                switch (true) {
                    case !!conversationSid: {
                        let conversation = conversations.find(cv => cv.sid === conversationSid) ?? null

                        resolve(conversation)
                        break
                    }

                    case !!employeeIds?.length: {
                        reject({
                            reason: FAILED_SEARCH_REASONS.DOES_NOT_EXIST,
                            payload: omit(locationState, 'conversationSid')
                        })
                        break
                    }

                    case conversations.isEmpty(): {
                        reject({ reason: FAILED_SEARCH_REASONS.LIST_IS_EMPTY })
                        break
                    }

                    case isMobileView: {
                        reject({ reason: FAILED_SEARCH_REASONS.IS_MOBILE_VIEW })
                        break
                    }

                    default: {
                        let conversation = sidOfLastSelected
                            ? conversations.find(o => o.sid === sidOfLastSelected)
                            : conversations.first()

                        resolve(conversation)
                        break
                    }
                }
            } else {
                reject({ reason: FAILED_SEARCH_REASONS.IS_ALREADY_SELECTED })
            }
        } catch (error) {
            reject({
                reason: FAILED_SEARCH_REASONS.INTERNAL_ERROR,
                payload: { error }
            })
        }
    })
}


export default ConversationSearchStrategy
