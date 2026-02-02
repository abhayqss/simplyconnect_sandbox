import BaseService from "./BaseService";

export class ClientMedicationService extends BaseService {
  find({ clientId, ...other }) {
    return super.request({
      url: `/clients/${clientId}/medications`,
      response: { extractDataOnly: true },
      params: other,
    });
  }

  findById(medicationId, { clientId }) {
    return super.request({
      url: `/clients/${clientId}/medications/${medicationId}`,
      response: { extractDataOnly: true },
    });
  }

  count({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/medications/count`,
    });
  }

  statistics({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/medications/statistics`,
      response: { extractDataOnly: true },
    });
  }

  addMedicationForClient({ clientId, body }) {
    return super.request({
      url: `/clients/${clientId}/medications`,
      method: "POST",
      body: body,
      response: { extractDataOnly: true },
    });
  }

  fetchAllMedicationList(params) {
    return super.request({
      url: `/medications`,
      params,
      response: { extractDataOnly: true },
      responseTimeout: 12000,
    });
  }

  fetchNDCForMedication(mediSpanId) {
    return super.request({
      url: `/medications/${mediSpanId}`,
      response: { extractDataOnly: true },
    });
  }

  deleteMedicationItem({ clientId, medicationId }) {
    return super.request({
      url: `/clients/${clientId}/medications/${medicationId}`,
      method: "DELETE",
      response: { extractDataOnly: true },
    });
  }

  fetchMedicationsDailyData(clientId, params) {
    return super.request({
      url: `/clients/${clientId}/medications/dailyTracker`,
      params,
      response: { extractDataOnly: true },
    });
  }
}

const service = new ClientMedicationService();
export default service;
