class TwilioVideoParticipant {
    #service = null

    constructor(service) {
        this.#service = service
    }

    get sid() {
        return this.#service.sid
    }

    get identity() {
        return this.#service.identity
    }

    get state() {
        return this.#service.state
    }

    get tracks() {
        return this.#service.tracks
    }

    get isSubscribed() {
        return this.#service.isSubscribed
    }

    get audioTracks() {
        return this.#service.audioTracks
    }

    get videoTracks() {
        return this.#service.videoTracks
    }

    on(eventName, cb) {
        return this.#service.on(eventName, cb)
    }

    off(eventName, cb) {
        return this.#service.off(eventName, cb)
    }
}

export default TwilioVideoParticipant