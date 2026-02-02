import service from "../lib/server";

/**
 *
 * @param params
 * @returns {Promise<service.AxiosResponse<{code:number,msg:string,data:{}}>>}
 * @param {string} params.channel_id 频道id
 * @param {number} params.type 1: 音频 2: 视频
 */
const callRejectApi = async (params) => {
  return await service.post(`/video/v1/reject`, {
    ...params,
  });
};

export default callRejectApi;
