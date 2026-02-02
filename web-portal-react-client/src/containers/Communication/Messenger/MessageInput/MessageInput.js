import React, {
    memo,
    useRef,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'
import { filesize } from 'filesize'

import { map } from 'underscore'

import { useSelector } from 'react-redux'

import {
    Input,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import {
    Loader,
    ErrorViewer
} from 'components'

import {
    FileButton,
    IconButton
} from 'components/buttons'

import {
    Quote,
    FilePreview,
    AudioProgress
} from 'components/communication/messenger'

import { useAudioRecorder } from 'hooks/common'
import { useConversations } from 'hooks/business/conversations'

import { ReactComponent as Submit } from 'images/send-1.svg'
import { ReactComponent as Voice } from 'images/microphone-1.svg'
import { ReactComponent as Folder } from 'images/folder-1.svg'
import { ReactComponent as Picture } from 'images/picture-1.svg'
import { ReactComponent as Delete } from 'images/delete-file.svg'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import {
    QUOTE_TYPES
} from 'components/communication/messenger/Quote/lib/Constants'

import {
    getQuoteTypeByMimeType
} from 'components/communication/messenger/Quote/lib/Utils'

import { UploadFromDocuTrackEditor } from '../'

import './MessageInput.scss'

const {
    JPG, PNG, MP3, MPEG, AMR, MP4, PDF, DOC, DOCX
} = ALLOWED_FILE_FORMATS

const placeholder = 'Type a message'
const RECORD_MESSAGE_DISABLED_HINT_TEXT = 'Record message disabled. To grant Simply Connect permission to access your microphone, go to your browser settings > Advanced > Content Settings > Microphone.'

const NOT_ALLOWED_ERROR = 'NotAllowedError'

const BIG_FILE_ERROR_TEXT = 'File exceeds maximum allowed size (150 Mb). Please choose another file.'
const INVALID_FILE_FORMAT_ERROR_TEXT = 'File type is not supported. Supported types: JPG, PNG, MP3, AMR, MP4, PDF, and Word'

const ENTER_KEYS = [10, 13]

const MAX_FILE_SIZE_MB = 150

function getFileSizeMB(size) {
    return filesize(size, { output: 'object', exponent: 2 }).value
}

function isAllowedFileMimeType(type) {
    const allowedMimeTypes = map(
        [JPG, PNG, MP3, MPEG, AMR, MP4, PDF, DOC, DOCX],
        format => ALLOWED_FILE_FORMAT_MIME_TYPES[format]
    )
    return allowedMimeTypes.includes(type)
}

function TextInput({
    value,
    onEnterPress,
    onCtrlEnterPress,
    ...props
}) {
    const ref = useRef()

    function onKeyPress(event) {
        let isEnterPressed = ENTER_KEYS.includes(event.charCode)
        let isShiftOrCtrl = event.ctrlKey || event.shiftKey
        let isMobileView = window.innerWidth <= 1024

        if (isEnterPressed && !isShiftOrCtrl && !isMobileView) {
            event.preventDefault()
            onEnterPress(event)
        }

        if (isEnterPressed && isShiftOrCtrl) {
            event.preventDefault()
            onCtrlEnterPress(event)
        }
    }

    useEffect(function resize() {
        let node = ref.current

        if (node) {
            node.style.height = '24px'
            node.style.height = node.scrollHeight + 'px'
        }
    })

    return (
        <Input
            innerRef={ref}
            type="textarea"
            value={value}
            className="MessageInput-TextInput flex-1"
            onKeyPress={onKeyPress}
            {...props}
        />
    )
}

function MessageInput(
    {
        message,
        conversation,
        isDisabled,
        isDisconnected,
        isReplying,
        onSubmit,
        onCancel,
        onCancelReplying,
        className
    }
) {
    const [text, setText] = useState('')
    const [file, setFile] = useState(null)
    const [error, setError] = useState(null)
    const [isUploading, setUploading] = useState(false)
    const [hasAudioPermissions, setHasAudioPermissions] = useState(true)
    const [isUploadFromDocuTrackEditorOpen, toggleUploadFromDocuTrackEditor] = useState(false)

    let isEditing = !!message && !isReplying

    const connectionStatus = useSelector(
        state => state.conversations.connectionStatus
    )

    const { isDocuTrackEnabled } = useSelector(state => (
        state.auth.login.user.data
    )) ?? {}

    const users = useSelector(state => state.conversations.users.data)

    const messageAuthor = message && users.get(message.author)

    let fileUrl = useMemo(() => file ? URL.createObjectURL(file) : null, [file])

    let { startRecording, stopRecording, isRecording } = useAudioRecorder({
        title: '',
        onFailRecording: useCallback((error) => {
            if (error.name === NOT_ALLOWED_ERROR) {
                setHasAudioPermissions(false)
            }
        }, [])
    })

    let hasFocus = file || isRecording
    let isAbleToSubmit = text.length || isReplying || isRecording || file   

    const isVoiceRecordDisabled = 
        !hasAudioPermissions
        || isEditing
        || file
        || isUploading
        || isDisabled
        || isDisconnected

    const isDocuTrackDisabled = 
        isRecording
        || isEditing
        || isUploading
        || isDisabled
        || isDisconnected
        || file

    const {
        sendMessage,
        updateMessage,
    } = useConversations()

    async function send(content, attributes) {
        if (isEditing) {
            return updateMessage(conversation, message, content)
        } else {
            return sendMessage(conversation, content, attributes)
        }
    }

    async function submit() {
        if (
            isDisabled
            || isDisconnected
            || connectionStatus !== 'connected'
        ) return

        setText('')

        let attributes
        let content = text
        let isMedia = isRecording || file

        if (isRecording) {
            let record = await stopRecording()

            content = record
        } else if (file) {
            content = file
        }

        if (isMedia) {
            setUploading(true)
        }

        if (isReplying) {
            attributes = {
                quote: {
                    text: message.text,
                    messageSid: message.sid,
                    authorIdentity: message.author,
                    date: message.dateCreated.getTime(),
                    type: getQuoteTypeByMimeType(message.media?.type) ?? QUOTE_TYPES.TEXT
                }
            }
        }

        if (connectionStatus === 'connected') {
            await send(content, attributes)
        }

        if (isMedia) {
            setFile(null)
            setUploading(false)
        }

        onSubmit(content)
    }

    function onVoiceRecord() {
        !isVoiceRecordDisabled && startRecording()
    }

    function cancelRecording() {
        stopRecording()
    }

    function finishRecording() {
        submit()
    }

    function onEnterPress() {
        text.trim() && submit()
    }

    function onCtrlEnterPress(e) {
        let node = e.target
        let position = node.selectionEnd

        setText(
            value => value.substring(0, position) + '\n' + value.substring(position),
        )

        setTimeout(() => {
            node.selectionEnd = position + 1
        }, 0)
    }

    const clearError = useCallback(() => {
        setError(null)
    }, [])

    const onChangeFile = useCallback(file => {
        if (!isAllowedFileMimeType(file.type)) {
            setError({ message: INVALID_FILE_FORMAT_ERROR_TEXT })
        } else if (MAX_FILE_SIZE_MB < getFileSizeMB(file.size)) {
            setError({ message: BIG_FILE_ERROR_TEXT })
        } else {
            setFile(file)
        }
    }, [])

    const onOpenUploadFromDocuTrackEditor = useCallback(() => {
        !isDocuTrackDisabled && toggleUploadFromDocuTrackEditor(true)
    }, [isDocuTrackDisabled])

    const onCloseUploadFromDocuTrackEditor = useCallback(() => {
        toggleUploadFromDocuTrackEditor(false)
    }, [])

    useEffect(() => {
        if (isEditing) {
            setText(message?.text ?? '')
        }
    }, [message, isEditing])

    return (
        <div className={cn('MessageInput', className)} id="conversation-messenger">
            <div className={cn(
                'MessageInput-RichTextarea flex-1',
                { 'MessageInput-RichTextarea_focus-within': hasFocus },
                { 'MessageInput-RichTextarea_isRecording': isRecording },
                { 'MessageInput-RichTextarea_isEditing': isEditing },
                { 'MessageInput-RichTextarea_isAttachingFile': file },
            )}>
                <div className="MessageInput-MediaActions">
                    <div className="h-flexbox">
                        <FileButton
                            id="add-files-btn"
                            Icon={Picture}
                            value={file}
                            tooltip="Add files"
                            onChangeFiles={onChangeFile}
                            className={cn('MessageInput-MediaAction', {
                                'MessageInput-MediaAction_disabled': (
                                    isRecording || isEditing || isUploading || isDisabled || isDisconnected
                                )
                            })}
                        />

                        {isDocuTrackEnabled && (
                            <IconButton
                                size={24}
                                Icon={Folder}
                                shouldHighLight={false}
                                tipTrigger="hover"
                                tipPlace="top"
                                id="upload-from-docutrack-btn"
                                tipText={!isDocuTrackDisabled && "Upload Document from DocuTrack"}
                                disabled={isDocuTrackDisabled}
                                onClick={onOpenUploadFromDocuTrackEditor}
                                className="MessageInput-MediaAction margin-left-16"
                            />
                        )}

                        <IconButton
                            size={24}
                            Icon={Voice}
                            shouldHighLight={false}
                            id="voice-recording-btn"
                            tipTrigger="hover"
                            tipPlace="top"
                            hasTip={!isDisabled && !isDisconnected}
                            tipText={hasAudioPermissions ? 'Record a message' : RECORD_MESSAGE_DISABLED_HINT_TEXT}
                            disabled={isVoiceRecordDisabled}
                            onClick={onVoiceRecord}
                            className={cn('MessageInput-MediaAction margin-left-16', {
                                'MessageInput-MediaAction_active': isRecording
                            })}
                        />

                        {/* <Note
                        onClick={onNote}
                        className={cn('MessageInput-MediaAction margin-left-16', {
                            'MessageInput-MediaAction_disabled': isRecording || isEditMode
                        })}
                    /> */}
                    </div>
                </div>

                {isRecording && (
                    <AudioProgress
                        onCancel={cancelRecording}
                        onFinish={finishRecording}
                        className="MessageInput-Audio"
                    />
                )}

                {!isRecording && (
                    <div className="MessageInput-Text">
                        <div className="flex-1">
                            {isReplying && message && (
                                <Quote
                                    hasCloseBtn
                                    date={message.dateCreated}
                                    title={messageAuthor.fullName}
                                    className="margin-left-12 margin-top-12"
                                    type={getQuoteTypeByMimeType(message.media?.type) ?? QUOTE_TYPES.TEXT}
                                    onClose={onCancelReplying}
                                >
                                    {message.text}
                                </Quote>
                            )}

                            <TextInput
                                value={text}
                                onChange={e => setText(e.target.value)}
                                onEnterPress={onEnterPress}
                                disabled={file || isUploading || isDisabled || isDisconnected}
                                placeholder={(!file && !isUploading) ? placeholder : ''}
                                onCtrlEnterPress={onCtrlEnterPress}
                            />
                        </div>

                        {isEditing && (
                            <Delete
                                className="MessageInput-CancelEditing"
                                onClick={onCancel}
                            />
                        )}

                        {!file && isUploading && (
                            <Loader className="MessageInput-Loader" />
                        )}
                    </div>
                )}

                {file && (
                    <div className="MessageInput-Previews">
                        <div
                            key={`${file.name}.${file.type}`}
                            className="MessageInput-Preview"
                        >
                            <FilePreview
                                data={file}
                                url={fileUrl}
                                isLoading={isUploading}
                                className="MessageInput-FilePreview"
                                renderIcon={() => (
                                    <Delete
                                        className="MessageInput-DeleteIcon"
                                        onClick={() => setFile(null)}
                                    />
                                )}
                            />
                        </div>
                    </div>
                )}
            </div>

            <IconButton
                size={24}
                Icon={Submit}
                shouldHighLight={false}
                tipTrigger="hover"
                tipPlace="top"
                id="send-message-btn"
                tipText="Send message"
                onClick={submit}
                className={cn(
                    'MessageInput-SubmitAction',
                    { 'MessageInput-SubmitAction_active': isAbleToSubmit && !isDisabled && !isDisconnected }
                )}
            />

            {isDisabled && (
                <Tooltip
                    placement="top"
                    target="conversation-messenger"
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
                    The user is no longer with Simply Connect. Messaging is disabled.
                </Tooltip>
            )}
            {!isDisabled && isDisconnected && (
                <Tooltip
                    placement="top"
                    target="conversation-messenger"
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

            <UploadFromDocuTrackEditor
                conversationSid={conversation?.sid}
                isOpen={isUploadFromDocuTrackEditorOpen}
                onClose={onCloseUploadFromDocuTrackEditor}
                onUploadSuccess={onCloseUploadFromDocuTrackEditor}
            />

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={clearError}
                />
            )}
        </div>
    )
}

export default memo(MessageInput)
