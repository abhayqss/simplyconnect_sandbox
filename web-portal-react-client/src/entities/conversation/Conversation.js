export default class Conversation {
    #provider

    constructor(provider) {
        this.#provider = provider
    }

    get sid() {
        return this.#provider.sid
    }

    get status() {
        return this.#provider.status
    }

    get state() {
        return this.#provider.state
    }

    get friendlyName() {
        return this.#provider.friendlyName
    }

    get lastMessage() {
        return this.#provider.lastMessage
    }

    get notificationLevel() {
        return this.#provider.notificationLevel
    }

    get lastReadMessageIndex() {
        return this.#provider.lastReadMessageIndex
    }

    get dateCreated() {
        return this.#provider.dateCreated
    }

    get dateUpdated() {
        return this.#provider.dateUpdated
    }

    get createdBy() {
        return this.#provider.createdBy
    }

    get attributes() {
        return this.#provider.attributes
    }

    get participantIdentities() {
        return this.#provider.attributes.participantIdentities
    }

    get uniqueName() {
        return this.#provider.uniqueName
    }

    get participatingClientId() {
        return this.#provider.participatingClientId
    }

    getProvider() {
        return this.#provider
    }
}