class User {
    #provider
    
    constructor(provider) {
        this.#provider = provider
    }

    get identity() {
        return this.#provider.identity
    }

    get activeCallConversationSids() {
        return this.#provider.activeCallConversationSids
    }

    get isOnline() {
        return this.#provider.isOnline
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

export default User