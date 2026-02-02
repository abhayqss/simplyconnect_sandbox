import { ComponentCollection } from "survey-core";

export function initCellPhone() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("cellphone")) {
    ComponentCollection.Instance.add({
      name: "cellphone",
      title: "CellPhone",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
