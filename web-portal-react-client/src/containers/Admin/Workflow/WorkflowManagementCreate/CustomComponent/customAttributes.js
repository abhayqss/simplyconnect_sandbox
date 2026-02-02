import { Serializer } from "survey-core";
import { localization, PropertyGridEditorCollection } from "survey-creator-core";

const translations = localization.getLocale("");

export function CustomAttributes() {
  // Add a score property to survey
  Serializer.addProperty("survey", {
    name: "score",
    category: "general",
    visibleIndex: 0,
  });

  // Add a score property to the Page class
  Serializer.addProperty("page", {
    name: "score",
    category: "general",
    visibleIndex: 0,
  });

  // Add a score property to question
  Serializer.addProperty("question", {
    name: "score",
    type: "expression",
    category: "general",
    default: null,
    visibleIndex: 0,

    onSetValue: (survey, value) => {
      // You can perform required checks or modify the `value` here
      survey.setPropertyValue("score", value);
      // You can perform required actions after the `value` is set
    },
  });

  // Register a custom property editor for the "score" property
  const scorePropertyEditor = {
    // Define the editor's configuration
    getJSON: function (obj, prop, options) {
      // Return a SurveyJS question configuration for the editor
      return {
        type: "text",
        placeholder: "Enter score value",
        title: "Score",
        description:
          "If it is a single item, please enter the score directly e.g., 1; if there are multiple items, please use commas to separate them e.g., 1,2,3,4",
        maxLength: 50,
      };
    },

    // Define conditions for when this editor should be used
    fit: function (prop) {
      // Apply this editor when the property name is "score"
      return prop.name === "score";
    },
  };

  // Register the custom editor to the PropertyGridEditorCollection
  PropertyGridEditorCollection.register(scorePropertyEditor);

  // Add translation for property help text
  translations.pehelp.score =
    "If it is a single item, please enter the score directly e.g., 1; if there are multiple items, please use commas to separate them e.g., 1,2,3,4";
}
