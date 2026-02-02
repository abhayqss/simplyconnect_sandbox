class ScrollContext {
    constructor(
        {
            ref,
            user,
            messages,
            participants,
            conversation,
            fetchMessages
        }
    ) {
        this.ref = ref
        this.user = user
        this.messages = messages
        this.participants = participants
        this.conversation = conversation
        this.fetchMessages = fetchMessages
    }

    executeStrategy(strategy) {
        return strategy.execute(this)
    }
}

export default ScrollContext