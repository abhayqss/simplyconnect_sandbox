import React, {
    memo,
    useRef,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { first } from 'underscore'

import Draggable from 'react-draggable'

import { useHistory } from 'react-router-dom'

import {
    useAuthUser
} from 'hooks/common'

import {
    useVideo,
    useLocalMedia,
    useConversations,
    useParticipantsMedia,
    useCurrentParticipant,
    useConversationLoading,
    useVideoPopupDOMProperties,
} from 'hooks/business/conversations'

import { withTooltip } from 'hocs'

import { Loader, ErrorViewer } from 'components'

import {
    Avatar,
} from 'components/communication'

import { GroupConversationParticipantPicker } from 'containers/Communication/Messenger'
import { CTMemberCommunicationParticipantPicker } from 'containers/IncidentReports'


import {
    Track,
    Popup,
    CallTime,
    TilesLayout,
    ControlButton,
    ParticipantInfo,
} from 'components/communication/videochat'

import { Conversation } from 'factories'

import { path } from 'lib/utils/ContextUtils'
import {
    ifElse,
    isInteger,
    getInitials,
    getInitialsFromString,
} from 'lib/utils/Utils'
import { RESPONSIVE_BREAKPOINTS } from 'lib/Constants'

import { ReactComponent as Close } from 'images/close-call.svg'
import { ReactComponent as Message } from 'images/message-2.svg'
import { ReactComponent as AddUser } from 'images/add-user-2.svg'
import { ReactComponent as Expand } from 'images/expand.svg'
import { ReactComponent as CameraDisabled } from 'images/camera-disabled.svg'
import { ReactComponent as Camera } from 'images/camera-enabled.svg'
import { ReactComponent as MicrophoneMuted } from 'images/microphone-muted.svg'
import { ReactComponent as Microphone } from 'images/microphone-enabled.svg'

import './Room.scss'

const { Map, List } = require('immutable')

const AddParticipantsButton = withTooltip({
    className: 'VideoChatRoom-Action',
    text: 'You can add up to 20 participants'
})(ControlButton)

const MAX_PARTICIPANTS_COUNT = 20
const { MOBILE_PORTRAIT } = RESPONSIVE_BREAKPOINTS

function Room({
    room,
    call,
    onClose,
    incidentReport
}) {
    const {
        size,
        isMobileView,
        isDesktopView
    } = useVideoPopupDOMProperties()

    const popupRef = useRef()
    const localParticipantRef = useRef()

    const [error, setError] = useState(null)
    const [isMuted, setIsMuted] = useState(false)
    const [isMinimized, setMinimized] = useState(false)
    const [conversation, setConversation] = useState(null)
    const [isCameraOff, setIsCameraOff] = useState(!call.isVideoCall)
    const [isLoadingNewParticipants, setIsLoadingNewParticipants] = useState(false)
    const [isParticipantPickerOpen, setIsParticipantPickerOpen] = useState(false)

    const [localTracks, setLocalTracks] = useState(List())
    const [remoteTracks, setRemoteTracks] = useState(Map())

    const authUser = useAuthUser()
    const history = useHistory()

    const { addParticipants, addDeclinedParticipant } = useVideo()

    const participants = useMemo(() => call.callees.push(call.caller), [call.callees, call.caller])

    const participantIds = useMemo(() => (
        participants.map(o => o.employeeId)
    ), [participants])

    const { emit, getBySid } = useConversations()

    const currentParticipant = useCurrentParticipant(conversation)
    const isCurrentParticipantOwner = currentParticipant?.isConversationOwner

    const isGrouped = participants.size > 2
    const isCaller = authUser.id === call.caller.employeeId

    const canAddParticipants = (isCurrentParticipantOwner || (!isGrouped && isCaller)) && !isLoadingNewParticipants

    const localParticipantInitialPosition = isMobileView ? { top: 0, left: 0 } : { right: 0, bottom: 0 }

    const otherParticipants = useMemo(() => {
        return participants
            .filter(o => o.employeeId !== authUser.id)
            .filter(o => !call.declinedIdentities.includes(o.identity))
            .filter(o => !call.timeoutIdentities.includes(o.identity))
            .filter(o =>
                call.onCallIdentities.includes(o.identity)
                || call.pendingIdentities.includes(o.identity)
            )

    }, [authUser.id, call, participants])

    const otherParticipant = isGrouped ? null : otherParticipants.first()

    const { toggleAudio, toggleVideo } = useLocalMedia(room, {
        onTrackConnected: useCallback((track) => {
            setLocalTracks(tracks => tracks.push(track))
        }, []),
        onTrackDisconnected: useCallback((trackPublications) => {
            const trackPublication = first(trackPublications)

            setLocalTracks(tracks => tracks.filter(track => {
                return track !== trackPublication.track
            }))
        }, [])
    })

    const removeParticipant = useCallback(identity => {
        addDeclinedParticipant(identity)
        setRemoteTracks(tracks => tracks.remove(identity))
    }, [addDeclinedParticipant])

    const detachTrack = useCallback((identity, trackSid) => {
        setRemoteTracks(tracks => {
            return tracks.update(identity, media => media.delete(trackSid))
        })
    }, [])

    const mediaState = useParticipantsMedia(room, {
        onTrackConnected: useCallback((identity, track) => {
            const setMedia = ifElse(
                (media, track) => media.has(track.sid),
                media => media,
                (media, track) => media.set(track.sid, track),
            )

            setRemoteTracks(ifElse(
                tracks => tracks.has(identity),
                tracks => tracks.update(identity, media => setMedia(media, track)),
                tracks => tracks.set(identity, Map([[track.sid, track]]))
            ))
        }, []),
        onTrackDisconnected: detachTrack,
        onParticipantDisconnected: removeParticipant
    })

    const expandPopup = () => {
        setMinimized(false)

        if (isMobileView) {
            popupRef.current.toggleMaximized()
        }
    }

    const toggleCamera = () => {
        toggleVideo()
        setIsCameraOff(prevState => !prevState)
    }

    const toggleMicrophone = () => {
        toggleAudio()
        setIsMuted(prevState => !prevState)
    }

    const onCancelMinimizing = useCallback(() => {
        setMinimized(false)
    }, [])

    const navigateConversation = () => {
        history.push(path('/chats'), {
            conversationSid: call.conversationSid,
            shouldSelectConversation: true
        })
        setMinimized(true)

        if (isMobileView) {
            popupRef.current.toggleMaximized()
        }
    }

    const onAddParticipant = () => setIsParticipantPickerOpen(true)

    const onCloseParticipantPicker = useCallback(() => {
        setIsParticipantPickerOpen(false)
    }, [])

    const onCompleteParticipantPicker = useCallback(async value => {
        const { clientId, contactIds, groupName } = value

        if (MAX_PARTICIPANTS_COUNT - participants.size < contactIds.length) {
            setError(Error('The limit of participants on this call has been reached.'))

            return
        }

        emit('conversationLoading', call.conversationSid)

        try {
            onCloseParticipantPicker()

            await addParticipants({
                roomSid: room.sid,
                friendlyName: groupName,
                employeeIds: contactIds,
                participatingClientId: clientId,
            })
        } catch (e) {
            setError(e)
        } finally {
            emit('conversationLoading', null)
        }
    }, [emit, call, addParticipants, participants, onCloseParticipantPicker, room])

    useConversationLoading((conversationSid) => {
        setIsLoadingNewParticipants(conversationSid === call.conversationSid)
    }, [call])

    useEffect(() => {
        getBySid(call.conversationSid)
            .then(Conversation)
            .then(setConversation)
    }, [call.conversationSid, getBySid])

    if (otherParticipants.isEmpty()) return null

    return (
        <Popup
            isDraggable
            ref={popupRef}
            dragBounds=".App"
            offsetParent=".App"
            isResizable={!isMinimized && isDesktopView}
            hasMinimizeButton={!isMinimized}
            hasMaximizeButton={!isMobileView}
            size={isMinimized ? 'auto' : size}
            width={isMinimized ? MOBILE_PORTRAIT : undefined}
            onDoubleClickHeader={onCancelMinimizing}
            onClickMinimizeBtn={() => setMinimized(true)}
            defaultPosition={isMinimized ? 'bottom-right' : 'center'}
            isMaximizedByDefault={isMobileView}
            className={cn(
                'VideoChatRoomPopup',
                { VideoChatRoomPopup_minimized: isMinimized }
            )}
            handle={isMinimized ? '.VideoChatRoom-DraggableHandler' : '.VideoChatPopup-Header'}
        >
            <div className="VideoChatRoom">
                <div className="VideoChatRoom-Header">
                    {isGrouped && (
                        <div className="VideoChatRoom-Title">
                            Group call
                        </div>
                    )}

                    {!isGrouped && (
                        <ParticipantInfo
                            className="VideoChatRoom-Title"
                            participant={otherParticipant}
                            isMuted={!mediaState.get(otherParticipant.identity)?.audio}
                        />
                    )}

                    <div className="VideoChatRoom-CallTime">
                        <CallTime />
                    </div>
                </div>

                <div className="VideoChatRoom-Body">
                    <Draggable
                        nodeRef={localParticipantRef}
                        bounds=".VideoChatRoom"
                        offsetParent={document.querySelector('.App')}
                        disabled={isDesktopView}
                        handle=".VideoChatRoom-LocalParticipantOverlay"
                    >
                        <div
                            ref={localParticipantRef}
                            style={localParticipantInitialPosition}
                            className={cn(
                                'VideoChatRoom-LocalParticipant',
                                { 'VideoChatRoom-Participant_video-streaming': !isCameraOff }
                            )}
                            id="local-participant"
                        >
                            {localTracks.map(track => {
                                return (
                                    <Track
                                        key={track.id}
                                        track={track}
                                        className="VideoChatRoom-ParticipantTrack"
                                    />
                                )
                            })}
                            {!isCameraOff && (
                                <div className="VideoChatRoom-LocalParticipantOverlay" ref={localParticipantRef} />
                            )}
                        </div>
                    </Draggable>

                    <div className="flex-1 h-100">
                        <div className="VideoChatRoom-MiniView">
                            <div className="VideoChatRoom-MiniViewSection">
                                <div className="VideoChatRoom-ShortInfo">
                                    <div className="padding-top-10 padding-bottom-10">
                                        <Avatar
                                            id={otherParticipants?.avatarId}
                                            className="VideoChatRoom-Avatar"
                                        >
                                            {isGrouped && call.friendlyName && (
                                                getInitialsFromString(call.friendlyName)
                                            )}

                                            {isGrouped && !call.friendlyName && (
                                                getInitials(otherParticipants.first())
                                            )}

                                            {!isGrouped && (
                                                getInitials(otherParticipant)
                                            )}

                                        </Avatar>
                                    </div>
                                    <div className="padding-top-10 padding-bottom-10">
                                        <div className="VideoChatRoom-CallTime">
                                            <CallTime />
                                        </div>
                                    </div>
                                </div>

                                <div className="padding-top-10 padding-bottom-10 margin-right-15">
                                    <ControlButton
                                        size={23}
                                        color="red"
                                        Icon={Close}
                                        onClick={onClose}
                                        className="VideoChatRoom-Action VideoChatRoom-CloseCallBtn"
                                    />
                                </div>
                            </div>

                            <div className="VideoChatRoom-MiniViewSection VideoChatRoom-DraggableHandler"></div>

                            <div className="VideoChatRoom-MiniViewSection">
                                <div className="VideoChatRoom-ActionDivider" />
                                <div className="margin-left-10 padding-top-10 padding-bottom-10">
                                    <ControlButton
                                        size={23}
                                        color={false}
                                        Icon={Expand}
                                        name="VideoChatRoom__ExpandBtn"
                                        tipText="Expand"
                                        onClick={expandPopup}
                                        className="VideoChatRoom-Action VideoChatRoom-ExpandBtn"
                                    />
                                </div>
                            </div>
                        </div>

                        <TilesLayout className="VideoChatRoom-Participants">
                            {otherParticipants.map(o => {
                                const media = mediaState.get(o.identity)
                                const tracks = remoteTracks.get(o.identity)

                                const isPending = !tracks || call.pendingIdentities.includes(o.identity)

                                return (
                                    <div
                                        id={o.identity}
                                        key={o.identity}
                                        className={cn(
                                            "VideoChatRoom-Participant",
                                            { 'VideoChatRoom-Participant_video-streaming': media?.video },
                                            { 'VideoChatRoom-Participant_audio-streaming': media?.audio },
                                        )}
                                    >
                                        {isPending && (
                                            <Loader hasBackdrop />
                                        )}

                                        <Avatar
                                            size="50%"
                                            id={o.avatarId}
                                            className="VideoChatRoom-ParticipantAvatar"
                                        >
                                            {getInitials(o)}
                                        </Avatar>

                                        {isGrouped && (
                                            <ParticipantInfo
                                                className="VideoChatRoom-ParticipantInfo"
                                                participant={o}
                                                isMuted={!media?.audio}
                                            />
                                        )}

                                        {tracks?.valueSeq().map((track) => {
                                            return (
                                                <Track
                                                    track={track}
                                                    key={track.sid}
                                                    className="VideoChatRoom-ParticipantTrack"
                                                />
                                            )
                                        })}
                                    </div>
                                )
                            })}
                        </TilesLayout>
                    </div>

                    <div className="VideoChatRoom-Actions">
                        <ControlButton
                            size={28}
                            Icon={isCameraOff ? CameraDisabled : Camera}
                            onClick={toggleCamera}
                            className="VideoChatRoom-Action"
                        />
                        <ControlButton
                            size={28}
                            Icon={isMuted ? MicrophoneMuted : Microphone}
                            onClick={toggleMicrophone}
                            className="VideoChatRoom-Action"
                        />
                        <ControlButton
                            size={28}
                            Icon={Message}
                            onClick={navigateConversation}
                            className="VideoChatRoom-Action"
                        />
                        {canAddParticipants && (
                            <AddParticipantsButton
                                size={28}
                                Icon={AddUser}
                                onClick={onAddParticipant}
                                isTooltipEnabled={participants.size === MAX_PARTICIPANTS_COUNT}
                                className={cn(
                                    'VideoChatRoom-Action',
                                    'VideoChatRoom-AddParticipantsButton',
                                    { 'VideoChatRoom-Action_disabled': participants.size === MAX_PARTICIPANTS_COUNT }
                                )}
                            />
                        )}
                        <ControlButton
                            size={28}
                            color="red"
                            Icon={Close}
                            onClick={onClose}
                            className="VideoChatRoom-Action"
                        />
                    </div>
                </div>
            </div>

            <GroupConversationParticipantPicker
                isOpen={isParticipantPickerOpen && conversation && !incidentReport}
                excludedContactIds={participantIds}
                areClientsExcluded={isInteger(conversation?.participatingClientId)}
                groupName={conversation?.friendlyName}
                onClose={onCloseParticipantPicker}
                onComplete={onCompleteParticipantPicker}
            />

            {isParticipantPickerOpen && conversation && incidentReport && (
                <CTMemberCommunicationParticipantPicker
                    isOpen
                    isDisabledGroupName
                    clientId={incidentReport?.clientId}
                    communityId={incidentReport?.communityId}
                    communicationType="video"
                    excludedContactIds={participantIds}
                    onClose={onCloseParticipantPicker}
                    groupName={conversation?.friendlyName}
                    onComplete={onCompleteParticipantPicker}
                />
            )}

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </Popup>
    )
}

export default memo(Room)