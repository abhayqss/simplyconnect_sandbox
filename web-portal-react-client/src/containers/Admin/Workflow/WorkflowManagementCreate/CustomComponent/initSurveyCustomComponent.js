import { initServicePlan } from "./servicePlan";
import { initFullName } from "./fullName";
import { initFirstName } from "./firstName";
import { initMiddleName } from "./middleName";
import { initLastName } from "./lastName";
import { initBirthDate } from "./birthDate";
import { initGender } from "./gender";
import { initRace } from "./race";
import { initMarital } from "./marital";
import { initCity } from "./city";
import { initStreet } from "./street";
import { initState } from "./state";
import { initZip } from "./zip";
import { initPhone } from "./phone";
import { initCellPhone } from "./cellPhone";
import { initEmail } from "./email";
import { initTriggerWorkflow } from "./triggerWorkflow";
import { initTriggerServicePlan } from "./triggerServicePlan";
import { CustomAttributes } from "./customAttributes";

import { ComponentCollection, Serializer } from "survey-core";

export default function initSurveyCustomComponent() {
  initServicePlan();
  initFullName();
  initFirstName();
  initMiddleName();
  initLastName();
  initBirthDate();
  initGender();
  initRace();
  initMarital();
  initCity();
  initStreet();
  initState();
  initZip();
  initPhone();
  initCellPhone();
  initEmail();
  initTriggerWorkflow();
  initTriggerServicePlan();
  CustomAttributes();
}

export const uninstallSurveyCustomComponent = () => {
  ComponentCollection.Instance.remove("serviceplan");
  ComponentCollection.Instance.remove("fullname");
  ComponentCollection.Instance.remove("firstname");
  ComponentCollection.Instance.remove("middlename");
  ComponentCollection.Instance.remove("lastname");
  ComponentCollection.Instance.remove("birthdate");
  ComponentCollection.Instance.remove("gender");
  ComponentCollection.Instance.remove("race");
  ComponentCollection.Instance.remove("marital");
  ComponentCollection.Instance.remove("city");
  ComponentCollection.Instance.remove("street");
  ComponentCollection.Instance.remove("state");
  ComponentCollection.Instance.remove("phone");
  ComponentCollection.Instance.remove("cellPhone");
  ComponentCollection.Instance.remove("email");
  ComponentCollection.Instance.remove("zipcode");
  ComponentCollection.Instance.remove("triggerworkflow");
  ComponentCollection.Instance.remove("triggerserviceplan");
};

/**
 * 仅仅只能够添加一个的组件
 * @type {string[]}
 */
export const onlyList = [
  "serviceplan",
  "fullname",
  "firstname",
  "middlename",
  "lastname",
  "birthdate",
  "gender",
  "race",
  "marital",
  "city",
  "street",
  "state",
  "zipcode",
  "phone",
  "cellphone",
  "email",
];

export const customComponents = ["triggerworkflow", "triggerserviceplan"];

export const customComponentsNotRequire = ["triggerworkflow", "triggerserviceplan"];

/**
 * JSON Editor 对应的name 和 title
 * 配合onlyList 生成唯一的组件
 */
export const fieldDisplayNames = {
  fullname: {
    title: "Full Name",
    name: "fullName",
  },
  serviceplan: {
    title: "Service Plan",
    name: "servicePlan",
  },
  firstname: {
    title: "First Name",
    name: "firstName",
  },
  middlename: {
    title: "Middle Name",
    name: "middleName",
  },
  lastname: {
    title: "Last Name",
    name: "lastName",
  },
  birthdate: {
    title: "Birth Date",
    name: "birthDate",
  },
  gender: {
    title: "Gender",
    name: "genderId",
  },
  race: {
    title: "Race",
    name: "raceId",
  },
  marital: {
    title: "Marital",
    name: "maritalStatusId",
  },
  city: {
    title: "City",
    name: "city",
  },
  street: {
    title: "Street",
    name: "street",
  },
  state: {
    title: "State",
    name: "stateId",
  },
  zipcode: {
    title: "ZipCode",
    name: "zipCode",
  },
  phone: {
    title: "Phone",
    name: "phone",
  },
  cellphone: {
    title: "CellPhone",
    name: "cellPhone",
  },
  email: {
    title: "Email",
    name: "email",
  },
};

/**
 * 右侧自定义属性展示内容
 */
export const propertyList = {
  fullname: ["hideNumber", "startWithNewLine", "title"],
  firstname: ["hideNumber", "startWithNewLine", "title"],
  middlename: ["hideNumber", "startWithNewLine", "title"],
  lastname: ["hideNumber", "startWithNewLine", "title"],
  birthdate: ["hideNumber", "startWithNewLine", "title"],
  gender: ["hideNumber", "startWithNewLine", "title"],
  race: ["hideNumber", "startWithNewLine", "title"],
  marital: ["hideNumber", "startWithNewLine", "title"],
  city: ["hideNumber", "startWithNewLine", "title"],
  street: ["hideNumber", "startWithNewLine", "title"],
  state: ["hideNumber", "startWithNewLine", "title"],
  zipcode: ["hideNumber", "startWithNewLine", "title"],
  phone: ["hideNumber", "startWithNewLine", "title"],
  cellphone: ["hideNumber", "startWithNewLine", "title"],
  email: ["hideNumber", "startWithNewLine", "title"],
  serviceplan: ["startWithNewLine"],
  triggerworkflow: ["hideNumber", "startWithNewLine", "title", "visibleIf", "defaultValue", "readOnly"],
  triggerserviceplan: ["hideNumber", "startWithNewLine", "title", "visibleIf", "defaultValue", "readOnly"],
};

// 原有组件自定义属性展示
export const oldPropertyList = [
  "text",
  "checkbox",
  "ranking",
  "radiogroup",
  "dropdown",
  "tagbox",
  "comment",
  "rating",
  "imagepicker",
  "boolean",
  "image",
  "html",
  "signaturepad",
];

export const oldPropertyListNotShow = {
  text: [
    "dataList",
    "autocomplete",
    "effectiveColSpan",
    "useDisplayValuesInDynamicTexts",
    "textUpdateMode",
    "correctAnswer",
    "clearIfInvisible",
    "expression",
    "file",
    "matrix",
    "matrixdropdown",
    "matrixdynamic",
    "panel",
    "paneldynamic",
  ],
  checkbox: ["choicesByUrl", "url", "path", "effectiveColSpan"],
  ranking: ["choicesByUrl", "url", "path", "effectiveColSpan"],
  radiogroup: ["choicesByUrl", "url", "path", "effectiveColSpan"],
  dropdown: ["choicesByUrl", "url", "path", "effectiveColSpan"],
  tagbox: ["choicesByUrl", "url", "path", "effectiveColSpan"],
  comment: ["effectiveColSpan"],
  rating: ["effectiveColSpan"],
  imagepicker: ["choicesByUrl", "url", "path", "effectiveColSpan"],
  boolean: ["effectiveColSpan"],
  image: ["effectiveColSpan"],
  html: ["effectiveColSpan"],
  signaturepad: ["effectiveColSpan"],
  expression: ["effectiveColSpan"],
  file: ["effectiveColSpan"],
  matrix: ["effectiveColSpan"],
  matrixdropdown: ["effectiveColSpan"],
  matrixdynamic: ["effectiveColSpan"],
  panel: ["effectiveColSpan"],
  paneldynamic: ["effectiveColSpan"],
};
