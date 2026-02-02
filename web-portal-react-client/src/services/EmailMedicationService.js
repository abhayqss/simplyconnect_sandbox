import BaseService from "./BaseService";

export class EmailMedicationService extends BaseService {
  findEmailMedicationList(clientId, messageBatchId) {
    return super.request({
      url: `/clients/${clientId}/medications/${messageBatchId}/medDeliveries`,
      method: "GET",
    });
  }

  /**
   * 更改特定客户的药物状态。
   *
   * @param {string} clientId - 客户的唯一标识符。
   * @param {string} messageBatchId - 消息批次的唯一标识符。
   * @param {Object} body - 请求体内容。
   * @param {("YES"|"NO"|"SOME")} body.intake - 表示药物摄取状态，可能的值为 "YES", "NO", "SOME"。
   * @param {string[]} body.medDeliveryIds - 药物交付的唯一标识符数组。
   * @returns {Promise} 返回一个请求的 Promise。
   */
  changeMedicationStatus(clientId, messageBatchId, body) {
    return super.request({
      method: "POST",
      url: `/clients/${clientId}/medications/${messageBatchId}/batchMedicationIntake`,
      body: body,
    });
  }
}

const service = new EmailMedicationService();
export default service;
