import { ComponentCollection } from "survey-core";
const apiServiceUrl = process.env.REACT_APP_REMOTE_SERVER_URL;

export function initMarital() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("marital")) {
    ComponentCollection.Instance.add({
      name: "marital",
      title: "Marital",
      questionJSON: {
        type: "dropdown",
        choicesByUrl: {
          url: `${apiServiceUrl}/directory/marital-status/data`,
          valueName: "id",
          titleName: "label",
        },
        readOnly: false,
      },
    });
  }
}
