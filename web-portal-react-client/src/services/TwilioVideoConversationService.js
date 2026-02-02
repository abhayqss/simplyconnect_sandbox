import { connect, createLocalAudioTrack, createLocalVideoTrack } from 'twilio-video'

function publishTrack(room, track) {
    room.localParticipant.publishTrack(track)
}
class TwilioVideoConversationService {
    async connect({ token, isVideoCall, ...options }) {
        const room = await connect(token, {
            ...options,
            tracks: [],
            audio: true,
            video: true,
        })

        let localVideoTrack
        let localAudioTrack = await this.createLocalTrack('audio')

        if (isVideoCall) {
            localVideoTrack = await this.createLocalTrack('video')
        }

        if (localVideoTrack) {
            publishTrack(room, localVideoTrack)
        }

        if (localAudioTrack) {
            publishTrack(room, localAudioTrack)
        }

        return room
    }

    disconnect(room) {
        room.getProvider().disconnect()
    }

    stopAllTracks(room) {
        room.getProvider().localParticipant.tracks.forEach(publication => {
            publication.track.stop()
            publication.track.detach()
            publication.unpublish()
        })
    }

    async createLocalTrack(type) {
        let track

        switch (type) {
            case 'audio':
                try {
                    track = await createLocalAudioTrack()
                } catch (error) {
                    console.warn('No audio input detected')
                }
                break

            case 'video':
                try {
                    track = await createLocalVideoTrack()
                } catch (error) {
                    console.warn('No web camera detected')
                }
                break

            default:
                throw Error('Unknown media type for a new local track creation')
        }

        return track
    }
}

export default TwilioVideoConversationService
