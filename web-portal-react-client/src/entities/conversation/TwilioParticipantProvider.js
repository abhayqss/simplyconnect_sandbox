export default class TwilioParticipantProvider {
    #service = null

    constructor(data) {
        this.#service = data
    }

    get isConversationOwner () {
        return this.#service.attributes.isOwner
    }

    get identity () {
        return this.#service.identity
    }

    get dateCreated () {
        return this.#service.dateCreated
    }

    get dateUpdated () {
        return this.#service.dateUpdated
    }

    get sid() {
        return this.#service.sid
    }

    get isTyping () {
        return this.#service.isTyping
    }

    get lastReadMessageIndex () {
        return this.#service.lastReadMessageIndex
    }

    get type () {
        return this.#service.type
    }

    remove() {
        return this.#service.remove()
    }
}