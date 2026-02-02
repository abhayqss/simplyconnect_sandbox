import { ComponentCollection } from "survey-core";
const apiServiceUrl = process.env.REACT_APP_REMOTE_SERVER_URL;

export function initGender() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("gender")) {
    ComponentCollection.Instance.add({
      name: "gender",
      title: "Gender",
      questionJSON: {
        type: "dropdown",
        choicesByUrl: {
          url: `${apiServiceUrl}/directory/genders/data`,
          valueName: "id",
          titleName: "label",
        },
        readOnly: false,
      },
    });
  }
}
