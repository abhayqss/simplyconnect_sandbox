import { ComponentCollection } from "survey-core";

export function initFullName() {
  // 首先检查是否已经存在自定义问题类型 'fullName'
  if (!ComponentCollection.Instance.getCustomQuestionByName("fullname")) {
    // 如果不存在，注册自定义问题类型 'fullName'
    ComponentCollection.Instance.add({
      name: "fullname",
      title: "Full Name",
      questionJSON: {
        type: "text",
        readOnly: false,
      },
    });
  }
}
