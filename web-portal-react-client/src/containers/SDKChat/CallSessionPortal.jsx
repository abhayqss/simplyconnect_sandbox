import { useChatManagerContext } from "./context/ChatManagerContext";
import LiveKit from "./chatRight/liveKit";

export default function CallSessionPortal() {
  const { callToken } = useChatManagerContext();
  if (!callToken) return null;
  return <LiveKit />;
}
