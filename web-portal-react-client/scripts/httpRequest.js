const axios = require("axios");

const FormData = require("form-data");

async function makeHttpRequest() {
  const url = "https://stg.simplyconnect.me/web-portal-backend/workflowTemplate";

  const form = new FormData();
  form.append("categoryId", "19");
  // form.append("communityIds[0]", "153876");
  form.append("communityIds[0]", "6");
  form.append("communityIds[1]", "7");
  form.append("communityIds[2]", "387317");
  form.append("communityIds[3]", "387325");
  form.append("communityIds[4]", "387334");
  form.append("communityIds[5]", "387344");
  form.append("communityIds[6]", "387359");
  form.append("communityIds[7]", "387360");
  form.append("communityIds[8]", "387361");
  form.append("communityIds[9]", "387370");
  form.append("communityIds[10]", "387371");
  form.append("communityIds[11]", "387380");
  form.append("communityIds[12]", "387385");
  form.append("communityIds[13]", "387391");
  form.append("documentESign", "false");
  form.append("name", "HUD Standards for Success - test-2");
  form.append("ongoingWorkflow", "false");
  form.append("organizationId", "2");
  form.append("scoreWorkflow", "false");
  form.append("id", "373");
  form.append("isCreateServicePlan", "false");
  form.append("code", "HUD Standards for Success - test-2");
  form.append(
    "content",
    JSON.stringify({
      pages: [
        {
          name: "page1",
          elements: [
            {
              type: "panel",
              name: "questionClient",
              title: "Client",
              state: "expanded",
              innerIndent: 1,
              elements: [
                {
                  type: "panel",
                  name: "questionDemographics",
                  title: "Demographics",
                  state: "expanded",
                  innerIndent: 1,
                  showQuestionNumbers: "off",
                  elements: [
                    {
                      type: "text",
                      name: "firstName",
                      title: "First Name",
                      hideNumber: true,
                      isRequired: true,
                    },
                    {
                      type: "text",
                      name: "lastName",
                      startWithNewLine: false,
                      title: "Last Name",
                      hideNumber: true,
                      isRequired: true,
                    },
                    {
                      type: "text",
                      name: "birthDate",
                      startWithNewLine: false,
                      title: "Date Of Birth",
                      hideNumber: true,
                      isRequired: true,
                      inputType: "date",
                    },
                    {
                      type: "dropdown",
                      name: "genderId",
                      title: "Gender",
                      hideNumber: true,
                      isRequired: true,
                      score: "1,2,3,4,5,6,77,88,99,0",
                      choices: [
                        {
                          value: "3",
                          text: "Male",
                        },
                        {
                          value: "2",
                          text: "Female",
                        },
                        {
                          value: "676213",
                          text: "Transgender Male",
                        },
                        {
                          value: "676214",
                          text: "Transgender Female",
                        },
                        {
                          value: "676215",
                          text: "Other",
                        },
                        {
                          value: "676216",
                          text: "Non-Binary",
                        },
                        {
                          value: "676217",
                          text: "Information not Collected",
                        },
                        {
                          value: "676218",
                          text: "Individual Refused",
                        },
                        {
                          value: "676219",
                          text: "Individual does not Know",
                        },
                        {
                          value: "4",
                          text: "Undefined",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "ethnicityId",
                      startWithNewLine: false,
                      title: "Ethnicity",
                      hideNumber: true,
                      score: "1,2,77,88,99",
                      choices: [
                        {
                          value: "676224",
                          text: "Hispanic/Latino",
                        },
                        {
                          value: "676227",
                          text: "Individual Refused",
                        },
                        {
                          value: "676228",
                          text: "Individual does not Know",
                        },
                        {
                          value: "676226",
                          text: "Information not Collected",
                        },
                        {
                          value: "676225",
                          text: "Not Hispanic/Latino",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "maritalStatusId",
                      title: "Martial Status",
                      hideNumber: true,
                      choices: [
                        {
                          value: "5",
                          text: "Annulled",
                        },
                        {
                          value: "6",
                          text: "Divorced",
                        },
                        {
                          value: "7",
                          text: "Domestic Partner",
                        },
                        {
                          value: "8",
                          text: "Interlocutory",
                        },
                        {
                          value: "9",
                          text: "Legally Separated",
                        },
                        {
                          value: "10",
                          text: "Married",
                        },
                        {
                          value: "11",
                          text: "Never Married",
                        },
                        {
                          value: "12",
                          text: "Polygamous",
                        },
                        {
                          value: "13",
                          text: "Widowed",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "raceId",
                      startWithNewLine: false,
                      title: "Race",
                      hideNumber: true,
                      score: "1,2,3,4,5,6,77,88,99,0",
                      choices: [
                        {
                          value: "135",
                          text: "American Indian or Alaska Native",
                        },
                        {
                          value: "152",
                          text: "Asian",
                        },
                        {
                          value: "183",
                          text: "Black or African American",
                        },
                        {
                          value: "605",
                          text: "Native Hawaiian or other Pacific Islander",
                        },
                        {
                          value: "975",
                          text: "White",
                        },
                        {
                          value: "525714",
                          text: "Mixed",
                        },
                        {
                          value: "525715",
                          text: "Information not Collected",
                        },
                        {
                          value: "525716",
                          text: "Individual Refused",
                        },
                        {
                          value: "525717",
                          text: "Individual does not Know",
                        },
                        {
                          value: "667",
                          text: "Other Race",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "headOfHousehold",
                      title: "Head of Household",
                      hideNumber: true,
                      score: "1,2,77,88,99",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                        {
                          value: "Item 10",
                          text: "Information not Collected",
                        },
                        {
                          value: "Item 4",
                          text: "Individual Refused",
                        },
                        {
                          value: "Item 5",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "veteranStatus",
                      startWithNewLine: false,
                      title: "Veteran Status",
                      hideNumber: true,
                      score: "1,2,77,88,99",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                        {
                          value: "Item 10",
                          text: "Information not Collected",
                        },
                        {
                          value: "Item 4",
                          text: "Individual Refused",
                        },
                        {
                          value: "Item 5",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "disabilityStatus",
                      title: "Disability Status",
                      score: "1,2,66,77,88,99",
                      hideNumber: true,
                      isRequired: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes, individual indicates a disability as defined in ADA",
                        },
                        {
                          value: "Item 2",
                          text: "No, individual indicates no disability as defined by ADA",
                        },
                        {
                          value: "Item 3",
                          text: "N/A",
                        },
                        {
                          value: "Item 4",
                          text: "Information not Collected",
                        },
                        {
                          value: "Item 5",
                          text: "Individual Refused",
                        },
                        {
                          value: "Item 6",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "disabilityCategory1",
                      visibleIf: "{disabilityStatus} = 'Item 1'",
                      title: "Disability Category",
                      hideNumber: true,
                      clearIfInvisible: "onHidden",
                      requiredIf: "{disabilityStatus} = 'Item 1'",
                      score: "1,2,3,66,77,88,99",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Impairment is primarily physical, including mobility and sensory impairments",
                        },
                        {
                          value: "Item 2",
                          text: "Impairment is primarily physical, including mobility and sensory impairments",
                        },
                        {
                          value: "Item 3",
                          text: "Impairment is both physical and mental",
                        },
                        {
                          value: "Item 4",
                          text: "N/A",
                        },
                        {
                          value: "Item 5",
                          text: "Information not Collected",
                        },
                        {
                          value: "Item 6",
                          text: "Individual Refused",
                        },
                        {
                          value: "Item 7",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "disabilityRequiresAssistance1",
                      visibleIf: "{disabilityStatus} = 'Item 1'",
                      startWithNewLine: false,
                      title: "Disability  Assistance",
                      hideNumber: true,
                      score: "1,2,3,66,77,88,99",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{disabilityStatus} = 'Item 1'",
                      choices: [
                        {
                          value: "Item 1",
                          text: "The individual with a disability requires services to manage home activities",
                        },
                        {
                          value: "Item 2",
                          text: "The individual with a disability does not require services for home management",
                        },
                        {
                          value: "Item 3",
                          text: "The individual with a disability was not assessed for these criteria",
                        },
                        {
                          value: "Item 4",
                          text: "N/A",
                        },
                        {
                          value: "Item 5",
                          text: "Information not Collected",
                        },
                        {
                          value: "Item 6",
                          text: "Individual Refused",
                        },
                        {
                          value: "Item 7",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "disabilityCategory2",
                      visibleIf: "{disabilityStatus} <> 'Item 1' and {disabilityStatus} notempty",
                      title: "Disability Category",
                      hideNumber: true,
                      defaultValue: "Item 4",
                      clearIfInvisible: "onHidden",
                      score: "1,2,3,66,77,88,99",
                      readOnly: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "Impairment is primarily physical, including mobility and sensory impairments",
                        },
                        {
                          value: "Item 2",
                          text: "Impairment is primarily physical, including mobility and sensory impairments",
                        },
                        {
                          value: "Item 3",
                          text: "Impairment is both physical and mental",
                        },
                        {
                          value: "Item 4",
                          text: "N/A",
                        },
                        {
                          value: "Item 5",
                          text: "Information not Collected",
                        },
                        {
                          value: "Item 6",
                          text: "Individual Refused",
                        },
                        {
                          value: "Item 7",
                          text: "Individual does not Know",
                        },
                      ],
                      placeholder: "N/A",
                    },
                    {
                      type: "dropdown",
                      name: "disabilityRequiresAssistance2",
                      visibleIf: "{disabilityStatus} <> 'Item 1' and {disabilityStatus} notempty",
                      startWithNewLine: false,
                      title: "Disability  Assistance",
                      hideNumber: true,
                      defaultValue: "Item 4",
                      clearIfInvisible: "onHidden",
                      score: "1,2,3,66,77,88,99",
                      readOnly: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "The individual with a disability requires services to manage home activities",
                        },
                        {
                          value: "Item 2",
                          text: "The individual with a disability does not require services for home management",
                        },
                        {
                          value: "Item 3",
                          text: "The individual with a disability was not assessed for these criteria",
                        },
                        {
                          value: "Item 4",
                          text: "N/A",
                        },
                        {
                          value: "Item 5",
                          text: "Information not Collected",
                        },
                        {
                          value: "Item 6",
                          text: "Individual Refused",
                        },
                        {
                          value: "Item 7",
                          text: "Individual does not Know",
                        },
                      ],
                      placeholder: "N/A",
                    },
                    {
                      type: "text",
                      name: "GAI",
                      title: "Gross Annual Income",
                      hideNumber: true,
                      resetValueIf: "{monthlyPaidEarnings} > 0 or {SSINumber} > 0 or {SSDINumber} > 0",
                      setValueExpression: "{monthlyPaidEarnings} * 12 + {SSINumber} * 12 + {SSDINumber} * 12",
                      defaultValue: "Item 4",
                      clearIfInvisible: "onHidden",
                      readOnly: true,
                      inputType: "number",
                      placeholder: "N/A",
                    },
                    {
                      type: "text",
                      name: "ssn",
                      title: "Social Security Number",
                      hideNumber: true,
                      enableIf: "{hasNoSSN} empty",
                      resetValueIf: "{hasNoSSN} = ['Item 1']",
                      requiredIf: "{hasNoSSN} empty",
                      inputType: "number",
                      min: 0,
                      max: 999999999,
                      step: 1,
                      placeholder: "XXX XX XXXX",
                    },
                    {
                      type: "checkbox",
                      name: "hasNoSSN",
                      titleLocation: "hidden",
                      hideNumber: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "Client doesn't have SSN",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "stateId",
                      title: "State",
                      hideNumber: true,
                      isRequired: true,
                      choices: [
                        {
                          value: "1",
                          text: "Alabama (AL)",
                        },
                        {
                          value: "37",
                          text: "Alaska (AK)",
                        },
                        {
                          value: "56",
                          text: "American Samoa (AS)",
                        },
                        {
                          value: "2",
                          text: "Arizona (AZ)",
                        },
                        {
                          value: "38",
                          text: "Arkansas (AR)",
                        },
                        {
                          value: "3",
                          text: "California (CA)",
                        },
                        {
                          value: "39",
                          text: "Colorado (CO)",
                        },
                        {
                          value: "4",
                          text: "Connecticut (CT)",
                        },
                        {
                          value: "40",
                          text: "Delaware (DE)",
                        },
                        {
                          value: "51",
                          text: "District of Columbia (DC)",
                        },
                        {
                          value: "5",
                          text: "Florida (FL)",
                        },
                        {
                          value: "6",
                          text: "Georgia (GA)",
                        },
                        {
                          value: "54",
                          text: "Guam (GU)",
                        },
                        {
                          value: "41",
                          text: "Hawaii (HI)",
                        },
                        {
                          value: "7",
                          text: "Idaho (ID)",
                        },
                        {
                          value: "42",
                          text: "Illinois (IL)",
                        },
                        {
                          value: "8",
                          text: "Indiana (IN)",
                        },
                        {
                          value: "43",
                          text: "Iowa (IA)",
                        },
                        {
                          value: "9",
                          text: "Kansas (KS)",
                        },
                        {
                          value: "44",
                          text: "Kentucky (KY)",
                        },
                        {
                          value: "45",
                          text: "Louisiana (LA)",
                        },
                        {
                          value: "22",
                          text: "Maine (ME)",
                        },
                        {
                          value: "46",
                          text: "Maryland (MD)",
                        },
                        {
                          value: "23",
                          text: "Massachusetts (MA)",
                        },
                        {
                          value: "47",
                          text: "Michigan (MI)",
                        },
                        {
                          value: "24",
                          text: "Minnesota (MN)",
                        },
                        {
                          value: "48",
                          text: "Mississippi (MS)",
                        },
                        {
                          value: "25",
                          text: "Missouri (MO)",
                        },
                        {
                          value: "49",
                          text: "Montana (MT)",
                        },
                        {
                          value: "26",
                          text: "Nebraska (NE)",
                        },
                        {
                          value: "27",
                          text: "Nevada (NV)",
                        },
                        {
                          value: "10",
                          text: "New Hampshire (NH)",
                        },
                        {
                          value: "28",
                          text: "New Hampshire (NH)",
                        },
                        {
                          value: "11",
                          text: "New Mexico (NM)",
                        },
                        {
                          value: "29",
                          text: "New York (NY)",
                        },
                        {
                          value: "30",
                          text: "North Carolina (NC)",
                        },
                        {
                          value: "12",
                          text: "North Dakota (ND)",
                        },
                        {
                          value: "55",
                          text: "Northern Mariana Islands (MP)",
                        },
                        {
                          value: "31",
                          text: "Ohio (OH)",
                        },
                        {
                          value: "13",
                          text: "Oklahoma (OK)",
                        },
                        {
                          value: "14",
                          text: "Oregon (OR)",
                        },
                        {
                          value: "32",
                          text: "Pennsylvania (PA)",
                        },
                        {
                          value: "52",
                          text: "Puerto Rico (PR)",
                        },
                        {
                          value: "15",
                          text: "Rhode Island (RI)",
                        },
                        {
                          value: "33",
                          text: "South Carolina (SC)",
                        },
                        {
                          value: "16",
                          text: "South Dakota (SD)",
                        },
                        {
                          value: "17",
                          text: "Tennessee (TN)",
                        },
                        {
                          value: "50",
                          text: "Texas (TX)",
                        },
                        {
                          value: "53",
                          text: "US Virgin Islands (VI)",
                        },
                        {
                          value: "18",
                          text: "Utah (UT)",
                        },
                        {
                          value: "34",
                          text: "Vermont (VT)",
                        },
                        {
                          value: "19",
                          text: "Virginia (VA)",
                        },
                        {
                          value: "35",
                          text: "Washington (WA)",
                        },
                        {
                          value: "20",
                          text: "West Virginia (WV)",
                        },
                        {
                          value: "36",
                          text: "Wisconsin (WI)",
                        },
                        {
                          value: "21",
                          text: "Wyoming (WY)",
                        },
                      ],
                    },
                    {
                      type: "text",
                      name: "city",
                      startWithNewLine: false,
                      title: "City",
                      hideNumber: true,
                      isRequired: true,
                    },
                    {
                      type: "text",
                      name: "zipCode",
                      title: "Zip Code",
                      hideNumber: true,
                      isRequired: true,
                      validators: [
                        {
                          type: "expression",
                        },
                      ],
                      inputType: "number",
                      max: 99999,
                      maxErrorText: "The value should not be greater than {99999}}",
                      step: 1,
                    },
                    {
                      type: "text",
                      name: "street",
                      startWithNewLine: false,
                      title: "Street",
                      hideNumber: true,
                      isRequired: true,
                    },
                    {
                      type: "text",
                      name: "createdDate",
                      title: "Move In Date",
                      hideNumber: true,
                      readOnly: true,
                      inputType: "date",
                    },
                    {
                      type: "text",
                      name: "deactivatedDate",
                      startWithNewLine: false,
                      title: "Move Out Date",
                      hideNumber: true,
                      readOnly: true,
                      inputType: "date",
                    },
                    {
                      type: "radiogroup",
                      name: "lastReason",
                      title: "Move Out Reason",
                      hideNumber: true,
                      choices: [
                        {
                          value: "COMPLETED_PROGRAM",
                          text: "Completed Program",
                        },
                        {
                          value: "CRIMINAL_ACTIVITY",
                          text: "Criminal Activity/ Destruction of Property/ Violence",
                        },
                        {
                          value: "DECLINED_SERVICES",
                          text: "Declined Services",
                        },
                        {
                          value: "DISAGREEMENT_WITH_RULES_PERSONS",
                          text: "Disagreement with Rules/Persons",
                        },
                        {
                          value: "GRADUATED",
                          text: "Graduated",
                        },
                        {
                          value: "LEFT_FOR_HOUSING_OPPORTUNITY",
                          text: "Left for Housing Opportunity",
                        },
                        {
                          value: "LIVES_OUTSIDE_SERVICE_AREA",
                          text: "Lives Outside Service Area",
                        },
                        {
                          value: "NEEDS_COULD_NOT_BE_MET",
                          text: "Needs Could Not be Met",
                        },
                        {
                          value: "NO_CONTACT",
                          text: "No Contact",
                        },
                        {
                          value: "NON_COMPLIANCE",
                          text: "Non-Compliance with Program",
                        },
                        {
                          value: "NON_PAYMENT",
                          text: "Non-Payment of rent/ occupancy",
                        },
                        {
                          value: "REACHED_MAXIMUM",
                          text: "Reached Maximum Time Allowed by program",
                        },
                        {
                          value: "UNKNOWN_DISAPPEAR",
                          text: "Unknown/Disappeared",
                        },
                        {
                          value: "1",
                          text: "Moved Out: Purchased a home",
                        },
                        {
                          value: "2",
                          text: "Moved Out: Another apartment or rental property",
                        },
                        {
                          value: "3",
                          text: "Moved Out: Higher Level of Care",
                        },
                        {
                          value: "4",
                          text: "Moved Out: With family",
                        },
                        {
                          value: "5",
                          text: "Moved Out: Other (e.g. Lease Termination)",
                        },
                        {
                          value: "6",
                          text: "Moved Out: Unknown",
                        },
                        {
                          value: "7",
                          text: "Eviction",
                        },
                        {
                          value: "8",
                          text: "Death",
                        },
                        {
                          value: "9",
                          text: "Lease Not Renewed",
                        },
                        {
                          value: "10",
                          text: "Could not afford rent increase",
                        },
                        {
                          value: "77",
                          text: "Information not collected",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "0",
                          text: "Other",
                        },
                      ],
                    },
                    {
                      type: "text",
                      name: "question4",
                      title: "Primary Health Care",
                      hideNumber: true,
                    },
                    {
                      type: "paneldynamic",
                      name: "question68",
                      titleLocation: "hidden",
                      hideNumber: true,
                      templateElements: [
                        {
                          type: "dropdown",
                          name: "healthCoverage",
                          title: "Health Coverage",
                          titleLocation: "top",
                          hideNumber: true,
                          score: "1,2,3,4,5,6,7,8,9,66,77,88,99",
                          choices: [
                            {
                              value: "1",
                              text: "Yes, covered through employer or union (current or former)",
                            },
                            {
                              value: "2",
                              text: "Yes, purchased insurance from insurance company",
                            },
                            {
                              value: "3",
                              text: "Medicare",
                            },
                            {
                              value: "4",
                              text: "Medicaid/Medical Assistance",
                            },
                            {
                              value: "5",
                              text: "TRICARE or other military health care",
                            },
                            {
                              value: "6",
                              text: "VA health care",
                            },
                            {
                              value: "7",
                              text: "Indian Health Service",
                            },
                            {
                              value: "8",
                              text: "Other health insurance or health coverage plan",
                            },
                            {
                              value: "9",
                              text: "No coverage",
                            },
                            {
                              value: "66",
                              text: "N/A",
                            },
                            {
                              value: "77",
                              text: "Information not Collected",
                            },
                            {
                              value: "88",
                              text: "Individual Refused",
                            },
                            {
                              value: "99",
                              text: "Individual does not Know",
                            },
                          ],
                        },
                        {
                          type: "dropdown",
                          name: "question3",
                          title: "Network",
                          titleLocation: "top",
                          hideNumber: true,
                          choices: [
                            {
                              value: "2",
                              text: "Cash or self-payment ",
                            },
                            {
                              value: "101",
                              text: "1199 Local Benefit Fund ",
                            },
                            {
                              value: "100",
                              text: "610014 - MEDC3 COMMERICAL HIT ",
                            },
                            {
                              value: "50",
                              text: "Advantra ",
                            },
                            {
                              value: "77",
                              text: "AdvoCare ",
                            },
                            {
                              value: "1",
                              text: "Aetna ",
                            },
                            {
                              value: "90",
                              text: "Aetna Better Health (My Care Ohio) ",
                            },
                            {
                              value: "102",
                              text: "AETNA US HEALTHCARE ",
                            },
                            {
                              value: "91",
                              text: "American Medical Security/United Healthcare ",
                            },
                            {
                              value: "88",
                              text: "Americo ",
                            },
                            {
                              value: "51",
                              text: "Ancillary Care Services (ACS) ",
                            },
                            {
                              value: "52",
                              text: "Anthem Blue Cross ",
                            },
                            {
                              value: "89",
                              text: "Bankers Fidelity ",
                            },
                            {
                              value: "92",
                              text: "BCE Emergis/Multiplan ",
                            },
                            {
                              value: "93",
                              text: "Beach Street/Multiplan ",
                            },
                            {
                              value: "13",
                              text: "Blue Cross and Blue Shield of Illinois ",
                            },
                            {
                              value: "14",
                              text: "Blue Cross and Blue Shield of Louisiana ",
                            },
                            {
                              value: "15",
                              text: "Blue Cross Blue Shield Massachusetts  Medicare Advantage ",
                            },
                            {
                              value: "16",
                              text: "Blue Cross Blue Shield of Massachusetts ",
                            },
                            {
                              value: "83",
                              text: "Blue Cross Blue Shield of Michigan ",
                            },
                            {
                              value: "17",
                              text: "Blue Cross Blue Shield of North Dakota ",
                            },
                            {
                              value: "18",
                              text: "Blue Shield of CA ",
                            },
                            {
                              value: "87",
                              text: "Buckeye Health Plan ",
                            },
                            {
                              value: "78",
                              text: "Bureau of Workers' Compensation ",
                            },
                            {
                              value: "19",
                              text: "CareFirst BlueCross BlueShield ",
                            },
                            {
                              value: "55",
                              text: "CareSource ",
                            },
                            {
                              value: "20",
                              text: "Cigna Healthcare ",
                            },
                            {
                              value: "21",
                              text: "Coastal Healthcare ",
                            },
                            {
                              value: "79",
                              text: "Cofinity ",
                            },
                            {
                              value: "22",
                              text: "Cofinity  Inc. ",
                            },
                            {
                              value: "84",
                              text: "Constitution Life Insurance Company (Supplement) ",
                            },
                            {
                              value: "56",
                              text: "CoreSource ",
                            },
                            {
                              value: "11",
                              text: "County or local government funds ",
                            },
                            {
                              value: "57",
                              text: "Coventry Health Care ",
                            },
                            {
                              value: "94",
                              text: "Definity Health/United HealthCare ",
                            },
                            {
                              value: "23",
                              text: "Fallon Community Health Plan ",
                            },
                            {
                              value: "58",
                              text: "First Health ",
                            },
                            {
                              value: "59",
                              text: "Flora Midwest ",
                            },
                            {
                              value: "85",
                              text: "ForeThought (Supplement) ",
                            },
                            {
                              value: "60",
                              text: "FrontPath Health Coalition ",
                            },
                            {
                              value: "61",
                              text: "Galaxy Health Network ",
                            },
                            {
                              value: "80",
                              text: "GEHA ",
                            },
                            {
                              value: "86",
                              text: "Golden Rule ",
                            },
                            {
                              value: "95",
                              text: "Golden Rule/United HealthCare ",
                            },
                            {
                              value: "24",
                              text: "Gundersen Health Plan ",
                            },
                            {
                              value: "25",
                              text: "Hawaii Medicare Service Association ",
                            },
                            {
                              value: "104",
                              text: "Health Net ",
                            },
                            {
                              value: "96",
                              text: "Health Plan of the Upper Ohio Valley/The Health Plan ",
                            },
                            {
                              value: "27",
                              text: "Health Plus of Louisiana ",
                            },
                            {
                              value: "103",
                              text: "HEALTHCARE INSURANCE ",
                            },
                            {
                              value: "26",
                              text: "HealthChoice of Oklahoma ",
                            },
                            {
                              value: "62",
                              text: "HealthSCOPE Benefits ",
                            },
                            {
                              value: "63",
                              text: "HealthSmart ",
                            },
                            {
                              value: "28",
                              text: "Healthsmart WTC Program ",
                            },
                            {
                              value: "64",
                              text: "HealthSpan ",
                            },
                            {
                              value: "30",
                              text: "Highmark Blue Cross Blue Delaware ",
                            },
                            {
                              value: "29",
                              text: "Highmark Blue Cross Blue Shield ",
                            },
                            {
                              value: "31",
                              text: "Highmark Blue Cross Blue West Virginia ",
                            },
                            {
                              value: "32",
                              text: "Highmark Blue Shield ",
                            },
                            {
                              value: "97",
                              text: "HomeTown Health Network/The Health Plan ",
                            },
                            {
                              value: "65",
                              text: "Humana ",
                            },
                            {
                              value: "49",
                              text: "I'll choose my insurance later ",
                            },
                            {
                              value: "5",
                              text: "IHS/Tribal/Urban (ITU) funds ",
                            },
                            {
                              value: "33",
                              text: "Independence Blue Cross ",
                            },
                            {
                              value: "3",
                              text: "Medicaid ",
                            },
                            {
                              value: "66",
                              text: "Medical Mutual of Ohio ",
                            },
                            {
                              value: "81",
                              text: "Medicare ",
                            },
                            {
                              value: "67",
                              text: "Meritain Health ",
                            },
                            {
                              value: "68",
                              text: "Multiplan ",
                            },
                            {
                              value: "34",
                              text: "New Mexico Health Connections ",
                            },
                            {
                              value: "35",
                              text: "Northeast Medical Services ",
                            },
                            {
                              value: "54",
                              text: "Ohio Bureau of Workers' Compensation ",
                            },
                            {
                              value: "69",
                              text: "Ohio Health Choice (OHC) ",
                            },
                            {
                              value: "71",
                              text: "Ohio Health Group ",
                            },
                            {
                              value: "70",
                              text: "Ohio PPO Connect ",
                            },
                            {
                              value: "10",
                              text: "Other State funds ",
                            },
                            {
                              value: "36",
                              text: "Pacific Independent Physician Association ",
                            },
                            {
                              value: "37",
                              text: "Paramount ",
                            },
                            {
                              value: "105",
                              text: "Partnership Health ",
                            },
                            {
                              value: "38",
                              text: "Physicians Health Plan of Northern Indiana ",
                            },
                            {
                              value: "72",
                              text: "PPOM/Cofinity ",
                            },
                            {
                              value: "39",
                              text: "PreferredOne ",
                            },
                            {
                              value: "40",
                              text: "Premera Blue Cross ",
                            },
                            {
                              value: "41",
                              text: "Priority Health Managed Benefits  Inc. ",
                            },
                            {
                              value: "98",
                              text: "Private Healthcare Systems (PHCS)/MultiPlan ",
                            },
                            {
                              value: "42",
                              text: "Providence Health Plan ",
                            },
                            {
                              value: "43",
                              text: "Sanford Health Plan ",
                            },
                            {
                              value: "44",
                              text: "Scripps Health ",
                            },
                            {
                              value: "99",
                              text: "Secure Horizons/United HealthCare ",
                            },
                            {
                              value: "45",
                              text: "SelectHealth Inc. ",
                            },
                            {
                              value: "46",
                              text: "Sharp Rees Stealy ",
                            },
                            {
                              value: "8",
                              text: "State corrections or juvenile justice funds ",
                            },
                            {
                              value: "9",
                              text: "State education funds ",
                            },
                            {
                              value: "4",
                              text: "State financed health insurance plan other than Medicaid ",
                            },
                            {
                              value: "6",
                              text: "State mental health agency (or equivalent) funds ",
                            },
                            {
                              value: "7",
                              text: "State welfare or child and family services funds ",
                            },
                            {
                              value: "73",
                              text: "SummaCare ",
                            },
                            {
                              value: "74",
                              text: "The Health Plan ",
                            },
                            {
                              value: "12",
                              text: "U.S Department of VA funds ",
                            },
                            {
                              value: "106",
                              text: "UCare ",
                            },
                            {
                              value: "47",
                              text: "United Healthcare ",
                            },
                            {
                              value: "76",
                              text: "United HealthCare Community Plan ",
                            },
                            {
                              value: "82",
                              text: "United Healthcare of Ohio ",
                            },
                            {
                              value: "75",
                              text: "USA Managed Care Organization (USA MCO) ",
                            },
                            {
                              value: "48",
                              text: "Wellmark Blue Cross Blue Shield ",
                            },
                          ],
                        },
                        {
                          type: "text",
                          name: "question40",
                          title: "Plan",
                          titleLocation: "top",
                          hideNumber: true,
                        },
                        {
                          type: "text",
                          name: "question42",
                          title: "Group Number",
                          titleLocation: "top",
                          hideNumber: true,
                          inputType: "number",
                        },
                        {
                          type: "text",
                          name: "question65",
                          startWithNewLine: false,
                          title: "Member  Number",
                          titleLocation: "top",
                          hideNumber: true,
                          inputType: "number",
                        },
                      ],
                      allowRemovePanel: false,
                      panelCount: 1,
                      panelAddText: "Add Insurance",
                      templateTitleLocation: "top",
                      panelRemoveButtonLocation: "right",
                    },
                    {
                      type: "text",
                      name: "question46",
                      title: "Medicare  Number",
                      titleLocation: "top",
                      hideNumber: true,
                      enableIf: "{question48} empty",
                      resetValueIf: "{question48} = ['Item 1']",
                      requiredIf: "{question48} empty",
                      inputType: "number",
                    },
                    {
                      type: "checkbox",
                      name: "question49",
                      title: "Medicare  Number",
                      titleLocation: "hidden",
                      hideNumber: true,
                      isRequired: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "Client doesn't have medicaidNumber",
                        },
                      ],
                    },
                    {
                      type: "text",
                      name: "question47",
                      title: "Medicaid Number",
                      titleLocation: "top",
                      hideNumber: true,
                      enableIf: "{question49} empty",
                      resetValueIf: "{question49} = ['Item 1']",
                      requiredIf: "{question49} empty",
                      inputType: "number",
                    },
                    {
                      type: "checkbox",
                      name: "question48",
                      title: "Medicare  Number",
                      titleLocation: "hidden",
                      hideNumber: true,
                      isRequired: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "Client doesn't have medicareNumber",
                        },
                      ],
                    },
                    {
                      type: "checkbox",
                      name: "question50",
                      title: "Add Authorization",
                      titleLocation: "hidden",
                      hideNumber: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "Add Authorization",
                        },
                      ],
                    },
                    {
                      type: "panel",
                      name: "question51",
                      visibleIf: "{question50} = ['Item 1']",
                      requiredIf: "{question50} = ['Item 1']",
                      title: "Authorization",
                      innerIndent: 1,
                      showQuestionNumbers: "off",
                      elements: [
                        {
                          type: "text",
                          name: "question52",
                          title: "Start Date",
                          hideNumber: true,
                          isRequired: true,
                          inputType: "date",
                        },
                        {
                          type: "text",
                          name: "question93",
                          startWithNewLine: false,
                          title: "End  Date",
                          isRequired: true,
                          inputType: "date",
                        },
                        {
                          type: "text",
                          name: "question54",
                          title: "Authorization Number",
                          isRequired: true,
                          inputType: "number",
                        },
                        {
                          type: "paneldynamic",
                          name: "question91",
                          titleLocation: "hidden",
                          templateElements: [
                            {
                              type: "text",
                              name: "question92",
                              startWithNewLine: false,
                              title: "Start Date",
                              isRequired: true,
                              inputType: "date",
                            },
                            {
                              type: "text",
                              name: "question53",
                              startWithNewLine: false,
                              title: "End  Date",
                              isRequired: true,
                              inputType: "date",
                            },
                            {
                              type: "text",
                              name: "question94",
                              title: "Authorization Number",
                              isRequired: true,
                              inputType: "number",
                            },
                          ],
                          panelAddText: "Add Authorization",
                          templateTitleLocation: "top",
                          panelRemoveButtonLocation: "right",
                        },
                      ],
                    },
                    {
                      type: "panel",
                      name: "PrimaryContact",
                      title: "Primary Contact",
                      innerIndent: 1,
                      elements: [
                        {
                          type: "radiogroup",
                          name: "question64Primarynotificationmethod",
                          title: "Primary contact",
                          hideNumber: true,
                          isRequired: true,
                          choices: [
                            {
                              value: "Item 1",
                              text: "Self",
                            },
                            {
                              value: "Item 2",
                              text: "Care team member",
                            },
                          ],
                        },
                        {
                          type: "radiogroup",
                          name: "question95",
                          title: "Primary notification method",
                          hideNumber: true,
                          isRequired: true,
                          choices: [
                            {
                              value: "Item 1",
                              text: "Email",
                            },
                            {
                              value: "Item 2",
                              text: "Phone",
                            },
                          ],
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "highestEducationLevel",
                      title: "Highest Education Level",
                      hideNumber: true,
                      score: "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,66,77,88,99",
                      choices: [
                        {
                          value: "0",
                          text: "No schooling completed, Nursery school, or Kindergarten",
                        },
                        {
                          value: "1",
                          text: "1st Grade completed",
                        },
                        {
                          value: "2",
                          text: "2nd Grade completed",
                        },
                        {
                          value: "3",
                          text: "3rd Grade completed",
                        },
                        {
                          value: "4",
                          text: "4th Grade completed",
                        },
                        {
                          value: "5",
                          text: "5th Grade completed",
                        },
                        {
                          value: "6",
                          text: "6th Grade completed",
                        },
                        {
                          value: "7",
                          text: "7th Grade completed",
                        },
                        {
                          value: "8",
                          text: "8th Grade completed",
                        },
                        {
                          value: "9",
                          text: "9th Grade completed",
                        },
                        {
                          value: "10",
                          text: "10th Grade completed",
                        },
                        {
                          value: "11",
                          text: "11th Grade completed",
                        },
                        {
                          value: "12",
                          text: "12th Grade completed, no diploma",
                        },
                        {
                          value: "13",
                          text: "High school diploma",
                        },
                        {
                          value: "14",
                          text: "GED or alternative credential",
                        },
                        {
                          value: "15",
                          text: "Less than 1 year of college credit",
                        },
                        {
                          value: "16",
                          text: "1 or more years of college credit, no degree",
                        },
                        {
                          value: "17",
                          text: "Associate’s degree",
                        },
                        {
                          value: "18",
                          text: "Bachelor’s degree",
                        },
                        {
                          value: "19",
                          text: "Master’s degree",
                        },
                        {
                          value: "20",
                          text: "Professional degree (e.g. MD, DDS, DVM. LLB, JD)",
                        },
                        {
                          value: "21",
                          text: "Doctorate degree ",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "question79",
                      title: "Primary Language",
                      hideNumber: true,
                      choices: [
                        {
                          value: "11",
                          text: "Chinese",
                        },
                        {
                          value: "8",
                          text: "English",
                        },
                        {
                          value: "10",
                          text: "French",
                        },
                        {
                          value: "6",
                          text: "Hmong",
                        },
                        {
                          value: "2",
                          text: "Native American Indian or Alaska Native languages",
                        },
                        {
                          value: "3",
                          text: "Russian",
                        },
                        {
                          value: "4",
                          text: "Services for the deaf and hard of hearing",
                        },
                        {
                          value: "5",
                          text: "Somalian",
                        },
                        {
                          value: "1",
                          text: "Spanish",
                        },
                        {
                          value: "9",
                          text: "Translation services",
                        },
                        {
                          value: "7",
                          text: "Other languages",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "interpreterTranslator",
                      title: "Interpreter/Translator",
                      hideNumber: true,
                      score: "1,2,66,77",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                      ],
                    },
                    {
                      type: "dropdown",
                      name: "functionallyLiterate",
                      title: "Literacy question",
                      hideNumber: true,
                      score: "1,2,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "checkbox",
                      name: "question55",
                      title: "Retained",
                      titleLocation: "hidden",
                      hideNumber: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "Advanced Directive",
                        },
                      ],
                      colCount: 0,
                    },
                    {
                      type: "checkbox",
                      name: "question90",
                      visibleIf: "{question55} allof ['Item 1']",
                      title: "Advanced Directive",
                      titleLocation: "top",
                      hideNumber: true,
                      clearIfInvisible: "onHidden",
                      requiredIf: "{question55} allof ['Item 1']",
                      choices: [
                        {
                          value: "1",
                          text: "DNR",
                        },
                        {
                          value: "2",
                          text: "POA",
                        },
                        {
                          value: "3",
                          text: "Living Will",
                        },
                      ],
                    },
                    {
                      type: "panel",
                      name: "panel6",
                      title: "HIE Opt In / Opt Out",
                      elements: [
                        {
                          type: "html",
                          name: "question64",
                          title: "Primary notification method",
                          hideNumber: true,
                          html: "A Health Information Exchange (HIE) is a way for health care providers participating in the HIE to share health information with each other through a secure, electronic means so that health care providers have the benefit of the most current available information. Simply Connect participates in HIEs in order to aid in the facilitation and coordination of your healthcare.<br/>PRIVACY AND SECURITY. Federal and state laws govern how your health information can be exchanged, viewed, or used through an HIE. Simply Connect is committed to keeping your electronic health record private and secure, and only provides, views or uses your health information consistent with those laws. <br/>PARTICIPATION IN HIEs. Through its participation in HIEs, Simply Connect makes patient information available electronically to other HIE participants (e.g., participating hospitals, doctors, health plans and government agencies). We may also receive information about patients from other HIE participants. We expect that using HIEs will provide faster and more complete access to your health information to make more informed decisions about your care.",
                        },
                        {
                          type: "checkbox",
                          name: "OPTOUT",
                          titleLocation: "hidden",
                          hideNumber: true,
                          enableIf: "{OPTIN} empty",
                          choices: [
                            {
                              value: "Item 1",
                              text: "OPT-OUT for all health information: I DO NOT want any of my information visible within the HIEs in which Simply Connect participates.",
                            },
                          ],
                        },
                        {
                          type: "html",
                          name: "question66",
                          html: "<div style='margin-left: 30px'><div style='display:flex; margin-left:20px;'> <div style='margin-right:10px'>·</div> <div>I understand that the applicable health information received by any Simply Connect provider WILL NOT BE VISIBLE in the HIEs in which Simply Connect participates. THIS INCLUDES EMERGENCY SITUATIONS.</div></div><div style='display:flex; margin-left:20px;'> <div style='margin-right:10px'>·</div> <div>I understand that I am free to revoke this Opt-Out request at any time and can do so by completing a new Opt-In/Opt-Out form.</div></div><div style='display:flex; margin-left:20px;'> <div style='margin-right:10px'>·</div> <div>I understand that this request only applies to sharing my health information with HIEs and that a health care provider may request and receive my medical information from other providers using other methods permitted by law. If you have previously opted out of participating in HIEs and want to reverse that decision, check the box below. Your health information from the period during which you had opted-out may be available through the HIEs after you decide to opt back in.</div></div>",
                        },
                        {
                          type: "checkbox",
                          name: "OPTIN",
                          titleLocation: "hidden",
                          hideNumber: true,
                          enableIf: "{OPTOUT} empty",
                          choices: [
                            {
                              value: "Item 1",
                              text: "OPT-IN/Cancel OPT-OUT: I WANT my information visible in the HIEs in which Simply Connect participates.",
                            },
                          ],
                        },
                        {
                          type: "text",
                          name: "question67",
                          title: "Obtained from",
                          hideNumber: true,
                          isRequired: true,
                        },
                        {
                          type: "radiogroup",
                          name: "question69",
                          title: "Obtained by",
                          hideNumber: true,
                          isRequired: true,
                          choices: [
                            {
                              value: "Item 1",
                              text: "Client",
                            },
                            {
                              value: "Item 2",
                              text: "Responsible Party",
                            },
                            {
                              value: "Item 3",
                              text: "Representative",
                            },
                          ],
                        },
                        {
                          type: "text",
                          name: "question70",
                          title: "Date Obtained",
                          hideNumber: true,
                          isRequired: true,
                          inputType: "date",
                        },
                      ],
                    },
                  ],
                },
                {
                  type: "panel",
                  name: "question97",
                  title: "Power of  Attorney  (POA)",
                  state: "collapsed",
                  innerIndent: 1,
                  elements: [
                    {
                      type: "paneldynamic",
                      name: "attorneys",
                      visible: false,
                      indent: 1,
                      titleLocation: "hidden",
                      hideNumber: true,
                      templateElements: [
                        {
                          type: "text",
                          name: "question23",
                          title: "First Name",
                          titleLocation: "top",
                          hideNumber: true,
                          isRequired: true,
                        },
                        {
                          type: "text",
                          name: "question18",
                          startWithNewLine: false,
                          title: "Last Name",
                          titleLocation: "top",
                          hideNumber: true,
                          isRequired: true,
                        },
                        {
                          type: "checkbox",
                          name: "types",
                          title: "POA Type",
                          titleLocation: "top",
                          hideNumber: true,
                          isRequired: true,
                          choices: [
                            {
                              value: "Item 1",
                              text: "Medical",
                            },
                            {
                              value: "Item 2",
                              text: "Financial",
                            },
                            {
                              value: "Item 3",
                              text: "Guardian",
                            },
                            {
                              value: "Item 4",
                              text: "Health Care Agent",
                            },
                          ],
                          showSelectAllItem: true,
                          selectAllText: "All",
                        },
                        {
                          type: "text",
                          name: "email002",
                          startWithNewLine: false,
                          title: "Email",
                          titleLocation: "top",
                          hideNumber: true,
                          inputType: "email",
                        },
                        {
                          type: "text",
                          name: "phone",
                          title: "Phone",
                          titleLocation: "top",
                          hideNumber: true,
                          isRequired: true,
                          inputType: "tel",
                          placeholder: "+XXX XXXXXX",
                        },
                        {
                          type: "text",
                          name: "attorneys.street",
                          startWithNewLine: false,
                          title: "Street",
                          titleLocation: "top",
                          hideNumber: true,
                          isRequired: true,
                        },
                        {
                          type: "text",
                          name: "question25",
                          startWithNewLine: false,
                          title: "City",
                          hideNumber: true,
                          isRequired: true,
                        },
                        {
                          type: "dropdown",
                          name: "question27",
                          title: "State",
                          titleLocation: "top",
                          hideNumber: true,
                          isRequired: true,
                          choices: [
                            {
                              value: "1",
                              text: "Alabama (AL)",
                            },
                            {
                              value: "37",
                              text: "Alaska (AK)",
                            },
                            {
                              value: "56",
                              text: "American Samoa (AS)",
                            },
                            {
                              value: "2",
                              text: "Arizona (AZ)",
                            },
                            {
                              value: "38",
                              text: "Arkansas (AR)",
                            },
                            {
                              value: "3",
                              text: "California (CA)",
                            },
                            {
                              value: "39",
                              text: "Colorado (CO)",
                            },
                            {
                              value: "4",
                              text: "Connecticut (CT)",
                            },
                            {
                              value: "40",
                              text: "Delaware (DE)",
                            },
                            {
                              value: "51",
                              text: "District of Columbia (DC)",
                            },
                            {
                              value: "5",
                              text: "Florida (FL)",
                            },
                            {
                              value: "6",
                              text: "Georgia (GA)",
                            },
                            {
                              value: "54",
                              text: "Guam (GU)",
                            },
                            {
                              value: "41",
                              text: "Hawaii (HI)",
                            },
                            {
                              value: "7",
                              text: "Idaho (ID)",
                            },
                            {
                              value: "42",
                              text: "Illinois (IL)",
                            },
                            {
                              value: "8",
                              text: "Indiana (IN)",
                            },
                            {
                              value: "43",
                              text: "Iowa (IA)",
                            },
                            {
                              value: "9",
                              text: "Kansas (KS)",
                            },
                            {
                              value: "44",
                              text: "Kentucky (KY)",
                            },
                            {
                              value: "45",
                              text: "Louisiana (LA)",
                            },
                            {
                              value: "22",
                              text: "Maine (ME)",
                            },
                            {
                              value: "46",
                              text: "Maryland (MD)",
                            },
                            {
                              value: "23",
                              text: "Massachusetts (MA)",
                            },
                            {
                              value: "47",
                              text: "Michigan (MI)",
                            },
                            {
                              value: "24",
                              text: "Minnesota (MN)",
                            },
                            {
                              value: "48",
                              text: "Mississippi (MS)",
                            },
                            {
                              value: "25",
                              text: "Missouri (MO)",
                            },
                            {
                              value: "49",
                              text: "Montana (MT)",
                            },
                            {
                              value: "26",
                              text: "Nebraska (NE)",
                            },
                            {
                              value: "27",
                              text: "Nevada (NV)",
                            },
                            {
                              value: "10",
                              text: "New Hampshire (NH)",
                            },
                            {
                              value: "28",
                              text: "New Hampshire (NH)",
                            },
                            {
                              value: "11",
                              text: "New Mexico (NM)",
                            },
                            {
                              value: "29",
                              text: "New York (NY)",
                            },
                            {
                              value: "30",
                              text: "North Carolina (NC)",
                            },
                            {
                              value: "12",
                              text: "North Dakota (ND)",
                            },
                            {
                              value: "55",
                              text: "Northern Mariana Islands (MP)",
                            },
                            {
                              value: "31",
                              text: "Ohio (OH)",
                            },
                            {
                              value: "13",
                              text: "Oklahoma (OK)",
                            },
                            {
                              value: "14",
                              text: "Oregon (OR)",
                            },
                            {
                              value: "32",
                              text: "Pennsylvania (PA)",
                            },
                            {
                              value: "52",
                              text: "Puerto Rico (PR)",
                            },
                            {
                              value: "15",
                              text: "Rhode Island (RI)",
                            },
                            {
                              value: "33",
                              text: "South Carolina (SC)",
                            },
                            {
                              value: "16",
                              text: "South Dakota (SD)",
                            },
                            {
                              value: "17",
                              text: "Tennessee (TN)",
                            },
                            {
                              value: "50",
                              text: "Texas (TX)",
                            },
                            {
                              value: "53",
                              text: "US Virgin Islands (VI)",
                            },
                            {
                              value: "18",
                              text: "Utah (UT)",
                            },
                            {
                              value: "34",
                              text: "Vermont (VT)",
                            },
                            {
                              value: "19",
                              text: "Virginia (VA)",
                            },
                            {
                              value: "35",
                              text: "Washington (WA)",
                            },
                            {
                              value: "20",
                              text: "West Virginia (WV)",
                            },
                            {
                              value: "36",
                              text: "Wisconsin (WI)",
                            },
                            {
                              value: "21",
                              text: "Wyoming (WY)",
                            },
                          ],
                          textWrapEnabled: false,
                        },
                        {
                          type: "text",
                          name: "question26",
                          startWithNewLine: false,
                          title: "Zip Code",
                          titleLocation: "top",
                          hideNumber: true,
                          isRequired: true,
                          inputType: "number",
                          max: 99999,
                          maxErrorText: "The value should not be greater than {99999}}",
                          step: 1,
                        },
                      ],
                      panelAddText: "Add POA",
                      panelRemoveText: "Remove",
                      templateTitleLocation: "top",
                      panelRemoveButtonLocation: "right",
                    },
                  ],
                },
                {
                  type: "panel",
                  name: "contact",
                  title: "Contact",
                  state: "collapsed",
                  innerIndent: 1,
                  indent: 1,
                  elements: [
                    {
                      type: "paneldynamic",
                      name: "question85",
                      titleLocation: "hidden",
                      templateElements: [
                        {
                          type: "dropdown",
                          name: "question37",
                          title: "Type",
                          hideNumber: true,
                          isRequired: true,
                          choices: [
                            {
                              value: "Item 1",
                              text: "Emergency",
                            },
                            {
                              value: "Item 2",
                              text: "Family",
                            },
                            {
                              value: "Item 3",
                              text: "Spouse",
                            },
                            {
                              value: "Item 4",
                              text: "Children",
                            },
                            {
                              value: "Item 5",
                              text: "Others",
                            },
                          ],
                        },
                        {
                          type: "text",
                          name: "question36",
                          title: "First Name",
                          hideNumber: true,
                          isRequired: true,
                        },
                        {
                          type: "text",
                          name: "question11",
                          startWithNewLine: false,
                          title: "Last Name",
                          hideNumber: true,
                          isRequired: true,
                        },
                        {
                          type: "text",
                          name: "question30",
                          title: "Phone",
                          hideNumber: true,
                          isRequired: true,
                          inputType: "tel",
                          placeholder: "+XXX  XXXXXXX",
                        },
                        {
                          type: "text",
                          name: "question29",
                          startWithNewLine: false,
                          title: "Email",
                          hideNumber: true,
                          inputType: "email",
                        },
                        {
                          type: "text",
                          name: "question31",
                          title: "Street",
                          hideNumber: true,
                        },
                        {
                          type: "text",
                          name: "question32",
                          startWithNewLine: false,
                          title: "City",
                          hideNumber: true,
                        },
                        {
                          type: "dropdown",
                          name: "stateSelect",
                          title: "State",
                          hideNumber: true,
                          choices: [
                            {
                              value: "1",
                              text: "Alabama (AL)",
                            },
                            {
                              value: "37",
                              text: "Alaska (AK)",
                            },
                            {
                              value: "56",
                              text: "American Samoa (AS)",
                            },
                            {
                              value: "2",
                              text: "Arizona (AZ)",
                            },
                            {
                              value: "38",
                              text: "Arkansas (AR)",
                            },
                            {
                              value: "3",
                              text: "California (CA)",
                            },
                            {
                              value: "39",
                              text: "Colorado (CO)",
                            },
                            {
                              value: "4",
                              text: "Connecticut (CT)",
                            },
                            {
                              value: "40",
                              text: "Delaware (DE)",
                            },
                            {
                              value: "51",
                              text: "District of Columbia (DC)",
                            },
                            {
                              value: "5",
                              text: "Florida (FL)",
                            },
                            {
                              value: "6",
                              text: "Georgia (GA)",
                            },
                            {
                              value: "54",
                              text: "Guam (GU)",
                            },
                            {
                              value: "41",
                              text: "Hawaii (HI)",
                            },
                            {
                              value: "7",
                              text: "Idaho (ID)",
                            },
                            {
                              value: "42",
                              text: "Illinois (IL)",
                            },
                            {
                              value: "8",
                              text: "Indiana (IN)",
                            },
                            {
                              value: "43",
                              text: "Iowa (IA)",
                            },
                            {
                              value: "9",
                              text: "Kansas (KS)",
                            },
                            {
                              value: "44",
                              text: "Kentucky (KY)",
                            },
                            {
                              value: "45",
                              text: "Louisiana (LA)",
                            },
                            {
                              value: "22",
                              text: "Maine (ME)",
                            },
                            {
                              value: "46",
                              text: "Maryland (MD)",
                            },
                            {
                              value: "23",
                              text: "Massachusetts (MA)",
                            },
                            {
                              value: "47",
                              text: "Michigan (MI)",
                            },
                            {
                              value: "24",
                              text: "Minnesota (MN)",
                            },
                            {
                              value: "48",
                              text: "Mississippi (MS)",
                            },
                            {
                              value: "25",
                              text: "Missouri (MO)",
                            },
                            {
                              value: "49",
                              text: "Montana (MT)",
                            },
                            {
                              value: "26",
                              text: "Nebraska (NE)",
                            },
                            {
                              value: "27",
                              text: "Nevada (NV)",
                            },
                            {
                              value: "10",
                              text: "New Hampshire (NH)",
                            },
                            {
                              value: "28",
                              text: "New Hampshire (NH)",
                            },
                            {
                              value: "11",
                              text: "New Mexico (NM)",
                            },
                            {
                              value: "29",
                              text: "New York (NY)",
                            },
                            {
                              value: "30",
                              text: "North Carolina (NC)",
                            },
                            {
                              value: "12",
                              text: "North Dakota (ND)",
                            },
                            {
                              value: "55",
                              text: "Northern Mariana Islands (MP)",
                            },
                            {
                              value: "31",
                              text: "Ohio (OH)",
                            },
                            {
                              value: "13",
                              text: "Oklahoma (OK)",
                            },
                            {
                              value: "14",
                              text: "Oregon (OR)",
                            },
                            {
                              value: "32",
                              text: "Pennsylvania (PA)",
                            },
                            {
                              value: "52",
                              text: "Puerto Rico (PR)",
                            },
                            {
                              value: "15",
                              text: "Rhode Island (RI)",
                            },
                            {
                              value: "33",
                              text: "South Carolina (SC)",
                            },
                            {
                              value: "16",
                              text: "South Dakota (SD)",
                            },
                            {
                              value: "17",
                              text: "Tennessee (TN)",
                            },
                            {
                              value: "50",
                              text: "Texas (TX)",
                            },
                            {
                              value: "53",
                              text: "US Virgin Islands (VI)",
                            },
                            {
                              value: "18",
                              text: "Utah (UT)",
                            },
                            {
                              value: "34",
                              text: "Vermont (VT)",
                            },
                            {
                              value: "19",
                              text: "Virginia (VA)",
                            },
                            {
                              value: "35",
                              text: "Washington (WA)",
                            },
                            {
                              value: "20",
                              text: "West Virginia (WV)",
                            },
                            {
                              value: "36",
                              text: "Wisconsin (WI)",
                            },
                            {
                              value: "21",
                              text: "Wyoming (WY)",
                            },
                          ],
                          textWrapEnabled: false,
                        },
                        {
                          type: "text",
                          name: "question34",
                          startWithNewLine: false,
                          title: "Zip Code",
                          hideNumber: true,
                          inputType: "number",
                          max: 99999,
                          maxErrorText: "The value should not be greater than {99999}}",
                          step: 1,
                        },
                      ],
                      panelAddText: "Add  Contact",
                      panelRemoveText: "Remove",
                      templateTitleLocation: "top",
                      panelRemoveButtonLocation: "right",
                    },
                  ],
                },
                {
                  type: "panel",
                  name: "Telecom",
                  questionTitleLocation: "top",
                  title: "Telecom",
                  state: "collapsed",
                  innerIndent: 1,
                  elements: [
                    {
                      type: "text",
                      name: "cellPhone",
                      title: "Cell Phone",
                      hideNumber: true,
                      isRequired: true,
                      inputType: "tel",
                      placeholder: "+XXX XXXXXXX",
                    },
                    {
                      type: "text",
                      name: "question38",
                      startWithNewLine: false,
                      title: "Home Phone",
                      hideNumber: true,
                      inputType: "tel",
                      placeholder: "+XXX XXXXXXX",
                    },
                    {
                      type: "text",
                      name: "email",
                      title: "Email",
                      hideNumber: true,
                      enableIf: "{doNotHaveEmail} empty",
                      requiredIf: "{doNotHaveEmail} empty",
                      inputType: "email",
                    },
                    {
                      type: "checkbox",
                      name: "doNotHaveEmail",
                      titleLocation: "hidden",
                      hideNumber: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "Client doesn't have email",
                        },
                      ],
                    },
                  ],
                },
                {
                  type: "panel",
                  name: "Financial",
                  requiredIf: "SSDI",
                  title: "Financial",
                  state: "collapsed",
                  innerIndent: 1,
                  elements: [
                    {
                      type: "text",
                      name: "grossMonthlyIncome",
                      title: "Gross Monthly Income,$",
                      description: "Whole dollars per month to calculate gross annual income.",
                      hideNumber: true,
                      inputType: "number",
                      min: 0,
                      step: 1,
                    },
                    {
                      type: "text",
                      name: "householdHousingCost",
                      title: "Household Housing Cost,$",
                      description: "Whole Numbers Only",
                      hideNumber: true,
                      inputType: "number",
                      min: 0,
                      step: 1,
                    },
                    {
                      type: "text",
                      name: "householdTransportationCost",
                      title: "Household Transportation Cost,$",
                      description: "Whole Numbers Only",
                      hideNumber: true,
                      inputType: "number",
                      min: 0,
                      step: 1,
                    },
                    {
                      type: "text",
                      name: "monthlyPaidEarnings",
                      title: "Monthly Paid Earnings,$",
                      description: "Whole Numbers Only",
                      hideNumber: true,
                      inputType: "number",
                      min: 0,
                      step: 1,
                    },
                    {
                      type: "dropdown",
                      name: "SSI",
                      title: "Supplemental Security Income (SSI)",
                      score: "1,2,66,77,88,99",
                      hideNumber: true,
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                      autocomplete: "tel-local",
                    },
                    {
                      type: "text",
                      name: "SSINumber",
                      visibleIf: "{SSI} = 1",
                      title: "Amount Received, $",
                      description: "Whole Numbers Only",
                      hideNumber: true,
                      requiredIf: "{SSI} = 1",
                      inputType: "number",
                      min: 0,
                      step: 1,
                    },
                    {
                      type: "dropdown",
                      name: "SSDI",
                      title: "Social Security Disability Insurance (SSDI)",
                      hideNumber: true,
                      score: "1,2,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                      autocomplete: "tel-local",
                    },
                    {
                      type: "text",
                      name: "SSDINumber",
                      visibleIf: "{SSDI} = 1",
                      title: "Amount Received, $",
                      description: "Whole Numbers Only",
                      hideNumber: true,
                      requiredIf: "{SSDI} = 1",
                      inputType: "number",
                      min: 0,
                      step: 1,
                    },
                    {
                      type: "dropdown",
                      name: "employmentStatus",
                      title: "Employment  Status",
                      hideNumber: true,
                      score: "1,2,3,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Employed",
                        },
                        {
                          value: "2",
                          text: "Not employed at any time in the last month and actively seeking work",
                        },
                        {
                          value: "3",
                          text: "Not employed at any time in the last month and not actively seeking work",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                      autocomplete: "tel-local",
                    },
                    {
                      type: "dropdown",
                      name: "employmentTypeStatus",
                      title: "Employment Type Status",
                      hideNumber: true,
                      score: "1,2,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Full-time worker employed in the last month",
                        },
                        {
                          value: "2",
                          text: "Part-time worker employed in the last month",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                      autocomplete: "tel-local",
                    },
                  ],
                },
                {
                  type: "panel",
                  name: "panel4",
                  title: "Ancillary Information",
                  state: "collapsed",
                  innerIndent: 1,
                  elements: [
                    {
                      type: "radiogroup",
                      name: "question56",
                      title: "Retained",
                      hideNumber: true,
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                      colCount: 4,
                    },
                    {
                      type: "dropdown",
                      name: "primaryHealthCareProvider",
                      title: "Primary Health Care Provider",
                      titleLocation: "top",
                      hideNumber: true,
                      score: "1,2,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "text",
                      name: "primaryHealthCareServiceEndDate",
                      title: "Primary Health Care Service End Date",
                      titleLocation: "top",
                      hideNumber: true,
                      inputType: "date",
                    },
                    {
                      type: "text",
                      name: "question57",
                      title: "PCP First Name",
                      titleLocation: "top",
                      hideNumber: true,
                    },
                    {
                      type: "text",
                      name: "question58",
                      startWithNewLine: false,
                      title: "PCP Last Name",
                      titleLocation: "top",
                      hideNumber: true,
                    },
                    {
                      type: "text",
                      name: "question59",
                      title: "Intake Date",
                      titleLocation: "top",
                      hideNumber: true,
                      inputType: "date",
                    },
                    {
                      type: "text",
                      name: "question60",
                      title: "Current Pharmacy Name",
                      titleLocation: "top",
                      hideNumber: true,
                    },
                  ],
                },
              ],
            },
            {
              type: "panel",
              name: "assessment",
              elements: [
                {
                  type: "panel",
                  name: "panelquestion1",
                  elements: [
                    {
                      type: "html",
                      name: "guidequestion1",
                      html: "The SeniorConnect assessment tool is a vehicle to explore, discover, document, understand and address the social determinants of health (SDOH) that can impact a senior's chronic condition and, if left unchecked or unmanaged, could worsen and escalate their status from merely chronic to frail or complex. <br/>The focus of SeniorConnect as a program is to help seniors engage in managing their own chronic conditions and ideally stabilize or slow the progression of their condition. As a service coordinator, you play an essential and important role in this engagement process. <br/>Keep in mind that the SeniorConnect assessment tool is not a scoring instrument or an algorithm that produces a 'score' or defines specific interventions or actions. It is designed to help you learn more about the senior as you apply your professional knowledge, experience, and critical thinking skills to develop a service plan that connects the senior to services in their community and identifies areas of learning for the senior around their condition.",
                    },
                  ],
                  title: "Guide",
                  innerIndent: 1,
                },
                {
                  type: "panel",
                  name: "General2",
                  elements: [
                    {
                      type: "panel",
                      name: "panel1",
                      elements: [
                        {
                          type: "checkbox",
                          name: "question1111",
                          title: "Who is completing the assessment?",
                          hideNumber: true,
                          renderAs: "prettycheckbox",
                          choices: ["Caregiver", "Family", "Patient", "Other"],
                        },
                      ],
                      title: "General",
                      state: "expanded",
                      innerIndent: 1,
                    },
                    {
                      type: "panel",
                      name: "panel18",
                      elements: [
                        {
                          type: "radiogroup",
                          name: "Areyouaveteran15",
                          title: "Are you a veteran?",
                          hideNumber: true,
                          renderAs: "prettycheckbox",
                          choices: ["Yes", "No"],
                        },
                        {
                          type: "radiogroup",
                          name: "AreyoureceivingVeteranbenefits16",
                          title: "Are you receiving Veteran benefits?",
                          hideNumber: true,
                          renderAs: "prettycheckbox",
                          choices: ["Yes", "No"],
                        },
                        {
                          type: "comment",
                          name: "HealthCareSurrogate18",
                          title: "Health Care Surrogate",
                          hideNumber: true,
                          maxLength: 5000,
                        },
                        {
                          type: "text",
                          name: "Monthlyincome19",
                          title: "Monthly income, $",
                          hideNumber: true,
                          inputType: "number",
                          placeholder: "$",
                        },
                        {
                          type: "radiogroup",
                          name: "Foodsupport20",
                          title: "Food support",
                          hideNumber: true,
                          renderAs: "prettycheckbox",
                          choices: ["Yes", "No", "Unknown"],
                        },
                        {
                          type: "panel",
                          name: "Emergencycontact131",
                          elements: [
                            {
                              type: "text",
                              name: "demographics_emergencyContact1_firstName32",
                              title: "First name",
                              titleLocation: "top",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "demographics_emergencyContact1_lastName33",
                              startWithNewLine: false,
                              title: "Last Name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "demographics_emergencyContact1_phoneNumber34",
                              title: "Phone number",
                              placeholder: "+XXX XXXXXX",
                            },
                            {
                              type: "panel",
                              name: "demographics_emergencyContact1_address35",
                              elements: [
                                {
                                  type: "text",
                                  name: "demographics_emergencyContact1_address_street36",
                                  title: "Street",
                                  maxLength: 256,
                                },
                                {
                                  type: "text",
                                  name: "demographics_emergencyContact1_address_city37",
                                  startWithNewLine: false,
                                  title: "City",
                                  maxLength: 256,
                                },
                                {
                                  type: "dropdown",
                                  name: "demographics_emergencyContact1_address_state38",
                                  title: "State",
                                  choicesFromQuestion: "stateId",
                                },
                                {
                                  type: "text",
                                  name: "demographics_emergencyContact1_address_zipCode39",
                                  startWithNewLine: false,
                                  title: "Zip Code",
                                  inputType: "number",
                                },
                              ],
                              title: "Address",
                              innerIndent: 1,
                            },
                          ],
                          questionTitleLocation: "top",
                          title: "Emergency contact #1",
                          state: "collapsed",
                          innerIndent: 1,
                          showQuestionNumbers: "off",
                        },
                        {
                          type: "panel",
                          name: "Emergencycontact240",
                          elements: [
                            {
                              type: "text",
                              name: "demographics_emergencyContact2_firstName41",
                              title: "First name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "demographics_emergencyContact2_lastName42",
                              startWithNewLine: false,
                              title: "Last Name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "demographics_emergencyContact2_phoneNumber43",
                              title: "Phone number",
                              inputType: "tel",
                              placeholder: "+XXX XXXXXX",
                            },
                            {
                              type: "panel",
                              name: "demographics_emergencyContact2_address44",
                              elements: [
                                {
                                  type: "text",
                                  name: "demographics_emergencyContact2_address_street45",
                                  title: "Street",
                                  maxLength: 256,
                                },
                                {
                                  type: "text",
                                  name: "demographics_emergencyContact2_address_city46",
                                  startWithNewLine: false,
                                  title: "City",
                                  maxLength: 256,
                                },
                                {
                                  type: "dropdown",
                                  name: "demographics_emergencyContact2_address_state47",
                                  title: "State",
                                  choicesFromQuestion: "stateId",
                                },
                                {
                                  type: "text",
                                  name: "demographics_emergencyContact2_address_zipCode48",
                                  startWithNewLine: false,
                                  title: "Zip Code",
                                  inputType: "number",
                                  max: 99999,
                                  step: 1,
                                },
                              ],
                              title: "Address",
                              innerIndent: 1,
                            },
                          ],
                          title: "Emergency contact #2",
                          state: "collapsed",
                          innerIndent: 1,
                          showQuestionNumbers: "off",
                        },
                        {
                          type: "panel",
                          name: "Emergencycontact349",
                          elements: [
                            {
                              type: "text",
                              name: "demographics_emergencyContact3_firstName50",
                              title: "First name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "demographics_emergencyContact3_lastName51",
                              startWithNewLine: false,
                              title: "Last Name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "demographics_emergencyContact3_phoneNumber52",
                              title: "Phone number",
                              inputType: "tel",
                              placeholder: "+XXX XXXXXX",
                            },
                            {
                              type: "panel",
                              name: "demographics_emergencyContact3_address53",
                              elements: [
                                {
                                  type: "text",
                                  name: "demographics_emergencyContact3_address_street54",
                                  title: "Street",
                                  maxLength: 256,
                                },
                                {
                                  type: "text",
                                  name: "demographics_emergencyContact3_address_city55",
                                  startWithNewLine: false,
                                  title: "City",
                                  maxLength: 256,
                                },
                                {
                                  type: "dropdown",
                                  name: "demographics_emergencyContact3_address_state56",
                                  title: "State",
                                  choicesFromQuestion: "stateId",
                                },
                                {
                                  type: "text",
                                  name: "demographics_emergencyContact3_address_zipCode57",
                                  startWithNewLine: false,
                                  title: "Zip Code",
                                  inputType: "number",
                                  max: 99999,
                                  step: 1,
                                },
                              ],
                              title: "Address",
                              innerIndent: 1,
                            },
                          ],
                          title: "Emergency contact #3",
                          state: "collapsed",
                          innerIndent: 1,
                          showQuestionNumbers: "off",
                        },
                        {
                          type: "panel",
                          name: "PrimaryCarePhysician58",
                          elements: [
                            {
                              type: "text",
                              name: "Firstname259",
                              title: "First name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "LastName260",
                              startWithNewLine: false,
                              title: "Last Name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "Phonenumber161",
                              title: "Phone number",
                              inputType: "tel",
                            },
                            {
                              type: "panel",
                              name: "Address62",
                              elements: [
                                {
                                  type: "text",
                                  name: "Street263",
                                  title: "Street",
                                  maxLength: 256,
                                },
                                {
                                  type: "text",
                                  name: "City264",
                                  startWithNewLine: false,
                                  title: "City",
                                  maxLength: 256,
                                },
                                {
                                  type: "dropdown",
                                  name: "State265",
                                  title: "State",
                                  choicesFromQuestion: "stateId",
                                },
                                {
                                  type: "text",
                                  name: "ZipCode266",
                                  startWithNewLine: false,
                                  title: "Zip Code",
                                  inputType: "number",
                                  max: 99999,
                                  step: 1,
                                },
                              ],
                              title: "Address",
                              innerIndent: 1,
                            },
                          ],
                          title: "Primary Care Physician",
                          state: "collapsed",
                          innerIndent: 1,
                          showQuestionNumbers: "off",
                        },
                        {
                          type: "panel",
                          name: "SpecialtyPhysicians67",
                          elements: [
                            {
                              type: "text",
                              name: "Firstname368",
                              title: "First name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "LastName369",
                              startWithNewLine: false,
                              title: "Last Name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "Specialty70",
                              title: "Specialty",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "Phonenumber271",
                              startWithNewLine: false,
                              title: "Phone number",
                              inputType: "tel",
                              placeholder: "+XXX XXXXXX",
                            },
                            {
                              type: "panel",
                              name: "Address72",
                              elements: [
                                {
                                  type: "text",
                                  name: "Street373",
                                  title: "Street",
                                  maxLength: 256,
                                },
                                {
                                  type: "text",
                                  name: "City374",
                                  startWithNewLine: false,
                                  title: "City",
                                  maxLength: 256,
                                },
                                {
                                  type: "dropdown",
                                  name: "State375",
                                  title: "State",
                                  choicesFromQuestion: "stateId",
                                },
                                {
                                  type: "text",
                                  name: "ZipCode376",
                                  startWithNewLine: false,
                                  title: "Zip Code",
                                  inputType: "number",
                                  max: 9999,
                                  step: 1,
                                },
                              ],
                              title: "Address",
                              innerIndent: 1,
                            },
                          ],
                          title: "Specialty Physicians",
                          state: "collapsed",
                          innerIndent: 1,
                          showQuestionNumbers: "off",
                        },
                        {
                          type: "panel",
                          name: "Pharmacy77",
                          elements: [
                            {
                              type: "text",
                              name: "Name78",
                              title: "Name",
                              maxLength: 256,
                            },
                            {
                              type: "text",
                              name: "Phonenumber379",
                              startWithNewLine: false,
                              title: "Phone number",
                              inputType: "tel",
                              placeholder: "+XXX XXXXXX",
                            },
                            {
                              type: "panel",
                              name: "Address80",
                              elements: [
                                {
                                  type: "text",
                                  name: "Street481",
                                  title: "Street",
                                  maxLength: 256,
                                },
                                {
                                  type: "text",
                                  name: "City482",
                                  startWithNewLine: false,
                                  title: "City",
                                  maxLength: 256,
                                },
                                {
                                  type: "dropdown",
                                  name: "State483",
                                  title: "State",
                                  choicesFromQuestion: "stateId",
                                },
                                {
                                  type: "text",
                                  name: "ZipCode484",
                                  startWithNewLine: false,
                                  title: "Zip Code",
                                  inputType: "number",
                                  max: 99999,
                                  step: 1,
                                },
                              ],
                              title: "Address",
                              innerIndent: 1,
                            },
                          ],
                          title: "Pharmacy",
                          state: "collapsed",
                          innerIndent: 1,
                          showQuestionNumbers: "off",
                        },
                      ],
                      title: "Demographics",
                      state: "collapsed",
                      innerIndent: 1,
                    },
                  ],
                  title: "Client Details",
                  state: "expanded",
                  innerIndent: 1,
                },
                {
                  type: "panel",
                  name: "MedicalHistory2",
                  title: "Medical",
                  state: "expanded",
                  innerIndent: 1,
                  showQuestionNumbers: "off",
                  elements: [
                    {
                      type: "radiogroup",
                      name: "question1",
                      title: " 1. Does the Resident appear alert and oriented?",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 3",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "routineMedicalCare",
                      title: "2. Have you been to routine medical checkups in the last 12 months?",
                      score: "1,2,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "Weightlbs34",
                      title: "3. Did you receive any new medical diagnosis?",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "text",
                      name: "question2",
                      visibleIf: "{Weightlbs34} = 'Item 1'",
                      title: "Medical Diagnosis",
                      requiredIf: "{Weightlbs34} = 'Item 1'",
                    },
                    {
                      type: "radiogroup",
                      name: "currentlyChronicConditions",
                      title: "4. Do you currently have any chronic health conditions?",
                      clearIfInvisible: "onHidden",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "text",
                      name: "CMConditions",
                      visibleIf: "{currentlyChronicConditions} = 'Item 1'",
                      title: "Chronic Medical Conditions",
                      requiredIf: "{currentlyChronicConditions} = 'Item 1'",
                    },
                    {
                      type: "radiogroup",
                      name: "question8",
                      title: "5. Do you understand your medical Diagnosis?",
                      clearIfInvisible: "onHidden",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "HIV",
                      title: "6. Have you been diagnosed with HIV?",
                      score: "1,2,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "takingAnyMedication",
                      title: "7. Are you currently taking any medications?",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "comment",
                      name: "Medications49",
                      visibleIf: "{takingAnyMedication} = 'Item 1'",
                      title: "7a. Medications:",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{takingAnyMedication} = 'Item 1'",
                      maxLength: 5000,
                      placeholder: "Flag polypharmacy and high risk meds",
                    },
                    {
                      type: "comment",
                      name: "questionreason",
                      visibleIf: "{takingAnyMedication} = 'Item 1'",
                      title: "7b. Reason:",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{takingAnyMedication} = 'Item 1'",
                      maxLength: 5000,
                    },
                    {
                      type: "radiogroup",
                      name: "pastTwoWeeks",
                      visibleIf: "{takingAnyMedication} = 'Item 1'",
                      title: "7c. Have you missed any prescribed doses in the past two weeks?",
                      requiredIf: "{takingAnyMedication} = 'Item 1'",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "comment",
                      name: "question17",
                      visibleIf: "{pastTwoWeeks} = 'Item 1'",
                      title: "Why?",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{pastTwoWeeks} = 'Item 1'",
                    },
                    {
                      type: "panel",
                      name: "questionImmunizations",
                      title: "8. Recent immunizations",
                      elements: [
                        {
                          type: "checkbox",
                          name: "FluPneumonia",
                          titleLocation: "hidden",
                          hideNumber: true,
                          choices: [
                            {
                              value: "Item 1",
                              text: "Flu/Pneumonia",
                            },
                          ],
                        },
                        {
                          type: "text",
                          name: "FluPneumoniaDate",
                          visibleIf: "{FluPneumonia} allof ['Item 1']",
                          startWithNewLine: false,
                          title: "Date",
                          titleLocation: "left",
                          hideNumber: true,
                        },
                        {
                          type: "checkbox",
                          name: "Hepatitis",
                          titleLocation: "hidden",
                          hideNumber: true,
                          choices: [
                            {
                              value: "Item 1",
                              text: "Hepatitis",
                            },
                          ],
                        },
                        {
                          type: "text",
                          name: "HepatitisDate",
                          visibleIf: "{Hepatitis} allof ['Item 1']",
                          startWithNewLine: false,
                          title: "Date",
                          titleLocation: "left",
                          hideNumber: true,
                        },
                        {
                          type: "checkbox",
                          name: "vaccine",
                          titleLocation: "hidden",
                          hideNumber: true,
                          choices: [
                            {
                              value: "Item 1",
                              text: "COVID-19 vaccine",
                            },
                          ],
                        },
                        {
                          type: "text",
                          name: "COVIDDate",
                          visibleIf: "{vaccine} allof ['Item 1']",
                          startWithNewLine: false,
                          title: "Date",
                          titleLocation: "left",
                          hideNumber: true,
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "ADentist",
                      title: "9. Do you have a Dentist?",
                      renderAs: "prettycheckbox",
                      score: "1,2,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                      choicesOrder: "asc",
                    },
                    {
                      type: "radiogroup",
                      name: "dentistLast12",
                      visibleIf: "{ADentist} = 1",
                      title: "9a. Have you been to the dentist in the last 12 months?",
                      requiredIf: "{ADentist} = 1",
                      score: "1,2,66,77,88,99",
                      renderAs: "prettycheckbox",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                      choicesOrder: "asc",
                    },
                    {
                      type: "comment",
                      name: "commentquestion1",
                      title: "10. When is the last time you have had your vision checked?",
                      renderAs: "prettycheckbox",
                    },
                    {
                      type: "radiogroup",
                      name: "EROrHospital",
                      title: "11. Have you been treated in the ER or hospital in the last 12 months?",
                      renderAs: "prettycheckbox",
                      score: "1,2,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                        {
                          value: "88",
                          text: "Individual Refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not Know",
                        },
                      ],
                      choicesOrder: "asc",
                    },
                    {
                      type: "text",
                      name: "ofVisits",
                      visibleIf: "{EROrHospital} = 1",
                      title: "# of visits",
                      description: "Whole Numbers Only",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{EROrHospital} = 1",
                      inputType: "number",
                      step: 1,
                    },
                    {
                      type: "comment",
                      name: "hospitalVisit",
                      visibleIf: "{EROrHospital} = 1",
                      title: "Reasons for ER/Hospital visit",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{EROrHospital} = 1",
                    },
                    {
                      type: "radiogroup",
                      name: "last12Falls",
                      title: "12. Have you had any falls in the last 12 months?",
                      renderAs: "prettycheckbox",
                      score: "1,2,66,77",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not Collected",
                        },
                      ],
                    },
                    {
                      type: "text",
                      name: "Comment618",
                      visibleIf: "{last12Falls} = 1",
                      title: "Number of  Falls",
                      description: "Whole Numbers Only",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{last12Falls} = 1",
                      inputType: "number",
                      step: 1,
                    },
                    {
                      type: "checkbox",
                      name: "Behavioralhealth",
                      title: "13. Behavioral health",
                      clearIfInvisible: "onHidden",
                      choices: [
                        {
                          value: "Item 4",
                          text: "Anxiety",
                        },
                        {
                          value: "Item 5",
                          text: "Depressions",
                        },
                        {
                          value: "Item 6",
                          text: "ETOH/drug abuse",
                        },
                        {
                          value: "Item 7",
                          text: "Schizophrenia",
                        },
                        {
                          value: "Item 8",
                          text: "Tobacco",
                        },
                        {
                          value: "Item 9",
                          text: "Not provided",
                        },
                        {
                          value: "Item 10",
                          text: "Other",
                        },
                      ],
                    },
                    {
                      type: "panel",
                      name: "PHQ-2",
                      title: "14. Patient Health Questionnaire-2 (PHQ-2)",
                      state: "expanded",
                      elements: [
                        {
                          type: "panel",
                          name: "beenbotheredbyanyofthefollowingproblem",
                          title:
                            "Over the last two weeks, how often have you been bothered by any of the following problems?",
                          elements: [
                            {
                              type: "radiogroup",
                              name: "Little interest or pleasure in doing things",
                              title: "Little interest or pleasure in doing things",
                              renderAs: "prettycheckbox",
                              choices: ["Not at all", "Several days", "More than one half of days", "Nearly every day"],
                            },
                            {
                              type: "boolean",
                              name: "priority211",
                              startWithNewLine: false,
                              title: "Priority",
                              titleLocation: "hidden",
                              defaultValue: "false",
                              renderAs: "prettycheckbox",
                            },
                            {
                              type: "comment",
                              name: "Comment74",
                              title: "Comment",
                              maxLength: 5000,
                            },
                            {
                              type: "radiogroup",
                              name: "Feeldownordepressed",
                              title: "Feel down or depressed",
                              renderAs: "prettycheckbox",
                              choices: ["Not at all", "Several days", "More than one half of days", "Nearly every day"],
                            },
                            {
                              type: "boolean",
                              name: "priority213",
                              startWithNewLine: false,
                              title: "Priority",
                              titleLocation: "hidden",
                              defaultValue: "false",
                              renderAs: "prettycheckbox",
                            },
                            {
                              type: "comment",
                              name: "Comment75",
                              title: "Comment",
                              maxLength: 5000,
                            },
                          ],
                        },
                      ],
                    },
                  ],
                },
                {
                  type: "panel",
                  name: "WellRx",
                  title: "WellRx",
                  state: "collapsed",
                  innerIndent: 1,
                  showQuestionNumbers: "off",
                  elements: [
                    {
                      type: "radiogroup",
                      name: "Inthelast2monthsdidyouorothersyoulivewitheatsmallermealsorskipmealsbecauseyoudidnthavemoneyforfood3",
                      title:
                        "1. Within the last 12 months have you worried that your food would run out before you were able to purchase more?",
                      choices: ["Yes", "No"],
                    },
                    {
                      type: "comment",
                      name: "Comment175",
                      title: "Comment",
                      maxLength: 5000,
                    },
                    {
                      type: "radiogroup",
                      name: "foodAssistance",
                      title: "2. Do you currently receive any food assistance?",
                      choices: ["Yes", "No"],
                    },
                    {
                      type: "radiogroup",
                      name: "SNAP",
                      visibleIf: "{foodAssistance} = 'Yes'",
                      title: "Food Assistance",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{foodAssistance} = 'Yes'",
                      score: "1,0,0",
                      choices: [
                        {
                          value: "snap",
                          text: "SNAP",
                        },
                        {
                          value: "foodPantry",
                          text: "Food Pantry",
                        },
                        {
                          value: "mobileMeals",
                          text: "Mobile Meals",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "TNAF",
                      title: "3. Do you currently receive TANF benefits?",
                      score: "1,2,66,77,88,99",
                      choices: [
                        {
                          value: "1",
                          text: "Yes",
                        },
                        {
                          value: "2",
                          text: "No",
                        },
                        {
                          value: "66",
                          text: "N/A",
                        },
                        {
                          value: "77",
                          text: "Information not collected",
                        },
                        {
                          value: "88",
                          text: "Individual refused",
                        },
                        {
                          value: "99",
                          text: "Individual does not know",
                        },
                      ],
                    },
                    {
                      type: "comment",
                      name: "Comment1810",
                      title: "Comment",
                      maxLength: 5000,
                    },
                    {
                      type: "radiogroup",
                      name: "Doyouhavetroublepayingforyourutilitiesgaselectricphone11",
                      title:
                        "4. In the last 12 months, have you received a disconnection notice from the electric, heating, or water company?",
                      renderAs: "prettycheckbox",
                      choices: ["Yes", "No"],
                    },
                    {
                      type: "radiogroup",
                      name: "question19",
                      visibleIf: "{Doyouhavetroublepayingforyourutilitiesgaselectricphone11} = 'Yes'",
                      title: "4a. Would you like assistance with local utility bill resources?",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{Doyouhavetroublepayingforyourutilitiesgaselectricphone11} = 'Yes'",
                      renderAs: "prettycheckbox",
                      choices: ["Yes", "No"],
                    },
                    {
                      type: "radiogroup",
                      name: "Doyouhavetroublefindingorpayingforaride16",
                      title:
                        "5. In the past 12 months, has the lack of transportation kept you from medical appointments or any activities of daily living?",
                      renderAs: "prettycheckbox",
                      choices: ["Yes", "No"],
                    },
                    {
                      type: "radiogroup",
                      name: "Wouldyoulikeustoprovideyouwithtransportationassistanceorresourcesfortransportation18",
                      visibleIf: "{Doyouhavetroublefindingorpayingforaride16} = 'Yes'",
                      title: "5a. Would you like assistance with transportation resources?",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{Doyouhavetroublefindingorpayingforaride16} = 'Yes'",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "liveAlone",
                      title: "6. Do you currently live alone?",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "whoElse",
                      visibleIf: "{liveAlone} = 'Item 2'",
                      title: "6a. Who else resides with you?",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{liveAlone} = 'Item 2'",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Live with family member",
                        },
                        {
                          value: "Item 2",
                          text: "Live with friend or relative",
                        },
                        {
                          value: "Item 3",
                          text: "Live with spouse",
                        },
                        {
                          value: "Item 4",
                          text: "Other",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "question24",
                      visibleIf: "{liveAlone} = 'Item 1'",
                      title: "6b. Do you ever feel threatened or abused?",
                      requiredIf: "{liveAlone} = 'Item 1'",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "alcohol",
                      visibleIf: "{liveAlone} = 'Item 1'",
                      title: "6c. Are you ever concerned of someone in the home abusing drugs or alcohol?",
                      requiredIf: "{liveAlone} = 'Item 1'",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "caregiving",
                      title: "7. Are you currently caregiving for anyone in your home?",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "caregivingForSchool",
                      visibleIf: "{caregiving} = 'Item 1'",
                      title: "7a. Are the people you are caregiving for school age or younger?",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{caregiving} = 'Item 1'",
                      choices: [
                        {
                          value: "Item",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                      showOtherItem: true,
                      otherText: "Other",
                    },
                    {
                      type: "radiogroup",
                      name: "situation",
                      visibleIf: "{caregiving} = 'Item 1'",
                      title:
                        "7b. Do you need daycare, adult care, or a better care situation for the people you are caring for?",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{caregiving} = 'Item 1'",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "panel",
                      name: "UpstreamRiskScreeningTool57",
                      title: "8. Social Supports Screening Tool",
                      state: "collapsed",
                      innerIndent: 1,
                      elements: [
                        {
                          type: "matrix",
                          name: "UpstreamRiskScreening58",
                          title: "Social Supports Screening",
                          columns: ["0", "1-2", "3-4", "Almost daily", "Multiple times a day"],
                          rows: [
                            "In a typical week, how many times do you talk on the telephone with family, friends or neighbors, # per week?",
                            "How often do you get together with friends or relatives, # per week?",
                            "How often do you attend religious or faith based services, # per month?",
                            "How often do you attend meetings of the clubs or organizations you belong too, # per month?",
                          ],
                        },
                        {
                          type: "comment",
                          name: "Comment2860",
                          title: "Comment",
                          maxLength: 5000,
                        },
                      ],
                    },
                    {
                      type: "checkbox",
                      name: "livingConditions",
                      title: "9. What are the current living conditions?",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Clear pathways",
                        },
                        {
                          value: "Item 2",
                          text: "Cluttered",
                        },
                        {
                          value: "Item 3",
                          text: "Cleanliness rating",
                        },
                      ],
                      otherText: "",
                      showSelectAllItem: true,
                    },
                    {
                      type: "radiogroup",
                      name: "MayIaskyouaboutyourFaithbackground62",
                      title: "10. May I ask you about your Faith ?",
                      renderAs: "prettycheckbox",
                      choices: ["Yes", "No"],
                    },
                    {
                      type: "radiogroup",
                      name: "faithpreferenc",
                      visibleIf: "{MayIaskyouaboutyourFaithbackground62} = Yes",
                      title: "10a. Do you have a spiritual or faith preference?",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{MayIaskyouaboutyourFaithbackground62} = Yes",
                      renderAs: "prettycheckbox",
                      choices: ["Yes", "No"],
                    },
                    {
                      type: "radiogroup",
                      name: "Doesyourspiritualityimpactthehealthdecisionsyoumake68",
                      visibleIf: "{faithpreferenc} = 'Yes'",
                      title:
                        "10b. Are there resources in your faith community that you would like for me to help mobilize on your behalf?",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{faithpreferenc} = 'Yes'",
                      renderAs: "prettycheckbox",
                      choices: ["Yes", "No"],
                    },
                  ],
                },
                {
                  type: "panel",
                  name: "panel222",
                  title: "ADL’s / IADLs",
                  state: "collapsed",
                  innerIndent: 1,
                  showQuestionNumbers: "off",
                  elements: [
                    {
                      type: "panel",
                      name: "question155",
                      title:
                        "1. Do you currently need assistance with daily activities such as bathing, dressing, grooming?",
                      elements: [
                        {
                          type: "radiogroup",
                          name: "ADLYESORNO",
                          title: "Activities of Daily Living (ADL’s)",
                          choices: [
                            {
                              value: "Item 1",
                              text: "Yes",
                            },
                            {
                              value: "Item 3",
                              text: "No",
                            },
                          ],
                        },
                        {
                          type: "checkbox",
                          name: "ADL",
                          visibleIf: "{ADLYESORNO} = 'Item 1'",
                          startWithNewLine: false,
                          title: "Activities of Daily Living (ADL’s)",
                          titleLocation: "hidden",
                          clearIfInvisible: "onHidden",
                          requiredIf: "{ADLYESORNO} = 'Item 1'",
                          score: "1,2,3,4,5,6,7,8,9,54",
                          choices: [
                            {
                              value: "1",
                              text: "Toileting hygiene",
                            },
                            {
                              value: "2",
                              text: "Feeding or eating",
                            },
                            {
                              value: "3",
                              text: "Dressing upper body",
                            },
                            {
                              value: "4",
                              text: "Dressing lower body",
                            },
                            {
                              value: "5",
                              text: "Grooming",
                            },
                            {
                              value: "6",
                              text: "Bathing",
                            },
                            {
                              value: "7",
                              text: "Toilet transferring",
                            },
                            {
                              value: "8",
                              text: "Transferring",
                            },
                            {
                              value: "9",
                              text: "Ambulation/locomotion",
                            },
                            {
                              value: "54",
                              text: "Individual did not demonstrate need for assistance",
                            },
                          ],
                        },
                        {
                          type: "radiogroup",
                          name: "IADL",
                          title: "Instrumental Activities of Daily Living (IADLs)",
                          choices: [
                            {
                              value: "0",
                              text: "Yes",
                            },
                            {
                              value: "56",
                              text: "No",
                            },
                          ],
                        },
                        {
                          type: "checkbox",
                          name: "IADLs",
                          visibleIf: "{IADL} = '0'",
                          startWithNewLine: false,
                          title: "Instrumental Activities of Daily Living (IADLs)",
                          titleLocation: "hidden",
                          clearIfInvisible: "onHidden",
                          requiredIf: "{IADL} = '0'",
                          score: "1,2,3,4,5,6,7,8,9",
                          choices: [
                            {
                              value: "1",
                              text: "Toileting hygiene",
                            },
                            {
                              value: "2",
                              text: "Feeding or eating",
                            },
                            {
                              value: "3",
                              text: "Dressing upper body",
                            },
                            {
                              value: "4",
                              text: "Dressing lower body",
                            },
                            {
                              value: "5",
                              text: "Grooming",
                            },
                            {
                              value: "6",
                              text: "Bathing",
                            },
                            {
                              value: "7",
                              text: "Toilet transferring",
                            },
                            {
                              value: "8",
                              text: "Transferring",
                            },
                            {
                              value: "9",
                              text: "Ambulation/locomotion",
                            },
                          ],
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "servicesInPlace",
                      title: "2. Do you currently have any services in place?",
                      score: "0,56",
                      choices: [
                        {
                          value: "0",
                          text: "Yes",
                        },
                        {
                          value: "56",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "checkbox",
                      name: "IAPAS",
                      visibleIf: "{servicesInPlace} = '0'",
                      startWithNewLine: false,
                      title: "Instrumental Adult Personal Assistance Service",
                      titleLocation: "hidden",
                      clearIfInvisible: "onHidden",
                      score: "1,2,3,4,5,6,7",
                      requiredIf: "{servicesInPlace} = '0'",
                      choices: [
                        {
                          value: "1",
                          text: "Telephone/Communications",
                        },
                        {
                          value: "2",
                          text: "Transportation/Traveling",
                        },
                        {
                          value: "3",
                          text: "Shopping",
                        },
                        {
                          value: "4",
                          text: "Preparing meals",
                        },
                        {
                          value: "5",
                          text: "Housework",
                        },
                        {
                          value: "6",
                          text: "Managing medications",
                        },
                        {
                          value: "7",
                          text: "Money management",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "question98",
                      visibleIf: "{servicesInPlace} = 56",
                      title: " Would you like us to link you with any services?",
                      clearIfInvisible: "onHidden",
                      choices: [
                        {
                          value: "Item 1",
                          text: "Yes",
                        },
                        {
                          value: "Item 2",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "checkbox",
                      name: "adaptive",
                      title: "3. Do you have or need any adaptive assistive devices?",
                      choices: [
                        "Adaptive Eating Device",
                        "Bed Rail",
                        "Bedside Commode",
                        "Cane",
                        "Grab Bars",
                        "Hand-held Shower",
                        "Raised Toilet Seat",
                        "Reach Stick",
                        "Rollator",
                        "Scooter",
                        "Shower Chair",
                        "Shower Transfer Chair",
                        "Slideboard",
                        "Walker",
                        "Wheelchair",
                      ],
                    },
                    {
                      type: "checkbox",
                      name: "mobility",
                      title: "4. Do you currently use any mobility devices?",
                      choices: [
                        {
                          value: "Adaptive Eating Device",
                          text: "Cane",
                        },
                        {
                          value: "Bed Rail",
                          text: "Rollator",
                        },
                        {
                          value: "Bedside Commode",
                          text: "Scooter",
                        },
                        {
                          value: "Cane",
                          text: "Walker",
                        },
                        {
                          value: "Grab Bars",
                          text: "Wheelchair",
                        },
                      ],
                    },
                    {
                      type: "radiogroup",
                      name: "InstrumentalActivitiesOfDailyLiving",
                      title:
                        "5. Do you currently need assistance with Instrumental Activities of Daily living such as shopping, meal preparation, money management etc. ?",
                      score: "0,54",
                      choices: [
                        {
                          value: "0",
                          text: "Yes",
                        },
                        {
                          value: "54",
                          text: "No",
                        },
                      ],
                    },
                    {
                      type: "checkbox",
                      name: "IADLCount",
                      visibleIf: "{InstrumentalActivitiesOfDailyLiving} = '0'",
                      startWithNewLine: false,
                      title: "Instrumental Activities of Daily Living (IADL)",
                      titleLocation: "hidden",
                      clearIfInvisible: "onHidden",
                      requiredIf: "{InstrumentalActivitiesOfDailyLiving} = '0'",
                      score: "1,2,3,4,5,6,7",
                      choices: [
                        {
                          value: "1",
                          text: "Telephone",
                        },
                        {
                          value: "2",
                          text: "Traveling",
                        },
                        {
                          value: "3",
                          text: "Shopping",
                        },
                        {
                          value: "4",
                          text: "Preparing meals",
                        },
                        {
                          value: "5",
                          text: "Housework",
                        },
                        {
                          value: "6",
                          text: "Medications",
                        },
                        {
                          value: "7",
                          text: "Money management",
                        },
                      ],
                      separateSpecialChoices: true,
                    },
                  ],
                },
              ],
              title: "Assessment",
              state: "collapsed",
              innerIndent: 1,
            },
          ],
        },
      ],
    }),
  );
  form.append("status", "DRAFT");

  const headers = {
    accept: "*/*",
    "accept-language": "zh-CN,zh;q=0.9",
    Authorization:
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNDkxNTkiLCJpYXQiOjE3NTEzNTU2OTQsImlzcyI6Imh0dHBzOi8vc3RnLnNpbXBseWNvbm5lY3QubWUiLCJyc2NpIjpbXSwiZXhwIjoxNzUxNDQyMDk0fQ.SWOwZv5i8sWB_ZZH4KHT3PqFehKspWYCd0E3Wu6VfCKvTZn-v6kr60KlBZZ6ANQad64BF_9K_CG7dHepeE1Eeg",
    ...form.getHeaders(),
    "Content-type": "multipart/form-data",
    "sec-ch-ua": '"Google Chrome";v="137", "Chromium";v="137", "Not/A)Brand";v="24"',
    "sec-ch-ua-mobile": "?0",
    "sec-ch-ua-platform": '"macOS"',
    "sec-fetch-dest": "empty",
    "sec-fetch-mode": "cors",
    "sec-fetch-site": "cross-side",
    "sec-fetch-storage-access": "active",
    timezoneoffset: "-480",
    Referer: "http://localhost:3000/",
    "Referrer-Policy": "strict-origin-when-cross-origin",
  };

  try {
    const response = await axios.put(url, form, { headers });
    console.log("Response:", response.data);
  } catch (error) {
    console.error("Error details:", {
      message: error.message,
      stack: error.stack,
      config: error.config,
      code: error.code,
      response: error.response ? error.response.data : "No response data",
    });
  }
}

makeHttpRequest();
