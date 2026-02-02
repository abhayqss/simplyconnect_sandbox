class TwilioUserProvider {
    #service = null

    constructor(service) {
        this.#service = service
    }

    get identity() {
        return this.#service.identity
    }

    get activeCallConversationSids() {
        return this.#service.attributes.activeCallConversationSids
    }

    get isOnline() {
        return this.#service.isOnline
    }

    on(eventName, cb) {
        return this.#service.on(eventName, cb)
    }

    off(eventName, cb) {
        return this.#service.off(eventName, cb)
    }

    getProvider() {
        return this.#service
    }
}

export default TwilioUserProvider
