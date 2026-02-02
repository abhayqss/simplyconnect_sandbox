import { ComponentCollection } from "survey-core";

export function initStreet() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("street")) {
    ComponentCollection.Instance.add({
      name: "street",
      title: "Street",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
