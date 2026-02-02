const { Record } = require("immutable");

export default Record({
  canEdit: false,
  eventTypeId: null,
  channels: [],
  responsibilityName: null,
  ratio: 70,
  threshold: true,
});
