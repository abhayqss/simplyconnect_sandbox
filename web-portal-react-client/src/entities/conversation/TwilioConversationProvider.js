export default class TwilioConversationProvider {
    #service

    constructor(service) {
        this.#service = service
    }

    get sid() {
        return this.#service.sid
    }

    get status() {
        return this.#service.status
    }

    get friendlyName() {
        return this.#service.attributes.friendlyName
    }

    get state() {
        return this.#service.state
    }

    get lastMessage() {
        return this.#service.lastMessage
    }

    get notificationLevel() {
        return this.#service.notificationLevel
    }

    get lastReadMessageIndex() {
        return this.#service.lastReadMessageIndex
    }

    get dateCreated() {
        return this.#service.dateCreated
    }

    get dateUpdated() {
        return this.#service.dateUpdated
    }

    get createdBy() {
        return this.#service.createdBy
    }

    get attributes() {
        return this.#service.attributes
    }

    get participantIdentities() {
        return this.#service.attributes.participantIdentities
    }

    get uniqueName() {
        return this.#service.uniqueName
    }

    get participatingClientId() {
        return this.#service.attributes.participatingClientId
    }

    getParticipants() {
        return this.#service.getParticipants()
    }

    getMessages(pageSize, from, direction) {
        return this.#service.getMessages(pageSize, from, direction)
    }

    getMessagesCount() {
        return this.#service.getMessagesCount()
    }

    setAllMessagesRead() {
        this.#service.setAllMessagesRead()
    }

    sendMessage(message, messageAttrs) {
        return this.#service.sendMessage(message, messageAttrs)
    }

    updateMessage(oldMessage, newMessage) {
        return oldMessage.getProvider().update(newMessage)
    }

    updateLastReadMessageIndex(index) {
        return this.#service.updateLastReadMessageIndex(index)
    }

    getParticipantByIdentity(identity) {
        return this.#service.getParticipantByIdentity(identity)
    }
}