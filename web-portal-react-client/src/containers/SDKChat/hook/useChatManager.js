// useChatManager.js
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { format } from "date-fns";
import { produce } from "immer";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import callCancelApi from "../api/callCancel";
import callJoinApi from "../api/callJoin";
import callLeaveApi from "../api/callLeave";
import callRejectApi from "../api/callReject";
import callStartApi from "../api/callStart";
import changeGroupName from "../api/changeGroupName";
import createChannel from "../api/createChannel";
import exitGroupChatMember from "../api/exitGroupChatMember";
import fetchChannelList from "../api/fetchChannelList";
import fetchMessagesList from "../api/fetchMessagesList";
import fetchUserInfo from "../api/fetchUserInfo";
import joinGroupChatMember from "../api/joinGroupChatMember";
import removeGroupChatMember from "../api/removeGroupChatMember";
import setLastView from "../api/setLastView";
import { highlightText } from "../lib/highlightMatch";
import { setGlobalChatToken } from "../lib/server";

import { useAuthUser } from "../../../hooks/common";
import getSdkAccessTokenApi from "../api/getSdkAccessToken";

export function useChatManager() {
  const queryClient = useQueryClient();
  const authUser = useAuthUser();

  // 搜索
  const [searchText, setSearchText] = useState("");
  // 本地维护的所有会话详细数据（消息列表等）
  const [allChannelData, setAllChannelData] = useState({});

  // 分页
  const [paginationState, setPaginationState] = useState({
    hasMore: true,
    isLoadingMore: false,
    lastMessageId: null,
    currentPage: 1,
  });

  // aws3  key secret
  const [awsData, setAwsData] = useState({
    os_key: "",
    os_secret: "",
  });

  // SDK access token data
  const [chatToken, setChatToken] = useState(null);
  const [chatUserId, setChatUserId] = useState(null);
  const [isLoadingSdkToken, setIsLoadingSdkToken] = useState(false);
  const hasRequestedToken = useRef(false);

  // Reset token request state when user logs out
  useEffect(() => {
    if (!authUser) {
      // 立即取消所有相关查询
      queryClient.cancelQueries({ queryKey: ["channelList"] });
      queryClient.removeQueries({ queryKey: ["channelList"] });

      // 清理所有聊天相关状态
      setChatToken(null);
      setChatUserId(null);
      setIsLoadingSdkToken(false);
      hasRequestedToken.current = false;
      setGlobalChatToken(null);

      // 清理其他状态
      setAllChannelData({});
      setCurrentChannelId(null);
      setSearchText("");
      setPaginationState({
        hasMore: true,
        isLoadingMore: false,
        lastMessageId: null,
        currentPage: 1,
      });
    }
  }, [authUser, queryClient]);

  // video token
  const [callToken, setCallToken] = useState(null);
  const [callType, setCallType] = useState(null);
  const [callChannelId, setCallChannelId] = useState(null);
  const [callInvite, setCallInvite] = useState(null);
  // 是否为拨打方
  const [isCaller, setIsCaller] = useState(false);

  const clearCallStatus = () => {
    setCallInvite(null);
    setCallToken(null);
    setCallChannelId(null);
    setCallType(null);
    setIsCaller(false);
  };

  // 1. 基础会话列表
  const { data: channelList = [] } = useQuery({
    queryKey: ["channelList"],
    queryFn: () => fetchChannelList(chatUserId),
    staleTime: 5 * 60 * 1000,
    enabled: !!(authUser && chatToken && chatUserId), // 需要用户登录且token和userId都存在
  });

  // 2. 监听 channelList，合并基础字段到 allChannelData
  useEffect(() => {
    if (!channelList.length) {
      return;
    }
    setAllChannelData((prev) => {
      const next = { ...prev };
      channelList.forEach((ch) => {
        next[ch.channel_id] = {
          ...next[ch.channel_id], // 保留详细字段
          ...ch, // 用新基础字段覆盖
        };
      });
      // 清理已删除会话
      Object.keys(next).forEach((id) => {
        if (!channelList.find((c) => c.channel_id === id)) {
          delete next[id];
        }
      });

      return next;
    });
  }, [channelList]);

  // 3. 当前选中会话ID（用useState管理）
  const [currentChannelId, setCurrentChannelId] = useState(null);

  // 5. 当前会话详细数据
  const currentChannel = currentChannelId ? allChannelData[currentChannelId] : null;

  // 6. 左侧会话列表（带高亮）
  const channels = useMemo(() => {
    const searchTerm = searchText.toLowerCase();
    return channelList
      .filter((channel) => {
        const name = channel.channel_display_name || channel.display_name || "";
        return name.toLowerCase().includes(searchTerm);
      })
      .map((channel) => {
        const name = channel.channel_display_name || channel.display_name || "";
        return {
          ...channel,
          highlightedName: searchText ? highlightText(name, searchTerm) : name,
        };
      });
  }, [channelList, searchText]);

  // 7. 获取消息列表（只维护 allChannelData）
  const fetchMessagesListMutation = useMutation({
    mutationFn: fetchMessagesList,
    onSuccess: (data, { channel_id, params }) => {
      setAllChannelData((prev) => {
        const old = prev[channel_id] || {};
        return {
          ...prev,
          [channel_id]: {
            ...old,
            message_list: params?.isLoadMore ? [...data.message_list, ...(old.message_list || [])] : data.message_list,
            message_list_total: data.total,
          },
        };
      });

      // 如果不是加载更多，需要更新分页状态
      if (!params?.isLoadMore) {
        const pageSize = 15;
        // 简化判断：只要返回的total大于页面大小，就认为有更多数据
        const hasMoreData = data.total > pageSize;

        setPaginationState({
          hasMore: hasMoreData,
          isLoadingMore: false,
          lastMessageId: null,
          currentPage: 1,
        });
      }
    },
  });

  // 8. 加载更多消息
  function getFirstMessageId(message_list) {
    if (!Array.isArray(message_list)) return undefined;
    const msg = message_list.find((m) => m && m.message_id && m.type !== "date-separator");
    return msg?.message_id;
  }

  const loadMoreMessages = useCallback(async () => {
    if (!currentChannelId || paginationState.isLoadingMore || !paginationState.hasMore) return;

    setPaginationState((prev) => ({ ...prev, isLoadingMore: true }));

    try {
      const channelData = allChannelData[currentChannelId];
      const lastMessageId = getFirstMessageId(channelData?.message_list);

      if (!lastMessageId) {
        setPaginationState((prev) => ({ ...prev, isLoadingMore: false, hasMore: false }));
        return;
      }

      const result = await fetchMessagesListMutation.mutateAsync({
        channel_id: currentChannelId,
        params: {
          page: paginationState.currentPage + 1,
          last_message_id: lastMessageId,
          isLoadMore: true,
        },
      });

      setPaginationState((prev) => {
        // 获取每页期望的数量
        const pageSize = 15;
        // 获取服务器返回的总数
        const serverTotal = result.total || 0;

        // 简化判断：只要返回的total大于页面大小，就认为有更多数据
        const hasMoreData = serverTotal > pageSize;

        return {
          ...prev,
          isLoadingMore: false,
          currentPage: result.current_page || prev.currentPage + 1,
          hasMore: hasMoreData,
        };
      });
    } catch (error) {
      setPaginationState((prev) => ({ ...prev, isLoadingMore: false }));
    }
  }, [currentChannelId, paginationState, allChannelData, fetchMessagesListMutation]);

  const updateChannelList = useCallback(
    (updater) => {
      queryClient.setQueryData(["channelList"], (old) => (typeof updater === "function" ? updater(old) : updater));
    },
    [queryClient],
  );

  // 9. 设置最后查看时间
  const setLastViewMutation = useMutation({
    mutationFn: setLastView,
    onSuccess: (_, channelId) => {
      // 只做最后查看时间字段更新（未读数归零由 handleUnreadMessage 保证）
      queryClient.setQueryData(["channelList"], (prev) =>
        produce(prev, (draft) => {
          const channel = draft?.find((c) => c.channel_id === channelId);
          if (channel) channel.last_view_time = Date.now();
        }),
      );
    },
  });

  // 10. 统一未读数处理
  const handleUnreadMessage = useCallback(
    (messageData) => {
      const channelId = messageData.channel_id;
      if (!channelId) return;
      // 当前窗口收到消息，只归零未读（不再调用 lastView）
      if (channelId === currentChannelId) {
        // 只归零未读（本地cache），不调lastView
        updateChannelList((channels) => {
          const result = channels?.map((channel) =>
            channel.channel_id === channelId ? { ...channel, un_read_count: 0 } : channel,
          );
          return result;
        });
        // 注意：不再调用 setLastViewMutation.mutate(channelId);
      } else {
        // 其他频道未读+1
        updateChannelList((channels) => {
          const result = channels?.map((channel) =>
            channel.channel_id === channelId
              ? { ...channel, un_read_count: (channel.un_read_count || 0) + 1 }
              : channel,
          );
          return result;
        });
      }
    },
    [currentChannelId, updateChannelList],
  );

  // 11. 切换频道
  const switchChannel = useCallback(
    async (channelId, force = false) => {
      if (channelId && (force || channelId !== currentChannelId)) {
        // 1. 归零未读数
        updateChannelList((channels) =>
          channels?.map((channel) => (channel.channel_id === channelId ? { ...channel, un_read_count: 0 } : channel)),
        );

        // 2. 立即切换频道，触发 UI 渲染本地缓存数据
        setCurrentChannelId(channelId);

        // 3. 更新最后查看时间（可并发，不必等）
        setLastViewMutation.mutate(channelId);

        // 4. 拉最新消息（异步，不阻塞渲染）
        fetchMessagesListMutation.mutate({
          channel_id: channelId,
          params: {},
        });
      }
    },
    [
      currentChannelId,
      updateChannelList,
      setLastViewMutation,
      fetchMessagesListMutation,
      setPaginationState,
      setCurrentChannelId,
    ],
  );

  const hasInitRef = useRef(false);
  useEffect(() => {
    if (!hasInitRef.current && !currentChannelId && channelList.length > 0) {
      switchChannel(channelList[0].channel_id);
      hasInitRef.current = true;
    }
    // 只依赖 currentChannelId, switchChannel，避免 channelList 变化每次重置
  }, [currentChannelId, switchChannel]);

  // 12. 刷新 channelList
  const refreshChannelList = useCallback(async () => {
    await queryClient.invalidateQueries(["channelList"]);
  }, [queryClient]);

  // 14. 提供消息发送后本地添加方法（带去重）
  const addMessageToChannel = useCallback((channelId, newMsg) => {
    setAllChannelData((prev) => {
      const old = prev[channelId] || {};
      const currentList = old.message_list || [];
      // 去重：同一 message_id 只允许插入一次
      const exists = currentList.some((msg) => msg.message_id === newMsg.message_id);
      if (exists) return prev;
      const updated_at = format(new Date(newMsg.created_at), "hh:mm a");
      return {
        ...prev,
        [channelId]: {
          ...old,
          message_list: [...currentList, newMsg],
          last_message: newMsg,
          message_list_total: (old.message_list_total || 0) + 1,
          updated_at,
        },
      };
    });
  }, []);

  // 群聊移除成员
  const removeGroupMemberMutation = useMutation({
    mutationFn: async ({ channelId, userIds }) => await removeGroupChatMember(channelId, userIds),
    onSuccess: async () => {
      await refreshChannelList();
    },
  });
  // 对外暴露的移除成员方法
  const removeGroupMember = useCallback(
    (channelId, userIds) => {
      removeGroupMemberMutation.mutate({ channelId, userIds });
    },
    [removeGroupMemberMutation],
  );

  // 退出群聊成员
  const exitGroupChatMutation = useMutation({
    mutationFn: async (channelId) => await exitGroupChatMember(channelId),
    onSuccess: async (_, channelId) => {
      await refreshChannelList();
      switchToNextValidChannel();
    },
  });

  // 暴露退出群聊成员的方法

  const exitGroupChat = useCallback(
    (channelId) => {
      exitGroupChatMutation.mutate(channelId);
    },
    [exitGroupChatMutation],
  );

  // 新建频道 mutation
  const createChannelMutation = useMutation({
    mutationFn: async (params) => await createChannel(params),
    onSuccess: async (data) => {
      await refreshChannelList(); // 刷新频道列表
      await switchChannel(data.channel_id); // 自动切换到新频道
    },
  });
  // 新建频道并切换方法
  const createChannelAndSwitch = useCallback(
    (params) => {
      createChannelMutation.mutate(params);
    },
    [createChannelMutation],
  );

  // 加入群聊
  const joinGroupChatMutation = useMutation({
    mutationFn: async ({ channelId, members }) => await joinGroupChatMember(channelId, members),
    onSuccess: async () => {
      await refreshChannelList();
    },
  });
  const joinGroupChat = useCallback(
    (channelId, members) => {
      joinGroupChatMutation.mutate({ channelId, members });
    },
    [joinGroupChatMutation],
  );

  // 切换到已经存在的会话
  const switchToNextValidChannel = useCallback(() => {
    // 过滤掉当前频道，选择第一个剩下的频道
    const filtered = channels.filter((c) => c.channel_id !== currentChannelId);
    if (filtered.length > 0) {
      switchChannel(filtered[0].channel_id);
    } else {
      setCurrentChannelId(null); // 没有可用频道，设为 null
    }
  }, [channels, currentChannelId, switchChannel]);

  /**
   * 获取个人信息
   */

  const fetchUserInfoMutation = useMutation({
    mutationFn: async () => await fetchUserInfo(),
    onSuccess: async (data) => {
      const awsData = {
        os_key: data.data.os_key,
        os_secret: data.data.os_secret,
      };

      setAwsData(awsData);
    },
  });

  const getUserInfo = useCallback(() => {
    fetchUserInfoMutation.mutate();
  }, [fetchUserInfoMutation]);

  // init sdk token
  const fetchSdkAccessTokenMutation = useMutation({
    mutationFn: async () => {
      setIsLoadingSdkToken(true);
      const res = await getSdkAccessTokenApi();
      return res?.data || null;
    },
    onSuccess: (data) => {
      setIsLoadingSdkToken(false);
      if (data && data.chatToken && data.chatUserId) {
        setChatToken(data.chatToken);
        setChatUserId(data.chatUserId);
        // 设置全局token供API调用使用
        setGlobalChatToken(data.chatToken);
      }
    },
    onError: (error) => {
      setIsLoadingSdkToken(false);
      // hasRequestedToken.current = false; // 重置状态以允许重试
    },
    retry: 3,
  });

  const getSdkAccessToken = useCallback(() => {
    // 防止重复调用，并确保用户已登录
    if (!authUser || hasRequestedToken.current || chatToken || isLoadingSdkToken) {
      return;
    }

    hasRequestedToken.current = true;
    fetchSdkAccessTokenMutation.mutate();
  }, [authUser, chatToken, isLoadingSdkToken]);

  // 视频相关
  const callStartMutation = useMutation({
    mutationFn: async (params) => {
      setCallType(params.type);
      setCallChannelId(params.channel_id);
      return await callStartApi(params);
    },
    onSuccess: async (data) => {
      setCallToken(data.video_token);
    },
  });

  const callStart = useCallback(
    (params) => {
      setIsCaller(true); // 点击拨号时设置为拨打方
      callStartMutation.mutate(params);
    },
    [callStartMutation],
  );

  // 视频取消
  const callCancelMutation = useMutation({
    mutationFn: async (params) => await callCancelApi(params),
  });

  const callCancel = useCallback(
    (params) => {
      callCancelMutation.mutate(params);
    },
    [callCancelMutation],
  );

  // 视频拒绝加入
  const callRejectMutation = useMutation({
    mutationFn: async (params) => await callRejectApi(params),
  });

  const callReject = useCallback(
    (params) => {
      callRejectMutation.mutate(params);
    },
    [callRejectMutation],
  );

  // 视频加入
  const callJoinMutation = useMutation({
    mutationFn: async (params) => {
      setCallType(params.type);
      setCallChannelId(params.channel_id);
      return await callJoinApi(params);
    },
    onSuccess: async (data) => {
      setCallToken(data.video_token);
    },
  });

  const callJoin = useCallback(
    (params) => {
      callJoinMutation.mutate(params);
    },
    [callJoinMutation],
  );

  // 视频挂断 leave
  const callLeaveMutation = useMutation({
    mutationFn: async (params) => await callLeaveApi(params),
  });
  const callLeave = useCallback(
    (params) => {
      callLeaveMutation.mutate(params);
    },
    [callLeaveMutation],
  );

  // 页面聚焦时自动 lastView 及清零未读
  useEffect(() => {
    const handleVisibility = () => {
      if (document.visibilityState === "visible" && currentChannelId) {
        // 只在有未读时才触发 lastView
        const channel = channelList.find((ch) => ch.channel_id === currentChannelId);
        if (channel && channel.un_read_count > 0) {
          setLastViewMutation.mutate(currentChannelId);
          updateChannelList((channels) =>
            channels?.map((c) => (c.channel_id === currentChannelId ? { ...c, un_read_count: 0 } : c)),
          );
        }
      }
    };
    document.addEventListener("visibilitychange", handleVisibility);
    return () => document.removeEventListener("visibilitychange", handleVisibility);
  }, [currentChannelId, channelList, setLastViewMutation, updateChannelList]);

  /**
   * 更改群聊名称
   * @param {string} channelId
   * @param {string} newName
   * @returns {Promise<{success:boolean, error?:string}>}
   */
  const updateGroupName = async (channelId, newName) => {
    if (!channelId || !newName) {
      return { success: false, error: "Parameter missing" };
    }
    try {
      await changeGroupName(channelId, newName);
      setAllChannelData((prev) => {
        const updated = { ...prev };
        if (updated[channelId]) {
          updated[channelId] = {
            ...updated[channelId],
            channel_display_name: newName,
          };
        }
        return updated;
      });
      updateChannelList((channels) =>
        channels?.map((c) => (c.channel_id === channelId ? { ...c, channel_display_name: newName } : c)),
      );
      return { success: true };
    } catch (e) {
      return { success: false, error: e?.message || "Group name update failed" };
    }
  };

  return {
    channels, // 左侧列表（带高亮）
    currentChannelId, // 选中频道ID
    currentChannel, // 右侧详细数据
    isLoading: fetchMessagesListMutation.isLoading,
    error: fetchMessagesListMutation.error,
    searchText,
    setSearchText,
    switchChannel, // 更改会话
    loadMoreMessages,
    paginationState,
    refreshChannelList, // 刷新 channelList 方法
    allChannelData, // 聚合的数据
    setAllChannelData, // 提供给 websocket/useSyncSocketToQuery
    addMessageToChannel, // 提供本地插入消息功能
    updateChannelList, // 更新 channelList
    handleUnreadMessage, // 处理未读
    removeGroupMember, // 移除群聊成员
    exitGroupChat, // 退出群聊
    createChannelAndSwitch, // 创建群聊并自动切换
    joinGroupChat, // 加入群聊
    switchToNextValidChannel, // 切换到已经存在的会话
    getUserInfo,
    awsData, //aws 密钥和 key
    callStart, // 拨通视频
    callType, // 当前通话类型
    setCallType,
    callChannelId, // 当前通话id
    setCallChannelId,
    callToken, // 视频 token
    getSdkAccessToken, // getSdkAccessToken
    callCancel, // 视频取消
    callReject, // 视频拒绝
    callJoin, // 视频加入
    callInvite, // 呼叫者信息
    setCallInvite,
    clearCallStatus,
    callLeave, // 新增的视频离开
    isCaller, // 是否为拨打方
    setIsCaller, // 如需外部强制设置
    updateGroupName, // <--- 新增/暴露
    chatToken, // SDK聊天token
    chatUserId, // SDK聊天用户ID
    isLoadingSdkToken, // SDK token加载状态
  };
}
