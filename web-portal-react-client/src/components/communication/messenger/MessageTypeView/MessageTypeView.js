import React from 'react'

import Highlighter from 'react-highlight-words'

import { isEmpty } from 'lib/utils/StringUtils'

import { ReactComponent as Call } from 'images/call.svg'
import { ReactComponent as Play } from 'images/play.svg'
import { ReactComponent as Photo } from 'images/photo.svg'
import { ReactComponent as File } from 'images/unknown-file.svg'

import './MessageTypeView.scss'

function MessageTypeView({ message, highlightedText }) {
    let content = (
        highlightedText && message?.text ? (
            <Highlighter
                textToHighlight={message?.text}
                searchWords={[highlightedText]}
                highlightClassName='MessageTypeView-HighlightedText'
            />
        ) : message?.text
    )

    if (isEmpty(content) && message?.attributes?.quote) {
        content = 'Quoted message'
    }

    if (message?.isCall) {
        content = (
            <>
                <Call className="margin-right-6"/>
                Call ended
            </>
        )
    } else if (message?.isVoice) {
        content = (
            <>
                <Play className="margin-right-6"/>
                Voice record
            </>
        )
    } else if (message?.media) {
        let Icon = File
        let text = 'File'

        const {
            type
        } = message.media

        if (type?.includes('audio')) {
            Icon = Play
            text = 'Audio file'
        } else if (type?.includes('image')) {
            Icon = Photo
            text = 'Photo message'
        }

        content = (
            <>
                {Icon && (
                    <Icon className="margin-right-6"/>
                )}
                {text}
            </>
        )
    }

    return (
        <div className="MessageTypeView">
            {content}
        </div>
    )
}

export default MessageTypeView