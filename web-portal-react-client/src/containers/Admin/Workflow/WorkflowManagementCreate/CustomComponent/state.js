import { ComponentCollection } from "survey-core";
const apiServiceUrl = process.env.REACT_APP_REMOTE_SERVER_URL;
export function initState() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("state")) {
    ComponentCollection.Instance.add({
      name: "state",
      title: "State",
      questionJSON: {
        type: "dropdown",
        choicesByUrl: {
          url: `${apiServiceUrl}/directory/states/data`,
          valueName: "id",
          titleName: "label",
        },
        readOnly: false,
      },
    });
  }
}
