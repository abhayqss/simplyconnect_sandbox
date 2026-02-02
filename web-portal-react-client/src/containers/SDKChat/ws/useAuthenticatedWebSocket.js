import { useEffect, useState } from "react";
import { HeartbeatWebSocket } from "./useWebSocket";

// 单例：全局只用一套状态和连接
let wsInstance = null;
let listeners = [];
let isConnectedSingleton = false;
let messagesSingleton = [];

function subscribe(fn) {
  listeners.push(fn);
  return () => {
    listeners = listeners.filter((l) => l !== fn);
  };
}

function notify() {
  listeners.forEach((fn) => fn({ isConnected: isConnectedSingleton, messages: messagesSingleton }));
}

export function useAuthenticatedWebSocket(token, options = {}) {
  const [state, setState] = useState({
    isConnected: isConnectedSingleton,
    messages: messagesSingleton,
  });

  // 订阅全局状态变化
  useEffect(() => {
    return subscribe(setState);
  }, []);

  useEffect(() => {
    const wsUrl = process.env.REACT_APP_SDK_SOCKET;

    // 断开逻辑：token 变为无效或变更时立即关闭上次连接，并清空单例
    if (wsInstance && (!token || wsInstance._token !== token)) {
      wsInstance.close();
      wsInstance = null;
      isConnectedSingleton = false;
      messagesSingleton = [];
      notify();
    }

    // 创建新 socket，仅在 token 存在且没有实例时执行
    if (!wsInstance && wsUrl && token) {
      wsInstance = new HeartbeatWebSocket(wsUrl, {
        ...options,
        token,
        onAuthRequired: () => console.log("Authentication required"),
      });
      wsInstance._token = token; // 添加实例 token 追踪

      wsInstance.on("open", () => {
        isConnectedSingleton = true;
        notify();
      });
      wsInstance.on("close", () => {
        isConnectedSingleton = false;
        notify();
      });
      wsInstance.on("error", console.error);
      wsInstance.onMessage((message) => {
        messagesSingleton = [...messagesSingleton, message];
        notify();
        if (options.onNewChatMessage) {
          options.onNewChatMessage(message);
        }
      });
    }

    // 只在组件卸载时彻底清理（避免 hooks 死循环产生多次 disconnect/connect）
    return () => {
      // 当组件卸载或 token 已经无效时，才强制断开
      if (wsInstance && !token) {
        wsInstance.close();
        wsInstance = null;
        isConnectedSingleton = false;
        messagesSingleton = [];
        notify();
      }
    };
    // 仅监听 token 变化，options 需要在外部用 useMemo 保证稳定。
  }, [token]);

  return {
    isConnected: state.isConnected,
    messages: state.messages,
    // 也可暴露 wsInstance 相关方法给需要发送消息的地方
  };
}
