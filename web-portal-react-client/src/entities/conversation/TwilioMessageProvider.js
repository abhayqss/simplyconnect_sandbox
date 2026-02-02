import TwilioMediaProvider from './TwilioMediaProvider'

import { EntityOrNull } from './Utils'

const Media = EntityOrNull(TwilioMediaProvider)

class TwilioMessageProvider {
    #service = null

    constructor(service) {
        this.#service = service
    } 

    get index() {
        return this.#service.index
    }

    get text() {
        return this.#service.body
    }

    get media() {
        return Media(this.#service.media)
    }

    get author() {
        return this.#service.author
    }

    get conversationSid() {
        return this.#service.conversation.sid
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

    get attributes() {
        return this.#service.attributes
    }

    get isSystemMessage() {
        return this.#service.author === 'system'
    }

    get isVoiceMessage() {
        return this.attributes?.isVoiceMessage
    }

    get displayLinks() {
        return this.attributes?.displayLinks
    }

    update(value) {
        return this.#service.updateBody(value)
    }

    updateAttributes(attributes) {
        return this.#service.updateAttributes(attributes)
    }

    remove() {
        return this.#service.remove()
    }

    getConversation() {
        return this.#service.conversation
    }
}

export default TwilioMessageProvider