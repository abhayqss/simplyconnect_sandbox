import { useEffect, useCallback } from 'react'

import { VideoConversationService } from 'factories'

const service = VideoConversationService()

const mapToArray = map => {
    const array = []

    map.forEach(v => array.push(v))

    return array
}

function unpublishPublication(publication) {
    publication.track.stop()
    publication.track.detach()
    publication.unpublish()
}

function useLocalMedia(room, { onTrackConnected, onTrackDisconnected }) {
    const TrackSwitch = useCallback((type) => {
        return async function toggle() {
            const tracksMap = type === 'video'
                ? room.localParticipant.videoTracks
                : room.localParticipant.audioTracks

            const trackPublications = mapToArray(tracksMap)

            if (trackPublications.length) {
                trackPublications.forEach(unpublishPublication)
                onTrackDisconnected(trackPublications)
            } else {
                room.localParticipant.publishTrack(await service.createLocalTrack(type))
            }
        }
    }, [room, onTrackDisconnected])

    const toggleAudio = useCallback(TrackSwitch('audio'), [])

    const toggleVideo = useCallback(TrackSwitch('video'), [])

    useEffect(() => {
        room.localParticipant.tracks.forEach(publication => {
            onTrackConnected(publication.track)
        })
    }, [onTrackConnected, toggleVideo, room])

    useEffect(() => {
        const onTrackPublished = publication => {
            onTrackConnected(publication.track)
        }

        room.localParticipant.on('trackPublished', onTrackPublished)

        return () => room.localParticipant.on('trackPublished', onTrackPublished)
    }, [room, onTrackConnected])

    useEffect(() => {
        const onDisconnected = localParticipant => {
            localParticipant.tracks.forEach(unpublishPublication)
        }

        room.localParticipant.on('disconnected', onDisconnected)

        return () => room.localParticipant.on('disconnected', onDisconnected)
    }, [room])

    return { toggleAudio, toggleVideo }
}

export default useLocalMedia
