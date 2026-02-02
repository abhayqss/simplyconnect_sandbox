import { ComponentCollection } from "survey-core";

export function initPhone() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("phone")) {
    ComponentCollection.Instance.add({
      name: "phone",
      title: "Phone",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
