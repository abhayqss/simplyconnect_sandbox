class TwilioMediaProvider {
    #service = null

    constructor(service) {
        this.#service = service
    }

    get sid() {
        return this.#service.sid
    }

    get type() {
        return this.#service.contentType
    }

    get name() {
        return this.#service.filename
    }

    get size() {
        return this.#service.size
    }

    getUrl() {
        return this.#service.getContentTemporaryUrl()
    }
}

export default TwilioMediaProvider
