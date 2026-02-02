class VideoParticipant {
    #provider = null

    constructor(provider) {
        this.#provider = provider
    }

    get sid() {
        return this.#provider.sid
    }

    get identity() {
        return this.#provider.identity
    }

    get state() {
        return this.#provider.state
    }

    get tracks() {
        return this.#provider.tracks
    }

    get isSubscribed() {
        return this.#provider.isSubscribed
    }

    get audioTracks() {
        return this.#provider.audioTracks
    }

    get videoTracks() {
        return this.#provider.videoTracks
    }

    on(eventName, cb) {
        return this.#provider.on(eventName, cb)
    }

    off(eventName, cb) {
        return this.#provider.off(eventName, cb)
    }
}

export default VideoParticipant