import React from 'react'

import cn from 'classnames'

import {
    Button,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { ReactComponent as MessagesIcon } from 'images/messages.svg'

function ConversationFallback({
    className,
    onCreateConversation,
    canCreateConversations
}) {
    return (
        <div className={cn('Conversation-Fallback', className)}>
            <MessagesIcon className="Conversation-FallbackImg" />
    
            <div className="Conversation-FallbackText">
                You have no messages
            </div>
    
            <div id="NewConversationBtn" className="margin-top-15">
                <Button
                    color='success'
                    onClick={onCreateConversation}
                    disabled={!canCreateConversations}
                    className="Conversations-StartBtn"
                >
                    Start chatting
                </Button>

                {!canCreateConversations && (
                    <Tooltip
                        target="NewConversationBtn"
                        modifiers={[
                            {
                                name: 'offset',
                                options: { offset: [0, 6] }
                            },
                            {
                                name: 'preventOverflow',
                                options: { boundary: document.body }
                            }
                        ]}
                    >
                        You don't have access to clients or contacts to start a conversation with
                    </Tooltip>
                )}
            </div>
        </div>
    )
}

export default ConversationFallback