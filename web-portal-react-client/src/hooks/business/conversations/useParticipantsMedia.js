import {
    useEffect,
    useCallback
} from 'react'

import { ifElse } from 'lib/utils/Utils'

import useParticipantsMediaState from './useParticipantsMediaState'

function useParticipantsMedia(room, {
    onTrackConnected,
    onTrackDisconnected,
    onParticipantDisconnected
}) {
    const {
        state,
        enableAudio,
        disableAudio,
        enableVideo,
        disableVideo,
        startReconnecting,
        stopReconnecting,
        addParticipant: addParticipantToMediaState,
        removeParticipant: removeParticipantFromMediaState,
    } = useParticipantsMediaState(room.participants)

    const enableTrack = useCallback(participant => ifElse(
        publication => publication.kind === 'video',
        () => enableVideo(participant.identity),
        () => enableAudio(participant.identity)
    ), [enableAudio, enableVideo])

    const enableAndConnectTrack = useCallback(participant => ifElse(
        publication => publication.kind === 'video',
        (publication) => {
            enableVideo(participant.identity)
            onTrackConnected(participant.identity, publication.track)
        },
        (publication) => {
            enableAudio(participant.identity)
            onTrackConnected(participant.identity, publication.track)
        },
    ), [enableAudio, enableVideo, onTrackConnected])
    
    const disableAndDisconnectTrack = useCallback(participant => ifElse(
        publication => publication.kind === 'video',
        (publication) => {
            disableVideo(participant.identity)
            onTrackDisconnected(participant.identity, publication.trackSid)
        },
        (publication) => {
            disableAudio(participant.identity)
            onTrackDisconnected(participant.identity, publication.trackSid)
        },
    ), [disableAudio, disableVideo, onTrackDisconnected])

    const onDisconnectedParticipant = useCallback(participant => {
        const disconnectTrack = publication => {
            if (publication.track) {
                onTrackDisconnected(participant.identity, publication.track)
            }
        }

        return () => {
            participant.tracks.forEach(disconnectTrack)
            removeParticipantFromMediaState(participant)
        }
    }, [removeParticipantFromMediaState, onTrackDisconnected])

    useEffect(function watchConnectedTracks() {
        const participantsEvents = new Map()

        function onConnected(participant) {
            const onTrackEnabled = enableAndConnectTrack(participant)
            const onTrackDisabled = disableAndDisconnectTrack(participant)
            const onDisconnected = onDisconnectedParticipant(participant)
            const onReconnecting = () => startReconnecting(participant.identity)
            const onReconnected = () => stopReconnecting(participant.identity)
            const onTrackSubscribed = (_, publication) =>  onTrackEnabled(publication)
            const onTrackUnsubscribed = (_, publication) =>  onTrackDisabled(publication)

            const eventHandlers = new Map([
                ['trackSubscribed', onTrackSubscribed],
                ['trackUnsubscribed', onTrackUnsubscribed],
                ['disconnected', onDisconnected],
                ['reconnecting', onReconnecting],
                ['reconnected', onReconnected],
            ])

            participant.tracks.forEach(publication => {
                if (publication.isSubscribed) {
                    onTrackEnabled(publication)
                    onTrackConnected(participant.identity, publication.track)
                }
            })

            eventHandlers.forEach((handler, event) => {
                participant.on(event, handler)
            })

            participantsEvents.set(participant.identity, eventHandlers)

            addParticipantToMediaState(participant)
        }

        function onDisconnected(participant) {
            onParticipantDisconnected(participant.identity)
        }

        room.participants.forEach(onConnected)

        room.on('participantConnected', onConnected)
        room.on('participantDisconnected', onDisconnected)

        return function unsubscribe() {
            room.participants.forEach(participant => {
                const eventHandlers = participantsEvents.get(participant.identity)

                if (eventHandlers) {
                    eventHandlers.forEach((handler, event) => {
                        participant.off(event, handler)
                    })
                }
            })

            room.off('participantConnected', onConnected)
            room.off('participantDisconnected', onDisconnected)
        }
    }, [
        room,
        enableTrack,
        onTrackConnected,
        startReconnecting,
        stopReconnecting,
        onDisconnectedParticipant,
        enableAndConnectTrack,
        onParticipantDisconnected,
        disableAndDisconnectTrack,
        addParticipantToMediaState,
    ])

    return state
}

export default useParticipantsMedia