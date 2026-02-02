import { ComponentCollection } from "survey-core";

export function initLastName() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("lastname")) {
    ComponentCollection.Instance.add({
      name: "lastname",
      title: "Last Name",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
