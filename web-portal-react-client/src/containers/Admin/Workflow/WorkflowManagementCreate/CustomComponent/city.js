import { ComponentCollection } from "survey-core";

export function initCity() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("city")) {
    ComponentCollection.Instance.add({
      name: "city",
      title: "City",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
