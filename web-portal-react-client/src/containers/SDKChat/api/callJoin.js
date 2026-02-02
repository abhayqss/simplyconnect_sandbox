import service from "../lib/server";

/**
 *
 * @param params
 * @returns {Promise<service.AxiosResponse<{video_token:string}>>}
 * @param {string} params.channel_id 频道id
 * @param {number} params.type 1: 音频 2: 视频
 */
const callJoinApi = async (params) => {
  const result = await service.post(`/video/v1/join`, {
    ...params,
  });

  return result.data;
};

export default callJoinApi;
