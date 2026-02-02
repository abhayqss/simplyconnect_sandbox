import { ComponentCollection, Serializer } from "survey-core";

const apiServiceUrl = process.env.REACT_APP_REMOTE_SERVER_URL;

export function initTriggerWorkflow() {
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

  if (!ComponentCollection.Instance.getCustomQuestionByName("triggerworkflow")) {
    ComponentCollection.Instance.add({
      name: "triggerworkflow",
      title: "Trigger Workflow",
      questionJSON: {
        title: "Trigger Workflow",
        type: "dropdown",
        choicesByUrl: {
          url: `${apiServiceUrl}/directory/workflowTemplate/find?organizationId=${orgId}&communityIds=${comId}&status=PUBLISHED`,
          valueName: "id",
          titleName: "name",
        },
        readOnly: false,
      },
    });
  }

  Serializer.getProperty("question", "visibleIf").displayName = "Trigger If";
}
