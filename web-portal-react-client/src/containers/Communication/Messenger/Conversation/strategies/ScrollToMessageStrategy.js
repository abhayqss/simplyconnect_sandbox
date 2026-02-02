import { defer } from 'lib/utils/Utils'

import BaseStrategy from './BaseStrategy'

class ScrollToMessageStrategy extends BaseStrategy {
    constructor(messageSid) {
        super()
        this.messageSid = messageSid
    }

    execute(context) {
        let {
            ref,
            messages,
            fetchMessages
        } = context

        if (!this.messageSid) return

        const index = messages.findIndex(
            o => o.sid === this.messageSid
        )

        if (index >= 0) {
            const nodes = ref.current.getItemNodes()
            const node = nodes[index]
            node && node.scrollIntoView({ behavior: 'smooth' })
        } else if (messages.first().index > 0) {
            fetchMessages({ from: messages.first().index - 1 }).then(data => {
                defer(500).then(() => {
                    this.execute({ ...context, messages: messages.unshift(...data) })
                })
            })
        }
    }
}

export default ScrollToMessageStrategy
