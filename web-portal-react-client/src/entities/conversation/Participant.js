
export default class Participant {
    #provider
    
    constructor(provider) {
        this.#provider = provider
    }

    get isConversationOwner () {    
        return this.#provider.isConversationOwner
    }

    get identity () {
        return this.#provider.identity
    }

    get dateCreated () {
        return this.#provider.dateCreated
    }

    get dateUpdated () {
        return this.#provider.dateUpdated
    }

    get sid() {
        return this.#provider.sid
    }

    get isTyping () {
        return this.#provider.isTyping
    }

    get lastReadMessageIndex () {
        return this.#provider.lastReadMessageIndex
    }

    get type () {
        return this.#provider.type
    }

    getProvider() {
        return this.#provider
    }
}