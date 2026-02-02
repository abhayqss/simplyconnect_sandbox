export class HeartbeatWebSocket {
  constructor(url, options = {}) {
    this.options = {
      pingInterval: 30000,
      pingTimeout: 10000,
      reconnectInterval: 5000,
      maxReconnectAttempts: 5,
      ...options,
    };
    this.url = url;
    this.socket = null;
    this.reconnectAttempts = 0;
    this.pingTimer = null;
    this.pongTimeoutTimer = null;
    this.isConnected = false;
    this.isExplicitClose = false;
    this.messageListeners = [];
    this.eventListeners = { open: [], close: [], error: [] };

    this.connect();
  }

  connect() {
    this.isExplicitClose = false;
    const urlWithToken = this.options.token ? `${this.url}?token=${encodeURIComponent(this.options.token)}` : this.url;
    this.socket = new WebSocket(urlWithToken);

    this.socket.onopen = (event) => {
      this.isConnected = true;
      this.reconnectAttempts = 0;
      this.startHeartbeat();
      this.emit("open", event);
    };

    this.socket.onclose = (event) => {
      this.isConnected = false;
      this.stopHeartbeat();
      this.emit("close", event);

      // 只有在非主动关闭、未超重连次数时才自动重连
      if (!this.isExplicitClose && this.reconnectAttempts < this.options.maxReconnectAttempts) {
        this.reconnectAttempts++;
        setTimeout(() => this.connect(), this.options.reconnectInterval);
      }
    };

    this.socket.onerror = (event) => {
      this.isConnected = false;
      this.emit("error", event);
    };

    this.socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);

        if (data.status === "OK" && data.data && data.data.Data && data.data.Data.type === "pong") {
          this.handlePong();
          return;
        }

        this.messageListeners.forEach((listener) => listener(data));
      } catch (error) {
        console.error("Error parsing message:", error);
      }
    };
  }

  startHeartbeat() {
    this.stopHeartbeat();
    this.sendPing();
    this.pingTimer = setInterval(() => {
      this.sendPing();
    }, this.options.pingInterval);
  }

  stopHeartbeat() {
    if (this.pingTimer) {
      clearInterval(this.pingTimer);
      this.pingTimer = null;
    }
    if (this.pongTimeoutTimer) {
      clearTimeout(this.pongTimeoutTimer);
      this.pongTimeoutTimer = null;
    }
  }

  sendPing() {
    if (this.isConnected) {
      this.send({
        action: "ping",
        type: "ping",
      });
      this.pongTimeoutTimer = setTimeout(() => {
        console.warn("Pong timeout, closing connection");
        this.socket.close();
      }, this.options.pingTimeout);
    }
  }

  handlePong() {
    if (this.pongTimeoutTimer) {
      clearTimeout(this.pongTimeoutTimer);
      this.pongTimeoutTimer = null;
    }
  }

  send(data) {
    if (this.isConnected && this.socket) {
      const message = typeof data === "string" ? data : JSON.stringify(data);
      this.socket.send(message);
    } else {
      console.warn("WebSocket not connected, message not sent:", data);
    }
  }

  onMessage(listener) {
    this.messageListeners.push(listener);
    return () => {
      this.messageListeners = this.messageListeners.filter((l) => l !== listener);
    };
  }

  on(event, listener) {
    if (this.eventListeners[event]) {
      this.eventListeners[event].push(listener);
    }
    return () => {
      if (this.eventListeners[event]) {
        this.eventListeners[event] = this.eventListeners[event].filter((l) => l !== listener);
      }
    };
  }

  emit(event, ...args) {
    if (this.eventListeners[event]) {
      this.eventListeners[event].forEach((listener) => listener(...args));
    }
  }

  close() {
    this.isExplicitClose = true;
    this.stopHeartbeat();
    this.reconnectAttempts = this.options.maxReconnectAttempts;
    if (this.socket) {
      this.socket.close();
    }
  }
}
