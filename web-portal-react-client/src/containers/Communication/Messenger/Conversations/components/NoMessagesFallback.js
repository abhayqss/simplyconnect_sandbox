import React from 'react'

import { ReactComponent as MessageIcon } from 'images/message.svg'

const NoMessagesFallback = () => (
    <div className="Conversations-Fallback">
        <MessageIcon className="Conversations-FallbackImg" />
        <div className="Conversations-FallbackText">
            No messages
        </div>
    </div>
)

export default NoMessagesFallback