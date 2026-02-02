import { useEffect } from "react";
import { useSelector } from "react-redux";
import { useChatManagerContext } from "../context/ChatManagerContext";

const SDKTokenInitializer = () => {
  const user = useSelector((state) => state.auth.login.user.data);
  const { getSdkAccessToken, chatToken, chatUserId } = useChatManagerContext();

  useEffect(() => {
    // 当用户登录且还没有chatToken时，立即获取SDK访问令牌
    if (user && !chatToken) {
      getSdkAccessToken();
    }
  }, [user, chatToken, getSdkAccessToken]);

  useEffect(() => {
    // 调试日志：显示SDK认证状态
    if (chatToken && chatUserId) {
      // SDK authentication successful
    }
  }, [chatToken, chatUserId]);

  return null;
};

export default SDKTokenInitializer;
