import React from 'react'

import DocumentTitle from 'react-document-title'

import { Footer } from 'components'

import Messenger from 'containers/Communication/Messenger/Messenger'

import './Messages.scss'

export default function Messages() {
    return (
        <DocumentTitle title="Simply Connect | Chats">
            <>
                <div className="Messages">
                    <Messenger className="h-100"/>
                </div>

                <Footer className="Messages-Footer" theme="gray"/>
            </>
        </DocumentTitle>
    )
}