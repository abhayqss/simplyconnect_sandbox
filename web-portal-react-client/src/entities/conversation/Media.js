class Media {
    #mediaProvider = null

    constructor(mediaProvider) {
        this.#mediaProvider = mediaProvider
    }

    get sid() {
        return this.#mediaProvider.sid
    }

    get type() {
        return this.#mediaProvider.type
    }

    get name() {
        return this.#mediaProvider.name
    }

    get size() {
        return this.#mediaProvider.size
    }

    getUrl() {
        return this.#mediaProvider.getUrl()
    }
}

export default Media
