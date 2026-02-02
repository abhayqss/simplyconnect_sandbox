import React from 'react'

import cn from 'classnames'

const LinkRegExp = /(((https?:\/\/)|(www\.))[^\s]+)/g

function addLink(text, link) {
    return text.replaceAll(link, `<a href="${link}" target="_blank">${link}</a>`)
}

function removeDuplicates(array) {
    return [...new Set(array)]
}

function parse(text = '') {
    let matches = [...text.matchAll(LinkRegExp)]

    if (!matches.length) return text

    const links = removeDuplicates(matches.map(match => match[0]))

    return links.reduce(addLink, text)
}

function MessageText({ hasLinks, className, children }) {
    return (
        <div
            className={cn('MessageText', className)}
            dangerouslySetInnerHTML={hasLinks ? { __html: parse(children) } : undefined}
        >
            {hasLinks ? null : children}
        </div>
    )
}

export default MessageText
