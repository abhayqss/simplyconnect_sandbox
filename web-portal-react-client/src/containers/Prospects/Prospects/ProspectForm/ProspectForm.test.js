import React from "react";

import { where, findWhere } from "underscore";

import { trim } from "lodash";

import { rest } from "msw";
import { setupServer } from "msw/node";

import { waitFor, render, fireEvent } from "lib/test-utils";

import Response from "lib/mock/server/Response";

import { formatSSN } from "lib/utils/Utils";

import { noop, isFunction } from "lib/utils/FuncUtils";

import { format, formats } from "lib/utils/DateUtils";

import {
  Race,
  User,
  State,
  Gender,
  Network2,
  Community,
  SystemRole,
  Organization,
  ClientDetails,
  MaritalStatus,
  IsVeteranCases,
  ContactDetails,
  ProspectDetails,
  CommunityDetails,
  OrganizationDetails,
  RelatedPartyRelationshipType,
} from "lib/mock/db/DB";

import configureStore from "redux/configureStore";

import ProspectForm from "./ProspectForm";

const BASE_URL = "https://dev.simplyconnect.me/web-portal-mock-backend";

const server = setupServer(
  rest.get(`${BASE_URL}/prospects/:prospectId`, (req, res, ctx) => {
    return res(ctx.json(Response.success(findWhere(ProspectDetails, { id: +req.params.prospectId }))));
  }),
  rest.get(`${BASE_URL}/contacts/:contactId`, (req, res, ctx) => {
    return res(ctx.json(Response.success(findWhere(ContactDetails, { id: +req.params.contactId }))));
  }),
  rest.get(`${BASE_URL}/authorized-directory/organizations`, (req, res, ctx) => {
    return res(ctx.json(Response.success(Organization)));
  }),
  rest.get(`${BASE_URL}/authorized-directory/communities`, (req, res, ctx) => {
    return res(
      ctx.json(Response.success(where(Community, { organizationId: +req.url.searchParams.get("organizationId") }))),
    );
  }),
  rest.get(`${BASE_URL}/directory/genders`, (req, res, ctx) => {
    return res(ctx.json(Response.success(Gender)));
  }),
  rest.get(`${BASE_URL}/directory/states`, (req, res, ctx) => {
    return res(ctx.json(Response.success(State)));
  }),
  rest.get(`${BASE_URL}/authorized-directory/editable-system-roles`, (req, res, ctx) => {
    return res(ctx.json(Response.success(SystemRole)));
  }),
  rest.get(`${BASE_URL}/directory/marital-status`, (req, res, ctx) => {
    return res(ctx.json(Response.success(MaritalStatus)));
  }),
  rest.get(`${BASE_URL}/directory/races`, (req, res, ctx) => {
    return res(ctx.json(Response.success(Race)));
  }),
  rest.get(`${BASE_URL}/authorized-directory/insurance/networks`, (req, res, ctx) => {
    return res(ctx.json(Response.success(Network2)));
  }),
  rest.get(`${BASE_URL}/directory/related-party-relationships`, (req, res, ctx) => {
    return res(ctx.json(Response.success(RelatedPartyRelationshipType)));
  }),
);

function renderAndConfigure({ prospectId, communityId, organizationId = 3, userOrganizationId = 3 }) {
  const { store, ...config } = render(
    <ProspectForm prospectId={prospectId} communityId={communityId} organizationId={organizationId} />,
  );

  const user = findWhere(User, { organizationId: userOrganizationId });
  store.dispatch({ type: "LOGIN_SUCCESS", payload: user });

  return { store, ...config };
}

function testFieldToBeVisible(title, name, action = noop) {
  it(title, () => {
    let { getByTestId } = render(<ProspectForm />);

    if (isFunction(action)) action({ getByTestId });

    const node = getByTestId(`${name}_field`);
    expect(node).toBeInTheDocument();
    expect(node).toBeVisible();
  });
}

function testFieldToBeRequired(title, name, action = noop, isAsync = false) {
  it(title, async function () {
    let { getByTestId, findByTestId } = render(<ProspectForm />);

    if (isFunction(action)) {
      await new Promise((resolve) => {
        resolve(
          action({
            getByTestId,
            findByTestId,
          }),
        );
      });
    }

    if (isAsync) {
      await waitFor(() => {
        const node = getByTestId(`${name}_field-label`);
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });
    } else {
      const node = getByTestId(`${name}_field-label`);
      expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
    }
  });
}

describe("<ProspectForm>:", function () {
  beforeAll(() => {
    server.listen();
  });

  describe("All fields are visible:", function () {
    describe("Demographics & Insurance:", function () {
      testFieldToBeVisible("First Name", "firstName");
      testFieldToBeVisible("Last Name", "lastName");
      testFieldToBeVisible("Middle Name", "middleName");
      testFieldToBeVisible("Date Of Birth", "birthDate");
      testFieldToBeVisible("Gender", "genderId");
      testFieldToBeVisible("Marital Status", "maritalStatusId");
      testFieldToBeVisible("Race", "raceId");
      testFieldToBeVisible("Social Security Number", "ssn");
      testFieldToBeVisible("Prospect doesn't have SSN", "hasNoSsn");
      testFieldToBeVisible("Veteran?", "veteranStatusName");
      testFieldToBeVisible("Network", "insuranceNetworkId");
      testFieldToBeVisible("Plan", "insurancePaymentPlan");
      testFieldToBeVisible("Photo", "avatar");
    });

    describe("Community:", function () {
      testFieldToBeVisible("Organization", "organizationId");
      testFieldToBeVisible("Community", "communityId");
    });

    describe("Contact Information:", function () {
      testFieldToBeVisible("Street", "address.street");
      testFieldToBeVisible("City", "address.city");
      testFieldToBeVisible("State", "address.stateId");
      testFieldToBeVisible("Zip Code", "address.zip");
      testFieldToBeVisible("Cell Phone", "cellPhone");
      testFieldToBeVisible("Email", "email");
    });

    describe("Primary Contact", function () {
      testFieldToBeVisible("Primary contact", "primaryContact.typeName");
      testFieldToBeVisible("Primary notification method", "primaryContact.notificationMethodName");
    });

    describe("Move-In Information:", function () {
      testFieldToBeVisible("Move-In Date", "moveInDate");
      testFieldToBeVisible("Rental Agreement Signed Date", "rentalAgreementSignedDate");
      testFieldToBeVisible("Assessment Date", "assessmentDate");
      testFieldToBeVisible("Referral Source", "referralSource");
      testFieldToBeVisible("Notes", "notes");
    });

    describe("Related Party:", function () {
      testFieldToBeVisible("First Name", "relatedParty.firstName");
      testFieldToBeVisible("Last Name", "relatedParty.lastName");
      testFieldToBeVisible("Relationship", "relatedParty.relationshipTypeName");
      testFieldToBeVisible("Street", "relatedParty.address.street");
      testFieldToBeVisible("City", "relatedParty.address.city");
      testFieldToBeVisible("State", "relatedParty.address.stateId");
      testFieldToBeVisible("Zip Code", "relatedParty.address.zip");
      testFieldToBeVisible("Cell Phone", "relatedParty.cellPhone");
      testFieldToBeVisible("Email", "relatedParty.email");
    });

    describe("Second Occupant:", function () {
      function test2ndOccupantFieldToBeVisible(name, title) {
        testFieldToBeVisible(name, title, ({ getByTestId }) => {
          fireEvent.click(getByTestId("hasSecondOccupant_field-label"));
        });
      }

      describe("Demographics & Insurance", function () {
        test2ndOccupantFieldToBeVisible("Related Party is 2nd Occupant", "relatedPartyIs2ndOccupant");
        test2ndOccupantFieldToBeVisible("First Name", "secondOccupant.firstName");
        test2ndOccupantFieldToBeVisible("Last Name", "secondOccupant.lastName");
        test2ndOccupantFieldToBeVisible("Middle Name", "secondOccupant.middleName");
        test2ndOccupantFieldToBeVisible("Date Of Birth", "secondOccupant.birthDate");
        test2ndOccupantFieldToBeVisible("Gender", "secondOccupant.genderId");
        test2ndOccupantFieldToBeVisible("Marital Status", "secondOccupant.maritalStatusId");
        test2ndOccupantFieldToBeVisible("Race", "secondOccupant.raceId");
        test2ndOccupantFieldToBeVisible("Social Security Number", "secondOccupant.ssn");
        test2ndOccupantFieldToBeVisible("2nd occupant doesn't have SSN", "secondOccupant.hasNoSsn");
        test2ndOccupantFieldToBeVisible("Veteran?", "secondOccupant.veteranStatusName");
        test2ndOccupantFieldToBeVisible("Network", "secondOccupant.insuranceNetworkId");
        test2ndOccupantFieldToBeVisible("Plan", "secondOccupant.insurancePaymentPlan");
        test2ndOccupantFieldToBeVisible("Photo", "secondOccupant.avatar");
      });

      describe("Contact Information:", function () {
        test2ndOccupantFieldToBeVisible("Use prospect address", "secondOccupant.hasProspectAddress");
        test2ndOccupantFieldToBeVisible("Street", "secondOccupant.address.street");
        test2ndOccupantFieldToBeVisible("City", "secondOccupant.address.city");
        test2ndOccupantFieldToBeVisible("State", "secondOccupant.address.stateId");
        test2ndOccupantFieldToBeVisible("Zip Code", "secondOccupant.address.zip");
        test2ndOccupantFieldToBeVisible("Cell Phone", "secondOccupant.cellPhone");
        test2ndOccupantFieldToBeVisible("Email", "secondOccupant.email");
      });
    });
  });

  describe("Fields marked as required:", function () {
    describe("Demographics & Insurance:", function () {
      testFieldToBeRequired("First Name", "firstName");
      testFieldToBeRequired("Last Name", "lastName");
      testFieldToBeRequired("Date Of Birth", "birthDate");
      testFieldToBeRequired("Gender", "genderId");
    });

    describe("Community:", function () {
      testFieldToBeRequired("Organization", "organizationId");
      testFieldToBeRequired("Community", "communityId");
    });

    describe("Contact Information:", function () {
      testFieldToBeRequired("Street", "address.street");
      testFieldToBeRequired("City", "address.city");
      testFieldToBeRequired("State", "address.stateId");
      testFieldToBeRequired("Zip Code", "address.zip");
      testFieldToBeRequired("Cell Phone", "cellPhone");
      testFieldToBeRequired("Email", "email");
    });

    describe("Primary Contact", function () {
      testFieldToBeRequired("Primary contact", "primaryContact.typeName");
      testFieldToBeRequired("Primary notification method", "primaryContact.notificationMethodName");
    });

    describe("Related Party", function () {
      function testRelatedPartyFieldsToBeRequired(action, isAsync) {
        testFieldToBeRequired("First Name", "relatedParty.firstName", action, isAsync);
        testFieldToBeRequired("Last Name", "relatedParty.lastName", action, isAsync);
        testFieldToBeRequired("Relationship", "relatedParty.relationshipTypeName", action, isAsync);
        testFieldToBeRequired("Street", "relatedParty.address.street", action, isAsync);
        testFieldToBeRequired("City", "relatedParty.address.city", action, isAsync);
        testFieldToBeRequired("State", "relatedParty.address.stateId", action, isAsync);
        testFieldToBeRequired("Zip Code", "relatedParty.address.zip", action, isAsync);
        testFieldToBeRequired("Cell Phone", "relatedParty.cellPhone", action, isAsync);
        testFieldToBeRequired("Email", "relatedParty.email", action, isAsync);
      }

      describe('Fields are required when "First Name" field is filled:', function () {
        function fillFirstNameField({ getByTestId }) {
          fireEvent.change(getByTestId("relatedParty.firstName_field-input"), { target: { value: "qwerty" } });
        }

        testRelatedPartyFieldsToBeRequired(fillFirstNameField);
      });

      describe('Fields are required when "Last Name" field is filled:', function () {
        function fillLastNameField({ getByTestId }) {
          fireEvent.change(getByTestId("relatedParty.lastName_field-input"), { target: { value: "qwerty" } });
        }

        testRelatedPartyFieldsToBeRequired(fillLastNameField);
      });

      describe('Fields are required when "Relationship" field is filled:', function () {
        async function fillRelationshipField({ findByTestId }) {
          const relationshipType = RelatedPartyRelationshipType[0];

          const optionNode = await findByTestId(`relatedParty.relationshipTypeName_${relationshipType.name}-option`);

          fireEvent.click(optionNode);
        }

        testRelatedPartyFieldsToBeRequired(fillRelationshipField, true);
      });

      describe('Fields are required when "Street" field is filled:', function () {
        function fillStreetField({ getByTestId }) {
          fireEvent.change(getByTestId("relatedParty.address.street_field-input"), { target: { value: "qwerty" } });
        }

        testRelatedPartyFieldsToBeRequired(fillStreetField);
      });

      describe('Fields are required when "City" field is filled:', function () {
        function fillCityField({ getByTestId }) {
          fireEvent.change(getByTestId("relatedParty.address.city_field-input"), { target: { value: "qwerty" } });
        }

        testRelatedPartyFieldsToBeRequired(fillCityField);
      });

      describe('Fields are required when "State" field is filled:', function () {
        async function fillStateField({ findByTestId }) {
          const state = findWhere(State, { id: 1 });

          const optionNode = await findByTestId(`relatedParty.address.stateId_${state.id}-option`);

          fireEvent.click(optionNode);
        }

        testRelatedPartyFieldsToBeRequired(fillStateField, true);
      });

      describe('Fields are required when "Zip" field is filled:', function () {
        function fillZipField({ getByTestId }) {
          fireEvent.change(getByTestId("relatedParty.address.zip_field-input"), { target: { value: "12345" } });
        }

        testRelatedPartyFieldsToBeRequired(fillZipField);
      });

      describe('Fields are required when "Cell Phone" field is filled:', function () {
        function fillCellPhoneField({ getByTestId }) {
          fireEvent.change(getByTestId("relatedParty.cellPhone_field-input"), { target: { value: "+5345432345" } });
        }

        testRelatedPartyFieldsToBeRequired(fillCellPhoneField);
      });

      describe('Fields are required when "Email" field is filled:', function () {
        function fillEmailField({ getByTestId }) {
          fireEvent.change(getByTestId("relatedParty.email_field-input"), { target: { value: "qwerty@mail.com" } });
        }

        testRelatedPartyFieldsToBeRequired(fillEmailField);
      });
    });

    describe("Second Occupant:", function () {
      function test2ndOccupantFieldToBeRequired(name, title) {
        testFieldToBeRequired(name, title, ({ getByTestId }) => {
          fireEvent.click(getByTestId("hasSecondOccupant_field-label"));
        });
      }

      describe("Demographics & Insurance", function () {
        test2ndOccupantFieldToBeRequired("First Name", "secondOccupant.firstName");
        test2ndOccupantFieldToBeRequired("Last Name", "secondOccupant.lastName");
        test2ndOccupantFieldToBeRequired("Middle Name", "secondOccupant.middleName");
        test2ndOccupantFieldToBeRequired("Date Of Birth", "secondOccupant.birthDate");
        test2ndOccupantFieldToBeRequired("Gender", "secondOccupant.genderId");
        test2ndOccupantFieldToBeRequired("Social Security Number", "secondOccupant.ssn");
      });

      describe("Contact Information:", function () {
        test2ndOccupantFieldToBeRequired("Street", "secondOccupant.address.street");
        test2ndOccupantFieldToBeRequired("City", "secondOccupant.address.city");
        test2ndOccupantFieldToBeRequired("State", "secondOccupant.address.stateId");
        test2ndOccupantFieldToBeRequired("Zip Code", "secondOccupant.address.zip");
        test2ndOccupantFieldToBeRequired("Cell Phone", "secondOccupant.cellPhone");
        test2ndOccupantFieldToBeRequired("Email", "secondOccupant.email");
      });
    });
  });

  describe("Business Logic:", function () {
    describe("Demographics & Insurance", function () {
      describe('"Prospect doesn\'t have SSN" field checked:', function () {
        function renderAndPrepare() {
          let { getByTestId } = render(<ProspectForm />);

          fireEvent.change(getByTestId("ssn_field-input"), { target: { value: "395264732" } });

          fireEvent.click(getByTestId("hasNoSsn_field-label"));

          return { getByTestId };
        }

        it('"Social Security Number" field is disabled', function () {
          const { getByTestId } = renderAndPrepare();
          expect(getByTestId("ssn_field")).toHaveClass("TextField_disabled");
        });

        it('"Social Security Number" field is clear', function () {
          const { getByTestId } = renderAndPrepare();
          expect(getByTestId("ssn_field-input")).toHaveValue("");
        });

        it('"Social Security Number" field is not required', function () {
          const { getByTestId } = renderAndPrepare();
          expect(String(getByTestId("ssn_field-label").innerHTML).endsWith("*")).toBeFalsy();
        });
      });
    });

    describe("Second Occupant:", function () {
      describe("Demographics & Insurance", function () {
        describe('"2nd occupant doesn\'t have SSN" field checked:', function () {
          function renderAndPrepare() {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.change(getByTestId("secondOccupant.ssn_field-input"), { target: { value: "395264732" } });

            fireEvent.click(getByTestId("secondOccupant.hasNoSsn_field-label"));

            return { getByTestId };
          }

          it('"Social Security Number" field is disabled', function () {
            const { getByTestId } = renderAndPrepare();
            expect(getByTestId("secondOccupant.ssn_field")).toHaveClass("TextField_disabled");
          });

          it('"Social Security Number" field is clear', function () {
            const { getByTestId } = renderAndPrepare();
            expect(getByTestId("secondOccupant.ssn_field-input")).toHaveValue("");
          });

          it('"Social Security Number" field is not required', function () {
            const { getByTestId } = renderAndPrepare();
            expect(String(getByTestId("secondOccupant.ssn_field-label").innerHTML).endsWith("*")).toBeFalsy();
          });
        });
      });

      describe("Contact Information:", function () {
        describe("Use Prospect address:", function () {
          describe("Prospect address isn't specified:", function () {
            it('"Use prospect address" field is disabled', () => {
              let { getByTestId } = render(<ProspectForm />);

              fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

              const node = getByTestId("secondOccupant.hasProspectAddress_field");
              expect(node).toHaveClass("CheckboxField_disabled");
            });
          });

          describe("Prospect address is specified:", function () {
            describe('"Use prospect address" field is not disabled, when:', function () {
              it("Prospect Street is specified", () => {
                let { getByTestId } = render(<ProspectForm />);

                fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

                fireEvent.change(getByTestId("address.street_field-input"), {
                  target: { value: "Test Street" },
                });

                const node = getByTestId("secondOccupant.hasProspectAddress_field");
                expect(node).not.toHaveClass("CheckboxField_disabled");
              });

              it("Prospect City is specified", () => {
                let { getByTestId } = render(<ProspectForm />);

                fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

                fireEvent.change(getByTestId("address.city_field-input"), {
                  target: { value: "Test City" },
                });

                const node = getByTestId("secondOccupant.hasProspectAddress_field");
                expect(node).not.toHaveClass("CheckboxField_disabled");
              });

              it("Prospect State is specified", async () => {
                let { getByTestId } = render(<ProspectForm />);

                const stateId = 1;
                fireEvent.click(getByTestId("address.stateId_toggle"));
                fireEvent.click(getByTestId("address.stateId_options").querySelector(`[data-value='${stateId}']`));

                fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

                const node = getByTestId("secondOccupant.hasProspectAddress_field");
                expect(node).not.toHaveClass("CheckboxField_disabled");
              });

              it("Prospect Zip Code is specified", () => {
                let { getByTestId } = render(<ProspectForm />);

                fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

                fireEvent.change(getByTestId("address.zip_field-input"), {
                  target: { value: "12345" },
                });

                const node = getByTestId("secondOccupant.hasProspectAddress_field");
                expect(node).not.toHaveClass("CheckboxField_disabled");
              });
            });

            describe("Prospect address is applied:", function () {
              const stateId = 1;
              const state = findWhere(State, { id: stateId });

              const ADDRESS = {
                STREET: "Prospect Test Street",
                CITY: "Prospect Test City",
                STATE_ID: stateId,
                STATE_TITLE: state.label,
                ZIP: "12345",
              };

              function renderAndPrepare() {
                let { getByTestId } = render(<ProspectForm />);

                fireEvent.change(getByTestId("address.street_field-input"), {
                  target: { value: ADDRESS.STREET },
                });

                fireEvent.change(getByTestId("address.city_field-input"), {
                  target: { value: ADDRESS.CITY },
                });

                fireEvent.change(getByTestId("address.zip_field-input"), {
                  target: { value: ADDRESS.ZIP },
                });

                fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

                return { getByTestId };
              }

              it("Street", function () {
                const { getByTestId } = renderAndPrepare();

                fireEvent.click(getByTestId("secondOccupant.hasProspectAddress_field-label"));

                expect(getByTestId(`secondOccupant.address.street_field-input`)).toHaveValue(ADDRESS.STREET);
              });

              it("City", function () {
                const { getByTestId } = renderAndPrepare();

                fireEvent.click(getByTestId("secondOccupant.hasProspectAddress_field-label"));

                expect(getByTestId(`secondOccupant.address.city_field-input`)).toHaveValue(ADDRESS.CITY);
              });

              it("State", async function () {
                const { getByTestId } = renderAndPrepare();

                fireEvent.click(getByTestId("address.stateId_toggle"));

                fireEvent.click(
                  getByTestId("address.stateId_options").querySelector(`[data-value='${ADDRESS.STATE_ID}']`),
                );

                fireEvent.click(getByTestId("secondOccupant.hasProspectAddress_field-label"));

                const node = getByTestId(`secondOccupant.address.stateId_selected-text`);
                expect(node).toHaveTextContent(ADDRESS.STATE_TITLE);
              });

              it("Zip", function () {
                const { getByTestId } = renderAndPrepare();

                fireEvent.click(getByTestId("secondOccupant.hasProspectAddress_field-label"));

                expect(getByTestId(`secondOccupant.address.zip_field-input`)).toHaveValue(ADDRESS.ZIP);
              });
            });
          });
        });
      });
    });

    describe('"Related Party is 2nd Occupant" field:', function () {
      describe("Related Party is not specified:", function () {
        it('"Related Party is 2nd Occupant" field is disabled', () => {
          let { getByTestId } = render(<ProspectForm />);

          fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

          const node = getByTestId("relatedPartyIs2ndOccupant_field");
          expect(node).toHaveClass("CheckboxField_disabled");
        });
      });

      describe("Related Party is specified:", function () {
        const stateId = 1;
        const state = findWhere(State, { id: stateId });

        const RELATED_PARTY = {
          FIRST_NAME: "Lionel",
          LAST_NAME: "Messi",
          RELATIONSHIP_TYPE_NAME: "PARENT",
          ADDRESS: {
            STREET: "Related Party Street",
            CITY: "Related Party City",
            STATE_ID: 1,
            STATE_TITLE: state.label,
            ZIP: "12345",
          },
          CELL_PHONE: "+3752953674835",
          EMAIL: "leonelmessi@gmail.com",
        };

        function renderAndSpecifyRelatedParty() {
          let { getByTestId } = render(<ProspectForm />);

          fireEvent.change(getByTestId("relatedParty.firstName_field-input"), {
            target: { value: RELATED_PARTY.FIRST_NAME },
          });

          fireEvent.change(getByTestId("relatedParty.lastName_field-input"), {
            target: { value: RELATED_PARTY.LAST_NAME },
          });

          fireEvent.click(getByTestId("relatedParty.relationshipTypeName_toggle"));
          fireEvent.click(
            getByTestId("relatedParty.relationshipTypeName_options").querySelector(
              `[data-value=${RELATED_PARTY.RELATIONSHIP_TYPE_NAME}]`,
            ),
          );

          fireEvent.change(getByTestId("relatedParty.address.street_field-input"), {
            target: { value: RELATED_PARTY.ADDRESS.STREET },
          });

          fireEvent.change(getByTestId("relatedParty.address.city_field-input"), {
            target: { value: RELATED_PARTY.ADDRESS.CITY },
          });

          fireEvent.click(getByTestId("relatedParty.address.stateId_toggle"));
          fireEvent.click(
            getByTestId("relatedParty.address.stateId_options").querySelector(
              `[data-value='${RELATED_PARTY.ADDRESS.STATE_ID}']`,
            ),
          );

          fireEvent.change(getByTestId("relatedParty.address.zip_field-input"), {
            target: { value: RELATED_PARTY.ADDRESS.ZIP },
          });

          fireEvent.change(getByTestId("relatedParty.cellPhone_field-input"), {
            target: { value: RELATED_PARTY.CELL_PHONE },
          });

          fireEvent.change(getByTestId("relatedParty.email_field-input"), { target: { value: RELATED_PARTY.EMAIL } });

          fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

          fireEvent.click(getByTestId("relatedPartyIs2ndOccupant_field-label"));

          return { getByTestId };
        }

        describe('"Related Party is 2nd Occupant" field is not disabled, when:', function () {
          it("Related Party First Name is specified", () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.change(getByTestId("relatedParty.firstName_field-input"), {
              target: { value: RELATED_PARTY.FIRST_NAME },
            });

            const node = getByTestId("relatedPartyIs2ndOccupant_field");
            expect(node).not.toHaveClass("CheckboxField_disabled");
          });

          it("Related Party Last Name is specified", () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.change(getByTestId("relatedParty.lastName_field-input"), {
              target: { value: RELATED_PARTY.LAST_NAME },
            });

            const node = getByTestId("relatedPartyIs2ndOccupant_field");
            expect(node).not.toHaveClass("CheckboxField_disabled");
          });

          it("Related Party Relationship is specified", () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.click(getByTestId("relatedParty.relationshipTypeName_toggle"));
            fireEvent.click(
              getByTestId("relatedParty.relationshipTypeName_options").querySelector(
                `[data-value=${RELATED_PARTY.RELATIONSHIP_TYPE_NAME}]`,
              ),
            );

            const node = getByTestId("relatedPartyIs2ndOccupant_field");
            expect(node).not.toHaveClass("CheckboxField_disabled");
          });

          it("Related Party Address Street is specified", () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.change(getByTestId("relatedParty.address.street_field-input"), {
              target: { value: RELATED_PARTY.ADDRESS.STREET },
            });

            const node = getByTestId("relatedPartyIs2ndOccupant_field");
            expect(node).not.toHaveClass("CheckboxField_disabled");
          });

          it("Related Party Address City is specified", () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.change(getByTestId("relatedParty.address.city_field-input"), {
              target: { value: RELATED_PARTY.ADDRESS.CITY },
            });

            const node = getByTestId("relatedPartyIs2ndOccupant_field");
            expect(node).not.toHaveClass("CheckboxField_disabled");
          });

          it("Related Party Address State is specified", () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.click(getByTestId("relatedParty.address.stateId_toggle"));
            fireEvent.click(
              getByTestId("relatedParty.address.stateId_options").querySelector(
                `[data-value='${RELATED_PARTY.ADDRESS.STATE_ID}']`,
              ),
            );

            const node = getByTestId("relatedPartyIs2ndOccupant_field");
            expect(node).not.toHaveClass("CheckboxField_disabled");
          });

          it("Related Party Address ZIP is specified", () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.change(getByTestId("relatedParty.address.zip_field-input"), {
              target: { value: RELATED_PARTY.ADDRESS.ZIP },
            });

            const node = getByTestId("relatedPartyIs2ndOccupant_field");
            expect(node).not.toHaveClass("CheckboxField_disabled");
          });

          it("Related Party Cell Phone is specified", () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.change(getByTestId("relatedParty.cellPhone_field-input"), {
              target: { value: RELATED_PARTY.CELL_PHONE },
            });

            const node = getByTestId("relatedPartyIs2ndOccupant_field");
            expect(node).not.toHaveClass("CheckboxField_disabled");
          });

          it("Related Party Email is specified", () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            fireEvent.change(getByTestId("relatedParty.email_field-input"), { target: { value: RELATED_PARTY.EMAIL } });

            const node = getByTestId("relatedPartyIs2ndOccupant_field");
            expect(node).not.toHaveClass("CheckboxField_disabled");
          });
        });

        describe('"Related Party is 2nd Occupant" field is checked:', function () {
          describe("Second Occupant field initialization:", function () {
            it("First Name", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.firstName_field-input")).toHaveValue(RELATED_PARTY.FIRST_NAME);
            });

            it("Last Name", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.lastName_field-input")).toHaveValue(RELATED_PARTY.LAST_NAME);
            });

            it("Street", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.address.street_field-input")).toHaveValue(
                RELATED_PARTY.ADDRESS.STREET,
              );
            });

            it("City", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.address.city_field-input")).toHaveValue(RELATED_PARTY.ADDRESS.CITY);
            });

            it("State", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.address.stateId_selected-text")).toHaveTextContent(
                RELATED_PARTY.ADDRESS.STATE_TITLE,
              );
            });

            it("Zip", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.address.zip_field-input")).toHaveValue(RELATED_PARTY.ADDRESS.ZIP);
            });

            it("Cell Phone", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.cellPhone_field-input")).toHaveValue(RELATED_PARTY.CELL_PHONE);
            });

            it("Email", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.email_field-input")).toHaveValue(RELATED_PARTY.EMAIL);
            });
          });

          describe("Second Occupant field disabling:", () => {
            it("First Name", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.firstName_field")).toHaveClass("TextField_disabled");
            });

            it("Last Name", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.lastName_field")).toHaveClass("TextField_disabled");
            });

            it("Use prospect address", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.hasProspectAddress_field")).toHaveClass("CheckboxField_disabled");
            });

            it("Street", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.address.street_field")).toHaveClass("TextField_disabled");
            });

            it("City", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.address.city_field")).toHaveClass("TextField_disabled");
            });

            it("State", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.address.stateId_field")).toHaveClass("SelectField_disabled");
            });

            it("Zip", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.address.zip_field")).toHaveClass("TextField_disabled");
            });

            it("Cell Phone", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.cellPhone_field")).toHaveClass("PhoneField_disabled");
            });

            it("Email", function () {
              let { getByTestId } = renderAndSpecifyRelatedParty();
              expect(getByTestId("secondOccupant.email_field")).toHaveClass("TextField_disabled");
            });
          });
        });
      });
    });
  });

  describe("Add New Prospect:", function () {
    describe("Default Field values:", function () {
      const organizationId = 3;
      const organization = findWhere(OrganizationDetails, { id: organizationId });

      function testTextFieldToBeEmpty(title, name) {
        it(`"${title}" field initialized correctly`, () => {
          let { getByTestId } = render(<ProspectForm />);

          const node = getByTestId(`${name}_field-input`);
          expect(node).toHaveValue("");
        });
      }

      function testSelectFieldToBeEmpty(title, name) {
        it(`"${title}" field initialized correctly`, () => {
          let { getByTestId } = render(<ProspectForm />);

          const node = getByTestId(`${name}_selected-text`);
          expect(node).toHaveTextContent("Select");
        });
      }

      describe("Demographics & Insurance:", function () {
        testTextFieldToBeEmpty("First Name", "firstName");
        testTextFieldToBeEmpty("Last Name", "lastName");
        testTextFieldToBeEmpty("Middle Name", "middleName");
        testTextFieldToBeEmpty("Date Of Birth", "birthDate");
        testSelectFieldToBeEmpty("Gender", "genderId");
        testSelectFieldToBeEmpty("Marital Status", "maritalStatusId");
        testSelectFieldToBeEmpty("Race", "raceId");

        it('"Social Security Number" field initialized correctly', () => {
          let { getByTestId } = render(<ProspectForm />);

          const node = getByTestId("ssn_field-input");
          expect(node).toHaveValue("");
          expect(node).toHaveAttribute("placeholder", "XXX XX XXXX");
        });

        it('"Prospect doesn\'t have SSN" field initialized correctly', () => {
          let { queryByTestId } = render(<ProspectForm />);

          const node = queryByTestId("hasNoSsn_field-check-mark");
          expect(node).toBeNull();
        });

        testSelectFieldToBeEmpty("Veteran?", "veteranStatusName");

        it('"Network" field initialized correctly', () => {
          let { getByTestId } = render(<ProspectForm />);

          const node = getByTestId("insuranceNetworkId_search-input");
          expect(node).toHaveValue("");
          expect(node).toHaveAttribute("placeholder", "Search by network name");
        });

        testTextFieldToBeEmpty("Plan", "insurancePaymentPlan");

        it('"User Photo" field initialized correctly', () => {
          let { getByTestId } = render(<ProspectForm />);

          const node = getByTestId("avatar_field-selected-file");
          expect(node).toHaveTextContent("File not chosen");
        });
      });

      describe("Community:", function () {
        it('"Organization" field initialized correctly', async () => {
          let { getByTestId } = renderAndConfigure({ organizationId });

          await waitFor(() => {
            const node = getByTestId("organizationId_selected-text");
            expect(node).toHaveTextContent(organization.name);
          });
        });

        describe('"Community" field initialized correctly:', function () {
          it("Organization has multiple communities", async () => {
            let { getByTestId } = renderAndConfigure({ organizationId });

            await waitFor(() => {
              const node = getByTestId("communityId_selected-text");
              expect(node).toHaveTextContent("Select");
            });
          });

          it("Organization has single community", async () => {
            const organizationId = 2964;
            const community = findWhere(Community, { organizationId });

            let { getByTestId } = renderAndConfigure({ organizationId, communityId: community.id });

            await waitFor(() => {
              const node = getByTestId("communityId_selected-text");
              expect(node).toHaveTextContent(community.name);
            });
          });
        });
      });

      describe("Contact Information", function () {
        testTextFieldToBeEmpty("Street", "address.street");
        testTextFieldToBeEmpty("City", "address.city");
        testSelectFieldToBeEmpty("State", "address.stateId");
        testTextFieldToBeEmpty("Zip Code", "address.zip");

        it('"Cell Phone" field initialized correctly', () => {
          let { getByTestId } = render(<ProspectForm />);

          const node = getByTestId("cellPhone_field-input");
          expect(node).toHaveValue("+1");
        });

        testTextFieldToBeEmpty("Email", "email");
      });

      describe("Primary Contact", function () {
        it('"Primary contact" field initialized correctly', () => {
          let { getByTestId } = render(<ProspectForm />);

          const node1 = getByTestId("primaryContact.typeName_field-SELF-check-mark");
          expect(node1).not.toHaveClass("Radio-CheckMark_checked");

          const node2 = getByTestId("primaryContact.typeName_field-CARE_TEAM_MEMBER-check-mark");
          expect(node2).not.toHaveClass("Radio-CheckMark_checked");
        });

        it('"Primary notification method" field initialized correctly', () => {
          let { getByTestId } = render(<ProspectForm />);

          const node1 = getByTestId("primaryContact.notificationMethodName_field-EMAIL-check-mark");
          expect(node1).not.toHaveClass("Radio-CheckMark_checked");

          const node2 = getByTestId("primaryContact.notificationMethodName_field-PHONE-check-mark");
          expect(node2).not.toHaveClass("Radio-CheckMark_checked");
        });
      });

      describe("Move-In Information:", function () {
        testTextFieldToBeEmpty("Move-In Date", "moveInDate");
        testTextFieldToBeEmpty("Rental Agreement Signed Date", "rentalAgreementSignedDate");
        testTextFieldToBeEmpty("Assessment Date", "assessmentDate");
        testTextFieldToBeEmpty("Referral Source", "referralSource");
        testTextFieldToBeEmpty("Notes", "notes");
      });

      describe("Related Party:", function () {
        testTextFieldToBeEmpty("First Name", "relatedParty.firstName");
        testTextFieldToBeEmpty("Last Name", "relatedParty.lastName");
        testSelectFieldToBeEmpty("Relationship", "relatedParty.relationshipTypeName");
        testTextFieldToBeEmpty("Street", "relatedParty.address.street");
        testTextFieldToBeEmpty("City", "relatedParty.address.city");
        testSelectFieldToBeEmpty("State", "relatedParty.address.stateId");
        testTextFieldToBeEmpty("Zip Code", "relatedParty.address.zip");

        it('"Cell Phone" field initialized correctly', () => {
          let { getByTestId } = render(<ProspectForm />);

          const node = getByTestId("relatedParty.cellPhone_field-input");
          expect(node).toHaveValue("+1");
        });

        testTextFieldToBeEmpty("Email", "relatedParty.email");
      });

      describe("Second Occupant:", function () {
        function testTextFieldToBeEmpty(title, name) {
          it(`"${title}" field initialized correctly`, () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            const node = getByTestId(`${name}_field-input`);
            expect(node).toHaveValue("");
          });
        }

        function testSelectFieldToBeEmpty(title, name) {
          it(`"${title}" field initialized correctly`, () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            const node = getByTestId(`${name}_selected-text`);
            expect(node).toHaveTextContent("Select");
          });
        }

        describe("Demographics & Insurance", function () {
          it(`"Related Party is 2nd Occupant" field initialized correctly`, () => {
            let { getByTestId, queryByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            const node = queryByTestId("relatedPartyIs2ndOccupant_field-check-mark");
            expect(node).toBeNull();
          });

          testTextFieldToBeEmpty("First Name", "secondOccupant.firstName");
          testTextFieldToBeEmpty("Last Name", "secondOccupant.lastName");
          testTextFieldToBeEmpty("Middle Name", "secondOccupant.middleName");
          testTextFieldToBeEmpty("Date Of Birth", "secondOccupant.birthDate");
          testSelectFieldToBeEmpty("Gender", "secondOccupant.genderId");
          testSelectFieldToBeEmpty("Marital Status", "secondOccupant.maritalStatusId");
          testSelectFieldToBeEmpty("Race", "secondOccupant.raceId");

          it('"Social Security Number" field initialized correctly', () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            const node = getByTestId("secondOccupant.ssn_field-input");
            expect(node).toHaveValue("");
            expect(node).toHaveAttribute("placeholder", "XXX XX XXXX");
          });

          it('"2nd occupant doesn\'t have SSN" field initialized correctly', () => {
            let { getByTestId, queryByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            const node = queryByTestId("secondOccupant.hasNoSsn_field-check-mark");
            expect(node).toBeNull();
          });

          testSelectFieldToBeEmpty("Veteran?", "secondOccupant.veteranStatusName");

          it('"Network" field initialized correctly', () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            const node = getByTestId("secondOccupant.insuranceNetworkId_search-input");
            expect(node).toHaveValue("");
            expect(node).toHaveAttribute("placeholder", "Search by network name");
          });

          testTextFieldToBeEmpty("Plan", "secondOccupant.insurancePaymentPlan");

          it('"User Photo" field initialized correctly', () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            const node = getByTestId("secondOccupant.avatar_field-selected-file");
            expect(node).toHaveTextContent("File not chosen");
          });
        });

        describe("Contact Information:", function () {
          it('"Use prospect address" field initialized correctly', () => {
            let { getByTestId, queryByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            const node = queryByTestId("secondOccupant.hasProspectAddress_field-check-mark");
            expect(node).toBeNull();
          });

          testTextFieldToBeEmpty("Street", "secondOccupant.address.street");
          testTextFieldToBeEmpty("City", "secondOccupant.address.city");
          testSelectFieldToBeEmpty("State", "secondOccupant.address.stateId");
          testTextFieldToBeEmpty("Zip Code", "secondOccupant.address.zip");

          it('"Cell Phone" field initialized correctly', () => {
            let { getByTestId } = render(<ProspectForm />);

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            const node = getByTestId("secondOccupant.cellPhone_field-input");
            expect(node).toHaveValue("+1");
          });

          testTextFieldToBeEmpty("Email", "secondOccupant.email");
        });
      });
    });
  });

  describe("Edit Prospect. Initialization:", function () {
    describe("Field Initialization:", function () {
      const prospectId = 2;
      const organizationId = 3;

      const prospect = findWhere(ProspectDetails, { id: prospectId });
      const gender = findWhere(Gender, { id: prospect.genderId });
      const maritalStatus = findWhere(MaritalStatus, { id: prospect.maritalStatusId });
      const race = findWhere(Race, { id: prospect.raceId });
      const isVeteran = findWhere(IsVeteranCases, { name: prospect.veteranStatusName });
      const network = findWhere(Network2, { id: prospect.insuranceNetworkId });
      const organization = findWhere(Organization, { id: prospect.organizationId });
      const community = findWhere(Community, { id: prospect.communityId });
      const state = findWhere(State, { id: prospect.address.stateId });

      function testTextFieldToHaveValue(title, name, value) {
        it(`"${title}" field initialized correctly`, async () => {
          let { getByTestId } = renderAndConfigure({ prospectId });

          await waitFor(() => {
            const node = getByTestId(`${name}_field-input`);
            expect(node).toHaveValue(value);
          });
        });
      }

      function testSelectFieldToHaveValue(title, name, value) {
        it(`"${title}" field initialized correctly`, async () => {
          let { getByTestId } = renderAndConfigure({ prospectId });

          await waitFor(() => {
            const node = getByTestId(`${name}_selected-text`);
            expect(node).toHaveTextContent(value);
          });
        });
      }

      function testFileFieldToHaveValue(title, name, value) {
        it(`"${title}" field initialized correctly`, async () => {
          let { getByTestId } = renderAndConfigure({ prospectId });

          await waitFor(() => {
            const node = getByTestId(`${name}_field-selected-file`);
            expect(node).toHaveTextContent(value);
          });
        });
      }

      function testCheckBoxFieldToHaveValue(title, name, value) {
        it(`"${title}" field initialized correctly`, async () => {
          let { queryByTestId } = renderAndConfigure({ prospectId });

          await waitFor(() => {
            const node = queryByTestId(`${name}_field-check-mark`);

            if (value) {
              expect(node).toBeInTheDocument();
              expect(node).toBeVisible();
            } else {
              expect(node).toBeNull();
            }
          });
        });
      }

      function testRadioButtonFieldToHaveValue(title, name, value, isChecked) {
        it(`"${title}" field initialized correctly`, async () => {
          let { queryByTestId } = renderAndConfigure({ prospectId });

          await waitFor(() => {
            const node = queryByTestId(`${name}_field-${value}-check-mark`);

            if (isChecked) {
              expect(node).toBeInTheDocument();
              expect(node).toBeVisible();
            } else {
              expect(node).toBeNull();
            }
          });
        });
      }

      describe("Demographics & Insurance:", function () {
        testTextFieldToHaveValue("First Name", "firstName", prospect.firstName);
        testTextFieldToHaveValue("Last Name", "lastName", prospect.lastName);
        testTextFieldToHaveValue("Middle Name", "middleName", prospect.middleName);
        testTextFieldToHaveValue("Date Of Birth", "birthDate", prospect.birthDate);
        testSelectFieldToHaveValue("Gender", "genderId", gender.title);
        testSelectFieldToHaveValue("Marital Status", "maritalStatusId", maritalStatus.label);
        testSelectFieldToHaveValue("Race", "raceId", race.title);
        testTextFieldToHaveValue("Social Security Number", "ssn", formatSSN(prospect.ssn));
        testCheckBoxFieldToHaveValue("Prospect doesn't have SSN", "hasNoSsn", trim(prospect.ssn).length === 0);
        testSelectFieldToHaveValue("Veteran?", "veteranStatusName", isVeteran.title);
        testSelectFieldToHaveValue("Network", "insuranceNetworkId", network.title);
        testTextFieldToHaveValue("Plan", "insurancePaymentPlan", prospect.insurancePaymentPlan);
        testFileFieldToHaveValue("Photo", "avatar", prospect.avatarName);
      });

      describe("Community:", function () {
        testSelectFieldToHaveValue("Organization Name", "organizationId", organization.label);
        testSelectFieldToHaveValue("Community Name", "communityId", community.name);
      });

      describe("Contact Information:", function () {
        testTextFieldToHaveValue("Street", "address.street", prospect.address.street);
        testTextFieldToHaveValue("City", "address.city", prospect.address.city);
        testSelectFieldToHaveValue("State", "address.stateId", state.label);
        testTextFieldToHaveValue("Zip Code", "address.zip", prospect.address.zip);
        testTextFieldToHaveValue("Cell Phone", "cellPhone", "+" + prospect.cellPhone);
        testTextFieldToHaveValue("Email", "email", prospect.email);
      });

      describe("Primary Contact:", function () {
        testRadioButtonFieldToHaveValue(
          "Primary contact",
          "primaryContact.typeName",
          prospect.primaryContact.typeName,
          true,
        );

        testRadioButtonFieldToHaveValue(
          "Primary notification method",
          "primaryContact.notificationMethodName",
          prospect.primaryContact.notificationMethodName,
          true,
        );
      });

      describe("Move-In Information:", function () {
        testTextFieldToHaveValue("Move-In Date", "moveInDate", format(prospect.moveInDate, formats.americanMediumDate));
        testTextFieldToHaveValue(
          "Rental Agreement Signed Date",
          "rentalAgreementSignedDate",
          format(prospect.rentalAgreementSignedDate, formats.americanMediumDate),
        );
        testTextFieldToHaveValue(
          "Assessment Date",
          "assessmentDate",
          format(prospect.assessmentDate, formats.americanMediumDate),
        );
        testTextFieldToHaveValue("Referral Source", "referralSource", prospect.referralSource);
        testTextFieldToHaveValue("Notes", "notes", prospect.notes);
      });

      describe("Related Party:", function () {
        testTextFieldToHaveValue("First Name", "relatedParty.firstName", prospect.relatedParty.firstName);
        testTextFieldToHaveValue("Last Name", "relatedParty.lastName", prospect.relatedParty.lastName);
        testSelectFieldToHaveValue(
          "Relationship",
          "relatedParty.relationshipTypeName",
          prospect.relatedParty.relationshipTypeTitle,
        );
        testTextFieldToHaveValue("Street", "relatedParty.address.street", prospect.relatedParty.address.street);
        testTextFieldToHaveValue("City", "relatedParty.address.city", prospect.relatedParty.address.city);
        const state = findWhere(State, { id: prospect.relatedParty.address.stateId });
        testSelectFieldToHaveValue("State", "relatedParty.address.stateId", state.label);
        testTextFieldToHaveValue("Zip Code", "relatedParty.address.zip", prospect.relatedParty.address.zip);
        testTextFieldToHaveValue("Cell Phone", "relatedParty.cellPhone", "+" + prospect.relatedParty.cellPhone);
        testTextFieldToHaveValue("Email", "relatedParty.email", prospect.relatedParty.email);
      });

      describe("Second Occupant:", function () {
        function testTextFieldToHaveValue(title, name, value) {
          it(`"${title}" field initialized correctly`, async () => {
            let { getByTestId } = renderAndConfigure({ prospectId });

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            await waitFor(() => {
              const node = getByTestId(`${name}_field-input`);
              expect(node).toHaveValue(value);
            });
          });
        }

        function testSelectFieldToHaveValue(title, name, value) {
          it(`"${title}" field initialized correctly`, async () => {
            let { getByTestId } = renderAndConfigure({ prospectId });

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            await waitFor(() => {
              const node = getByTestId(`${name}_selected-text`);
              expect(node).toHaveTextContent(value);
            });
          });
        }

        function testSearchFieldToHaveValue(title, name, value) {
          it(`"${title}" field initialized correctly`, async () => {
            let { getByTestId } = renderAndConfigure({ prospectId });

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            await waitFor(() => {
              const node = getByTestId(`${name}_search-input`);
              expect(node).toHaveValue(value);
            });
          });
        }

        function testCheckBoxFieldToHaveValue(title, name, value) {
          it(`"${title}" field initialized correctly`, async () => {
            let { getByTestId, queryByTestId } = renderAndConfigure({ prospectId });

            fireEvent.click(getByTestId("hasSecondOccupant_field-label"));

            await waitFor(() => {
              const node = queryByTestId(`${name}_field-check-mark`);

              if (value) {
                expect(node).toBeInTheDocument();
                expect(node).toBeVisible();
              } else {
                expect(node).toBeNull();
              }
            });
          });
        }

        const { secondOccupant } = prospect;

        const gender = findWhere(Gender, { id: secondOccupant.genderId });
        const maritalStatus = findWhere(MaritalStatus, { id: secondOccupant.maritalStatusId });
        const race = findWhere(Race, { id: secondOccupant.raceId });
        const isVeteran = findWhere(IsVeteranCases, { name: secondOccupant.veteranStatusName });
        const network = findWhere(Network2, { id: secondOccupant.insuranceNetworkId });
        const state = findWhere(State, { id: secondOccupant.address.stateId });

        describe("Demographics & Insurance:", function () {
          testTextFieldToHaveValue("First Name", "secondOccupant.firstName", secondOccupant.firstName);
          testTextFieldToHaveValue("Last Name", "secondOccupant.lastName", secondOccupant.lastName);
          testTextFieldToHaveValue("Middle Name", "secondOccupant.middleName", secondOccupant.middleName);
          testTextFieldToHaveValue("Date Of Birth", "secondOccupant.birthDate", secondOccupant.birthDate);
          testSelectFieldToHaveValue("Gender", "secondOccupant.genderId", gender.title);
          testSelectFieldToHaveValue("Marital Status", "secondOccupant.maritalStatusId", maritalStatus.label);
          testSelectFieldToHaveValue("Race", "secondOccupant.raceId", race.title);
          testTextFieldToHaveValue("Social Security Number", "secondOccupant.ssn", formatSSN(secondOccupant.ssn));
          testCheckBoxFieldToHaveValue(
            "Prospect doesn't have SSN",
            "secondOccupant.hasNoSsn",
            trim(secondOccupant.ssn).length === 0,
          );
          testSelectFieldToHaveValue("Veteran?", "secondOccupant.veteranStatusName", isVeteran.title);
          testSearchFieldToHaveValue("Network", "secondOccupant.insuranceNetworkId", network.title);
          testTextFieldToHaveValue("Plan", "secondOccupant.insurancePaymentPlan", secondOccupant.insurancePaymentPlan);
          testFileFieldToHaveValue("Photo", "secondOccupant.avatar", secondOccupant.avatarName);
        });

        describe("Contact Information:", function () {
          testTextFieldToHaveValue("Street", "secondOccupant.address.street", secondOccupant.address.street);
          testTextFieldToHaveValue("City", "secondOccupant.address.city", secondOccupant.address.city);
          testSelectFieldToHaveValue("State", "secondOccupant.address.stateId", state.label);
          testTextFieldToHaveValue("Zip Code", "secondOccupant.address.zip", secondOccupant.address.zip);
          testTextFieldToHaveValue("Cell Phone", "secondOccupant.cellPhone", "+" + secondOccupant.cellPhone);
          testTextFieldToHaveValue("Email", "secondOccupant.email", secondOccupant.email);
        });
      });
    });
  });

  afterAll(() => {
    server.close();
  });
});
