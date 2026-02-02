import { ComponentCollection, Serializer } from "survey-core";

export function initDocumentESign() {
  const data = JSON.parse(localStorage.getItem("workflowData"));

  const filteredCommunity = data.filteredCommunity;
  const filteredOrg = data.filteredOrg;

  filteredCommunity?.unshift({ text: "", value: "" });
  filteredOrg?.unshift({ text: "", value: "" });

  console.log(filteredOrg, filteredCommunity);

  ComponentCollection.Instance.add({
    name: "documentESign",
    title: "Document (Need E-sign)",
    elementsJSON: [
      {
        name: "Organization",
        type: "dropdown",
        placeholder: "Select a Organization...",
        choices: filteredOrg,
      },
      {
        name: "Community",
        type: "dropdown",
        placeholder: "Select a Community...",
        choices: filteredCommunity,
      },
      {
        name: "Template",
        type: "dropdown",
        placeholder: "Select a Template*...",
        choicesByUrl: {
          url: "https://surveyjs.io/api/CountriesExample",
        },
      },
    ],
    //SurveyJS calls this function one time on registing component, after creating "fullname" class.
    onInit() {
      //SurveyJS will create a new class "fullname". We can add properties for this class onInit()
      Serializer.addProperty("fullname", {
        name: "showMiddleName:boolean",
        default: false,
        category: "general",
      });
    },
    //SurveyJS calls this function after creating new question and loading it's properties from JSON
    //It calls in runtime and at design-time (after loading from JSON) and pass the current component/root question as parameter
    onLoaded(question) {
      this.changeMiddleVisibility(question);
    },
    //SurveyJS calls this on a property change in the component/root question
    //It has three parameters that are self explained
    onPropertyChanged(question, propertyName, newValue) {
      if (propertyName == "showMiddleName") {
        this.changeMiddleVisibility(question);
      }
    },
    //The custom function that used in onLoaded and onPropertyChanged functions
    changeMiddleVisibility(question) {
      //get middle question from the content panel
      let middle = question.contentPanel.getQuestionByName("middleName");
      if (!!middle) {
        //Set visible property based on component/root question showMiddleName property
        middle.visible = question.showMiddleName === true;
      }
    },
  });
}
