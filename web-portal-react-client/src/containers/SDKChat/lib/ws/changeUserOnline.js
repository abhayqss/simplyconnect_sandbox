export function changeUserOnline(data, updateChannelList, isOnline) {
  updateChannelList((channels) => {
    return channels?.map((channel) => ({
      ...channel,
      online: channel.channel_id === data.channel_id ? isOnline : channel.online,
      channel_member: (channel.channel_member || []).map((member) =>
        member.user_id === data.sender ? { ...member, online: isOnline } : member,
      ),
    }));
  });
}
