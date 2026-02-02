import { ComponentCollection, Serializer } from "survey-core";

export function initEmail() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("email")) {
    ComponentCollection.Instance.add({
      name: "email",
      title: "Email",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
