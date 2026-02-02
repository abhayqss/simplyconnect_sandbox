import BaseStrategy from "./BaseStrategy";

class ScrollToLastMessageStrategy extends BaseStrategy {
  constructor(message) {
    super();
    this.message = message;
  }

  execute(context) {
    let { ref, user, participants } = context;

    let currentUser = participants.find((o) => o.employeeId === user.id);

    if (!this.message.sid || currentUser.identity === this.message.author) {
      ref.current?.scrollToBottom({ behavior: "smooth" });
    }
  }
}

export default ScrollToLastMessageStrategy;
