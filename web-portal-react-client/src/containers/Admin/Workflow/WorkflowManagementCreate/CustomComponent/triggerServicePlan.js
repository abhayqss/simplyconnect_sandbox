import { ComponentCollection, Serializer } from "survey-core";
const apiServiceUrl = process.env.REACT_APP_REMOTE_SERVER_URL;

export function initTriggerServicePlan() {
  const orgIdString = localStorage.getItem("triggerCurrentOrgId");
  const comIdString = localStorage.getItem("triggerCurrentComId");

  let orgId, comId;

  try {
    orgId = orgIdString ? JSON.parse(orgIdString) : null;
    comId = comIdString ? JSON.parse(comIdString) : null;
  } catch (error) {
    console.error("Error parsing localStorage data:", error);
    return;
  }

  if (!orgId || !comId) {
    return;
  }

  if (!ComponentCollection.Instance.getCustomQuestionByName("triggerserviceplan")) {
    ComponentCollection.Instance.add({
      name: "triggerserviceplan",
      title: "Trigger Service Plan",
      questionJSON: {
        title: "Trigger Service Plan",
        type: "dropdown",
        choicesByUrl: {
          url: `${apiServiceUrl}/directory/servicePlanTemplate/find?organizationId=${orgId}&communityIds=${comId}&status=PUBLISHED`,
          valueName: "id",
          titleName: "name",
        },
        readOnly: false,
      },
    });
  }

  Serializer.getProperty("question", "visibleIf").displayName = "Trigger If";
}
