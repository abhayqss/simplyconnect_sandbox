import { ComponentCollection } from "survey-core";
const apiServiceUrl = process.env.REACT_APP_REMOTE_SERVER_URL;

export function initRace() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("race")) {
    ComponentCollection.Instance.add({
      name: "race",
      title: "Race",
      questionJSON: {
        type: "dropdown",
        choicesByUrl: {
          url: `${apiServiceUrl}/directory/races/data`,
          valueName: "id",
          titleName: "title",
        },
        readOnly: false,
      },
    });
  }
}
