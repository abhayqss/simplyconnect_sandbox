import React, {
    memo,
    useMemo,
    useState,
    useCallback
} from 'react'

import cn from 'classnames'

import {
    map,
    noop,
    uniq,
    find,
    chain,
    reduce,
    countBy
} from 'underscore'

import request from 'superagent'
import { saveAs } from 'file-saver'

import { useSelector } from 'react-redux'

import {
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import {
    useDownloadingStatusInfoToast
} from 'hooks/common'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useMediaUrl,
    useConversations
} from 'hooks/business/conversations'

import {
    ErrorViewer,
    OutsideClickListener
} from 'components'

import { IconButton } from 'components/buttons'

import MessageText from '../MessageText/MessageText'

import {
    isEmpty,
    getInitials,
    DateUtils as DU,
    StringUtils as SU
} from 'lib/utils/Utils'

import {
    isString
} from 'lib/utils/StringUtils'

import {
    addAbortAllEventListener,
    removeAbortAllEventListener
} from 'lib/utils/AjaxUtils'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES,
} from 'lib/Constants'

import { ReactComponent as Menu } from 'images/dots.svg'
import { ReactComponent as Send } from 'images/forward.svg'
import { ReactComponent as Reply } from 'images/emodji-reply.svg'

import Quote from '../Quote/Quote'
import Avatar from '../../Avatar/Avatar'
import { AudioTrack } from '../Audio/Audio'
import BaseFilePreview from '../FilePreview/FilePreview'

import { Emoji, ActionPicker } from '../'

import { EMOJI } from '../Emoji/lib/Constants'

import './Message.scss'

const isAudio = mimeType => ALLOWED_FILE_FORMAT_MIME_TYPES[ALLOWED_FILE_FORMATS.MP3] === mimeType

const { format, formats } = DU
const formatDate = date => format(date, formats.time)

const { concatIf } = SU

function Date({ children }) {
    return (
        <div className="Message-Date">
            {formatDate(children)}
        </div>
    )
}

function FilePreview(props) {
    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const download = () => {
        const rq = request
            .get(props.url)
            .responseType('blob')

        const abort = () => rq.abort()

        addAbortAllEventListener(abort)

        withDownloadingStatusInfoToast(() => rq.then(o => {
            removeAbortAllEventListener(abort)
            saveAs(o.body, props.data.name)
        }))
    }

    return (
        <BaseFilePreview onClick={download} {...props} />
    )
}

function ReactionDetails({ message, reaction }) {
    const emoji = EMOJI.find(o => o.id === reaction.id)

    const users = useSelector(
        state => state.conversations.users.data
    )

    const authorIdentities = reduce(message.attributes.reactions, (memo, o) => {
        if (o.id === reaction.id) memo.push(o.authorIdentity)
        return memo
    }, [])

    const authors = chain(users.toJS())
        .filter(o => authorIdentities.includes(o.identity))
        .sortBy('fullName')
        .value()

    return (
        <div className="MessageReactionDetails">
            <div className="MessageReactionDetails-Reaction">
                {emoji.renderIcon({ className: 'MessageReactionDetails-ReactionIcon' })}
            </div>
            <div className="MessageReactionDetails-AuthorList">
                {map(authors, o => (
                    <div key={o.identity} className="MessageReactionDetails-AuthorListItem">
                        <div className="MessageReactionDetails-Author">
                            <Avatar
                                id={o.avatarId}
                                className="MessageReactionDetails-AuthorAvatar"
                            >
                                {getInitials({ fullName: o.fullName })}
                            </Avatar>
                            <div className="MessageReactionDetails-AuthorName">
                                {o.fullName}
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

function Message(
    {
        data,
        author,
        canSend,
        isCurrentUser,
        isDisconnected,
        onTextClick,
        onClickQuote,
        onSend = noop,
        onEdit: onEditCb,
        onReply,
        className
    }
) {
    const [error, setError] = useState(null)
    const [isActionPickerOpen, setIsActionPickerOpen] = useState(false)

    const {
        addMessageReaction,
        deleteMessageReaction
    } = useConversations()

    const authUser = useAuthUser()

    const users = useSelector(state => state.conversations.users.data)

    const currUser = useMemo(() => (
        users.find(user => user.employeeId === authUser.id)
    ), [users, authUser])

    const reactions = useMemo(
        () => data.attributes?.reactions ?? [], [data]
    )

    const currUserReaction = useMemo(() => find(
        reactions, o => o.authorIdentity === currUser.identity
    ), [reactions, currUser])

    const { quote } = data.attributes ?? {}

    const quoteAuthor = quote && users.get(quote.authorIdentity)

    const canSendToDocuTrack = (
        canSend
        && data.media
        && !data.isSystemMessage
    )

    const bubbleClassNames = cn(
        'Message-Bubble',
        { 'Message-Bubble_bg_blue': isCurrentUser },
        { 'Message-Bubble_type_info': data.isSystemMessage }
    )

    const Media = data.media && (
        (isAudio(data.media.type) && data.media.name === 'voice-message.mp3')
            || data.isVoiceMessage ? AudioTrack : FilePreview
    )

    const fileUrl = useMediaUrl(data.media)

    function onClosePicker() {
        setIsActionPickerOpen(false)
    }

    function onEdit() {
        onClosePicker()
        onEditCb()
    }

    const _onReply = useCallback(() => {
        onReply(data)
    }, [data, onReply])

    const onEmojiReaction = useCallback(async reaction => {
        if (isDisconnected) return
        
        const isSame = (
            currUserReaction
            && reaction.id === currUserReaction.id
        )

        const mutate = isSame ? deleteMessageReaction : addMessageReaction

        try {
            const { sid, conversationSid } = data

            await mutate(
                { reactionId: reaction.id },
                { messageSid: sid, conversationSid }
            )
        } catch (e) {
            setError(e)
        }
    }, [
        data,
        isDisconnected,
        currUserReaction,
        addMessageReaction,
        deleteMessageReaction
    ])

    const {
        lastName,
        firstName,
        communityName
    } = author ?? {}

    let fullName = ''

    if (!data.isSystemMessage) {
        fullName = concatIf(
            `${firstName} ${lastName}`,
            ` - ${communityName}`,
            !!communityName
        )
    }

    const reactionIds = useMemo(() => map(
        data.attributes?.reactions, o => o.id
    ), [data])

    const reactionCounts = useMemo(
        () => countBy(reactionIds), [reactionIds]
    )

    return (
        <div className={cn('Message', className)}>
            {!(isCurrentUser || data.isSystemMessage) && (
                <Avatar
                    id={author.avatarId}
                    className="Message-Avatar"
                >
                    {getInitials({ fullName })}
                </Avatar>
            )}

            <div className="Message-Box">
                {/* Temporary hidden by CCN-6095 request */}
                {/*{isEdited && isCurrentUser && (
                    <Pencil className="Message-EditIndicator" />
                )}*/}
                <div className="Message-Content">
                    {isString(data.text) && (
                        <div
                            className={bubbleClassNames}
                            onClick={() => onTextClick(data)}
                            id={`message-${data.sid}`}
                        >
                            <div className="v-flexbox flex-1 margin-right-20">
                                <div className="h-flexbox justify-content-between margin-bottom-5">
                                    <div className="Message-Author">
                                        {!isCurrentUser && fullName}
                                    </div>
                                    {isEmpty(data.media) && !data.isSystemMessage && (
                                        <Date>{data.dateCreated}</Date>
                                    )}
                                </div>

                                {quote && (
                                    <Quote
                                        date={quote.date}
                                        type={quote.type}
                                        onClick={onClickQuote}
                                        title={quoteAuthor.fullName}
                                        className="margin-bottom-5 align-self-start"
                                    >
                                        <div className="line-clamp-2">
                                            {quote.text}
                                        </div>
                                    </Quote>
                                )}

                                <MessageText
                                    hasLinks={data.displayLinks}
                                    className="Message-Text"
                                >
                                    {data.text}
                                </MessageText>

                                <div className="h-flexbox justify-content-between">
                                    <Emoji
                                        each={o => {
                                            if (!uniq(reactionIds).includes(o.id)) return false

                                            return {
                                                tooltip: {
                                                    hideArrow: true,
                                                    className: 'MessageReactionDetailsTooltip',
                                                    render: reaction => (
                                                        <ReactionDetails
                                                            message={data}
                                                            reaction={reaction}
                                                        />
                                                    )
                                                },
                                                description: reactionCounts[o.id]
                                            }
                                        }}
                                        onSelect={onEmojiReaction}
                                        className="Message-Reactions"
                                    />
                                </div>
                            </div>
                            <div className="position-relative">
                                {!data.isSystemMessage && (
                                    <>
                                        <Menu
                                            id={`message-${data.sid}-menu-toggle`}
                                            className={cn(
                                                "MessageMenu-Toggle",
                                                { "MessageMenu-Toggle__disconnected": isDisconnected }
                                            )}
                                        />
                                        {!isDisconnected && (
                                            <Tooltip
                                                trigger="focus"
                                                hideArrow={true}
                                                boundariesElement="scrollParent"
                                                target={`message-${data.sid}-menu-toggle`}
                                                className="MessageMenu MessageMenuPopup"
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
                                                <div className="MessageMenuPopup-Header">
                                                    Actions
                                                </div>
                                                <div className="MessageMenuPopup-Body">
                                                    <ul className="MessageMenu-Actions">
                                                        <li onClick={_onReply} className="MessageMenu-Action">
                                                            <Reply className="MessageMenu-ActionIcon"/> Reply
                                                        </li>
                                                    </ul>
                                                    <Emoji
                                                        onSelect={onEmojiReaction}
                                                        className="MessageMenu-Reactions"
                                                    />
                                                </div>
                                            </Tooltip>
                                        )}                                            
                                        {isDisconnected && (
                                            <Tooltip
                                                target={`message-${data.sid}-menu-toggle`}
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
                                                Access to client data is not available per client request
                                            </Tooltip>
                                        )}
                                    </>
                                )}
                            </div>
                        </div>
                    )}

                    {data.media && (
                        <div className={bubbleClassNames}>
                            <div className="v-flexbox flex-1 margin-right-20">
                                <div className="h-flexbox justify-content-between margin-bottom-5">
                                    <div className="Message-Author">
                                        {!isCurrentUser && fullName}
                                    </div>
                                    {isEmpty(data.media) && !data.isSystemMessage && (
                                        <Date>{data.dateCreated}</Date>
                                    )}
                                </div>

                                {quote && (
                                    <Quote
                                        date={quote.date}
                                        type={quote.type}
                                        onClick={onClickQuote}
                                        title={quoteAuthor.fullName}
                                        className="margin-bottom-5 align-self-start"
                                    >
                                        <div className="line-clamp-2">
                                            {quote.text}
                                        </div>
                                    </Quote>
                                )}

                                <div className="margin-bottom-3">
                                    <Media
                                        url={fileUrl}
                                        data={data.media}
                                        className="Message-MediaFile"
                                    />
                                </div>

                                <div className="h-flexbox justify-content-between">
                                    <Emoji
                                        each={o => {
                                            if (!uniq(reactionIds).includes(o.id)) return false

                                            return {
                                                tooltip: {
                                                    hideArrow: true,
                                                    className: 'MessageReactionDetailsTooltip',
                                                    render: reaction => (
                                                        <ReactionDetails
                                                            message={data}
                                                            reaction={reaction}
                                                        />
                                                    )
                                                },
                                                description: reactionCounts[o.id]
                                            }
                                        }}
                                        onSelect={onEmojiReaction}
                                        className="Message-Reactions"
                                    />
                                </div>
                            </div>

                            <div className="position-relative">
                                {!data.isSystemMessage && (
                                    <>
                                        <Menu
                                            id={`message-${data.sid}-menu-toggle`}
                                            className={cn(
                                                "MessageMenu-Toggle",
                                                { "MessageMenu-Toggle__disconnected": isDisconnected }
                                            )}
                                        />
                                        {!isDisconnected && (
                                            <Tooltip
                                                trigger="focus"
                                                hideArrow={true}
                                                boundariesElement="scrollParent"
                                                target={`message-${data.sid}-menu-toggle`}
                                                className="MessageMenu MessageMenuPopup"
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
                                                <div className="MessageMenuPopup-Header">
                                                    Actions
                                                </div>
                                                <div className="MessageMenuPopup-Body">
                                                    <ul className="MessageMenu-Actions">
                                                        <li onClick={_onReply} className="MessageMenu-Action">
                                                            <Reply className="MessageMenu-ActionIcon"/> Reply
                                                        </li>
                                                    </ul>
                                                    <Emoji
                                                        onSelect={onEmojiReaction}
                                                        className="MessageMenu-Reactions"
                                                    />
                                                </div>
                                            </Tooltip>
                                        )}
                                        {isDisconnected && (
                                            <Tooltip
                                                target={`message-${data.sid}-menu-toggle`}
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
                                                Access to client data is not available per client request
                                            </Tooltip>
                                        )}
                                    </>
                                )}
                            </div>
                        </div>
                    )}
                </div>

                {canSendToDocuTrack && (
                    <IconButton
                        size={24}
                        Icon={Send}
                        shouldHighLight={false}
                        id={`send-message-${data.media.sid}-btn`}
                        tipText={isDisconnected ? "Access to client data is not available per client request" : "Send Document To DocuTrack"}
                        onClick={() => onSend(data)}
                        tipTrigger="hover"
                        tipPlace="top"
                        className="Message-SendBtn"
                        disabled={isDisconnected}
                    />
                )}

                {/* Temporary hidden by CCN-6095 request */}
                {/*{isEdited && !isCurrentUser && (
                    <Pencil className="Message-EditIndicator" />
                )}*/}
            </div>

            {isCurrentUser && !data.isSystemMessage && (
                <OutsideClickListener
                    onClick={() => setIsActionPickerOpen(false)}
                    className="Message-Actions"
                >
                    {/* Temporary hidden by CCN-6095 request */}
                    {/* {data.sid ? (
                        <div
                            onClick={() => setIsActionPickerOpen(state => !state)}
                            className={cn('Message-EditIcon', {
                                'Message-EditIcon_disabled': !data.text,
                                'Message-EditIcon_focused': isActionPickerOpen
                            })}
                        >
                            <Edit />
                        </div>
                    ) : (
                        <div className="Message-ActionsGutter"/>
                    )} */}
                    <div className="Message-ActionsGutter" />

                    {isActionPickerOpen && (
                        <ActionPicker
                            bottom={-24}
                            right={18}
                            options={[{
                                title: 'Edit',
                                onClick: onEdit
                            }]}
                            className="Message-ActionPicker"
                        />
                    )}
                </OutsideClickListener>
            )}

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </div>
    )
}

export default memo(Message)
