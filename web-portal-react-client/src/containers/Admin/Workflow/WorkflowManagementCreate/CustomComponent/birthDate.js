import { ComponentCollection } from "survey-core";

export function initBirthDate() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("birthdate")) {
    ComponentCollection.Instance.add({
      name: "birthdate",
      title: "Birth Date",
      questionJSON: {
        type: "text",
        inputType: "date",
        readOnly: false,
      },
    });
  }
}
