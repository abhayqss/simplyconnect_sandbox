import BaseMedia from './Media'

import { EntityOrNull } from './Utils'

const Media = EntityOrNull(BaseMedia)

class Message {
    #provider = null

    constructor(provider) {
        this.#provider = provider
    }

    get conversationSid() {
        return this.#provider.conversationSid
    }

    get index() {
        return this.#provider.index
    }

    get text() {
        return this.#provider.text
    }

    get media() {
        return Media(this.#provider.media)
    }

    get author() {
        return this.#provider.author
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

    get attributes() {
        return this.#provider.attributes
    }

    get isSystemMessage() {
        return this.#provider.isSystemMessage
    }

    get isVoiceMessage() {
        return this.#provider.isVoiceMessage
    }

    get displayLinks() {
        return this.#provider.displayLinks
    }

    getProvider() {
        return this.#provider
    }

    updateAttributes(attributes) {
        return this.#provider.updateAttributes(attributes)
    }
}

export default Message