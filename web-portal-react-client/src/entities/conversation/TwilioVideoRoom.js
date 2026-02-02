import TwilioVideoParticipant from './TwilioVideoParticipant'

class TwilioVideoRoom {
    #service = null

    constructor(provider) {
        this.#service = provider
    }

    get sid() {
        return this.#service.sid
    }

    get state() {
        return this.#service.state
    }

    get name() {
        return this.#service.name
    }

    get participants() {
        return this.#service.participants
    }

    get isRecording() {
        return this.#service.isRecording
    }

    get localParticipant() {
        return this.#service.localParticipant
    }

    on(eventName, cb) {
        return this.#service.on(eventName, cb)
    }

    off(eventName, cb) {
        return this.#service.off(eventName, cb)
    }

    disconnect() {
        return this.#service.disconnect()
    }

    stopTracks() {
        this.localParticipant.tracks.forEach(track => {
            track.stop()
            track.detach()
        })
        this.localParticipant.unpublishTracks(
            this.localParticipant.tracks
        )
    }

    getParticipantList(participants, ParticipantClass) {
        let result = []

        participants.forEach(participant => {
            result.push(new ParticipantClass(new TwilioVideoParticipant(participant)))
        })

        return result
    }
}

export default TwilioVideoRoom