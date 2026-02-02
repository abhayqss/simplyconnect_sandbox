import { ComponentCollection, Serializer } from "survey-core";

export function initZip() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("zipcode")) {
    ComponentCollection.Instance.add({
      name: "zipcode",
      title: "Zip Code",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
