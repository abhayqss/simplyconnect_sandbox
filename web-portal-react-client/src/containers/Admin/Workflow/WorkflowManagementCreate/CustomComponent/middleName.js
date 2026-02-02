import { ComponentCollection } from "survey-core";

export function initMiddleName() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("middlename")) {
    ComponentCollection.Instance.add({
      name: "middlename",
      title: "Middle name",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
