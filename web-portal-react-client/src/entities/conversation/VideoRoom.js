import memoize from 'memoize-one'

import VideoParticipant from './VideoParticipant'

class VideoRoom {
    #provider = null

    constructor(provider) {
        this.#provider = provider

        this.getParticipantList = memoize(participants => provider.getParticipantList(participants, VideoParticipant))
    }

    get sid() {
        return this.#provider.sid
    }

    get state() {
        return this.#provider.state
    }

    get name() {
        return this.#provider.name
    }

    get isRecording() {
        return this.#provider.isRecording
    }

    get localParticipant() {
        return this.#provider.localParticipant
    }

    get participants() {
        return this.getParticipantList(this.#provider.participants)
    }

    on(eventName, cb) {
        return this.#provider.on(eventName, cb)
    }

    off(eventName, cb) {
        return this.#provider.off(eventName, cb)
    }

    getProvider() {
        return this.#provider
    }
}

export default VideoRoom
