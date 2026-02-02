import { ComponentCollection } from "survey-core";

export function initFirstName() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("firstname")) {
    ComponentCollection.Instance.add({
      name: "firstname",
      title: "First Name",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
