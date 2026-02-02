import React from "react";

import { map, where, chain, findWhere } from "underscore";

import { trim } from "lodash";
import moment from "moment";

import { rest } from "msw";
import { setupServer } from "msw/node";
import { Provider } from "react-redux";

import { createBrowserHistory } from "history";

import { waitFor, render, fireEvent } from "lib/test-utils";

import Response from "lib/mock/server/Response";

import { isEmpty, isNotEmpty } from "lib/utils/Utils";

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
  ContactDetails,
  OrganizationDetails,
} from "lib/mock/db/DB";

import configureStore from "redux/configureStore";

import ClientForm from "./ClientForm";

const BASE_URL = "https://dev.simplyconnect.me/web-portal-mock-backend";

const server = setupServer(
  rest.get(`${BASE_URL}/clients/:clientId`, (req, res, ctx) => {
    return res(ctx.json(Response.success(findWhere(ClientDetails, { id: +req.params.clientId }))));
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
);

const formatSSN = (ssn) => (ssn ? `###-##-${ssn.substring(5, 9)}` : "");

function renderAndConfigure({ clientId, communityIds = [], organizationId = 3, userOrganizationId = 3 }) {
  const { store, ...config } = render(
    <ClientForm clientId={clientId} communityIds={communityIds} organizationId={organizationId} />,
  );

  const user = findWhere(User, { organizationId: userOrganizationId });
  store.dispatch({ type: "LOGIN_SUCCESS", payload: user });

  return { store, ...config };
}

describe("<ClientForm>:", function () {
  beforeAll(() => {
    server.listen();
  });

  describe("All fields are visible:", function () {
    const organizationId = 3;

    describe("Demographics:", function () {
      it("First Name", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("firstName_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Last Name", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("lastName_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Date Of Birth", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("birthDate_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Gender", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("genderId_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Marital Status", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("maritalStatusId_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Race", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("raceId_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Social Security Number", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("ssn_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Client doesn't have SSN", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("hasNoSSN_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("State", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("address.stateId_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("City", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("address.city_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Zip Code", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("address.zip_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Street", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("address.street_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("User Photo", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("avatar_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });
    });

    describe("Power of Attorney (POA):", function () {
      it("First Name", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        fireEvent.click(getByTestId("add-attorney-btn"));

        const node = getByTestId("attorneys.0.firstName_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Last Name", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        fireEvent.click(getByTestId("add-attorney-btn"));

        const node = getByTestId("attorneys.0.lastName_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("POA Type", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        fireEvent.click(getByTestId("add-attorney-btn"));

        const node = getByTestId("attorneys.0.types_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Email", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        fireEvent.click(getByTestId("add-attorney-btn"));

        const node = getByTestId("attorneys.0.email_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Phone", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        fireEvent.click(getByTestId("add-attorney-btn"));

        const node = getByTestId("attorneys.0.phone_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("State", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        fireEvent.click(getByTestId("add-attorney-btn"));

        const node = getByTestId("attorneys.0.state_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("City", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        fireEvent.click(getByTestId("add-attorney-btn"));

        const node = getByTestId("attorneys.0.city_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Zip code", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        fireEvent.click(getByTestId("add-attorney-btn"));

        const node = getByTestId("attorneys.0.zipCode_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Street", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        fireEvent.click(getByTestId("add-attorney-btn"));

        const node = getByTestId("attorneys.0.street_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });
    });

    describe("Community:", function () {
      it("Organization", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("organizationId_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Community", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("communityId_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Unit #", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("unit_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });
    });

    describe("Telecom:", function () {
      it("Cell Phone", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("cellPhone_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Home Phone", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("phone_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Email", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("email_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Client doesn't have email", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("hasNoEmail_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });
    });

    describe("Insurance:", function () {
      it("Network", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("insurances.0.networkId_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Plan", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("insurances.0.paymentPlan_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Group Number", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("insurances.0.groupNumber_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Member Number", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("insurances.0.memberNumber_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Medicare Number", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("medicareNumber_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Medicaid Number", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("medicaidNumber_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Add Authorization", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("hasInsuranceAuthorizations_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });
    });

    describe("Ancillary Information", function () {
      it("Retained", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("retained_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Has Advanced Directive On File", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("hasAdvancedDirectiveOnFile_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("PCP First Name", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("primaryCarePhysicianFirstName_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("PCP Last Name", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("primaryCarePhysicianLastName_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Intake Date", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("intakeDate_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Current Pharmacy Name", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("currentPharmacyName_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Referral Source", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("referralSource_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Risk score", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("riskScore_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });
    });

    describe("Primary contact", function () {
      it("Primary contact", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("primaryContact.typeName_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });

      it("Primary notification method", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);
        const node = getByTestId("primaryContact.notificationMethodName_field");
        expect(node).toBeInTheDocument();
        expect(node).toBeVisible();
      });
    });
  });

  describe("Fields marked as required:", function () {
    const organizationId = 3;

    describe("Demographics:", function () {
      it("First Name", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);

        const node = getByTestId("firstName_field-label");
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });

      it("Last Name", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);

        const node = getByTestId("lastName_field-label");
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });

      it("Date Of Birth", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);

        const node = getByTestId("birthDate_field-label");
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });

      it("Gender", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);

        const node = getByTestId("genderId_field-label");
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });

      it("Social Security Number", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);

        const node = getByTestId("ssn_field-label");
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });

      it("State", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);

        const node = getByTestId("address.stateId_field-label");
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });

      it("City", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);

        const node = getByTestId("address.city_field-label");
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });

      it("Zip Code", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);

        const node = getByTestId("address.zip_field-label");
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });

      it("Street", async () => {
        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[]} />);

        const node = getByTestId("address.street_field-label");
        expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
      });
    });
  });

  describe("Add new Client. Default Field values:", function () {
    const organizationId = 3;

    const organization = findWhere(OrganizationDetails, { id: organizationId });

    describe("Demographics:", function () {
      it('"First Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("firstName_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Last Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("lastName_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Date Of Birth" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("birthDate_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Gender" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("genderId_selected-text");
          expect(node).toHaveTextContent("Select");
        });
      });

      it('"Marital Status" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("maritalStatusId_selected-text");
          expect(node).toHaveTextContent("Select");
        });
      });

      it('"Race" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("raceId_selected-text");
          expect(node).toHaveTextContent("Select");
        });
      });

      it('"Social Security Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("ssn_field-input");
          expect(node).toHaveValue("");
          expect(node).toHaveAttribute("placeholder", "XXX XX XXXX");
        });
      });

      it('"Client doesn\'t have SSN" field initialized correctly', async () => {
        let { queryByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = queryByTestId("hasNoSSN_field-check-mark");
          expect(node).toBeNull();
        });
      });

      it('"State" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("address.stateId_selected-text");
          expect(node).toHaveTextContent("Select");
        });
      });

      it('"City" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("address.city_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Zip Code" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("address.zip_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Street" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("address.street_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"User Photo" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("avatar_field-selected-file");
          expect(node).toHaveTextContent("File not chosen");
        });
      });
    });

    describe("Power of Attorney (POA)", function () {
      it('"First Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });
        fireEvent.click(getByTestId("add-attorney-btn"));

        await waitFor(() => {
          const node = getByTestId("attorneys.0.firstName_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Last Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });
        fireEvent.click(getByTestId("add-attorney-btn"));

        await waitFor(() => {
          const node = getByTestId("attorneys.0.lastName_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"POA Type" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });
        fireEvent.click(getByTestId("add-attorney-btn"));

        await waitFor(() => {
          const node = getByTestId("attorneys.0.types_selected-text");
          expect(node).toHaveTextContent("Select");
        });
      });

      it('"Email" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });
        fireEvent.click(getByTestId("add-attorney-btn"));

        await waitFor(() => {
          const node = getByTestId("attorneys.0.email_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Phone" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });
        fireEvent.click(getByTestId("add-attorney-btn"));

        await waitFor(() => {
          const node = getByTestId("attorneys.0.phone_field-input");
          expect(node).toHaveValue("+1");
        });
      });

      it('"State" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });
        fireEvent.click(getByTestId("add-attorney-btn"));

        await waitFor(() => {
          const node = getByTestId("attorneys.0.state_selected-text");
          expect(node).toHaveTextContent("Select");
        });
      });

      it('"City" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });
        fireEvent.click(getByTestId("add-attorney-btn"));

        await waitFor(() => {
          const node = getByTestId("attorneys.0.city_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Zip code" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });
        fireEvent.click(getByTestId("add-attorney-btn"));

        await waitFor(() => {
          const node = getByTestId("attorneys.0.zipCode_field-input");
          expect(node).toHaveValue(null);
        });
      });

      it('"Street" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });
        fireEvent.click(getByTestId("add-attorney-btn"));

        await waitFor(() => {
          const node = getByTestId("attorneys.0.street_field-input");
          expect(node).toHaveValue("");
        });
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
          const communityIds = chain(Community)
            .where({ organizationId })
            .map((o) => o.id)
            .value();

          let { getByTestId } = renderAndConfigure({ organizationId, communityIds });

          await waitFor(() => {
            const node = getByTestId("communityId_selected-text");
            expect(node).toHaveTextContent("Select");
          });
        });

        it("Organization has single community", async () => {
          const organizationId = 2964;
          const communityIds = chain(Community)
            .where({ organizationId })
            .map((o) => o.id)
            .value();

          let { getByTestId } = renderAndConfigure({ organizationId, communityIds });

          const community = findWhere(Community, { organizationId });

          await waitFor(() => {
            const node = getByTestId("communityId_selected-text");
            expect(node).toHaveTextContent(community.name);
          });
        });
      });

      it("Unit #", async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("unit_field-input");
          expect(node).toHaveValue("");
        });
      });
    });

    describe("Telecom", function () {
      it('"Cell Phone" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("cellPhone_field-input");
          expect(node).toHaveValue("+1");
        });
      });

      it('"Home Phone" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("phone_field-input");
          expect(node).toHaveValue("+1");
        });
      });

      it('"Email" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("email_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Client doesn\'t have email" field initialized correctly', async () => {
        let { queryByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = queryByTestId("hasNoEmail_field-check-mark");
          expect(node).toBeNull();
        });
      });
    });

    describe("Insurance", function () {
      it('"Network" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("insurances.0.networkId_search-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Plan" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("insurances.0.paymentPlan_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Group Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("insurances.0.groupNumber_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Member Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("insurances.0.memberNumber_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Medicare Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("medicareNumber_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Medicaid Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("medicaidNumber_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Add Authorization" field initialized correctly', async () => {
        let { queryByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = queryByTestId("hasInsuranceAuthorizations_field-check-mark");
          expect(node).toBeNull();
        });
      });
    });

    describe("Ancillary Information", function () {
      it('"Retained" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("retained_field-true-check-mark");
          expect(node).not.toHaveClass("Radio-CheckMark_checked");
        });

        await waitFor(() => {
          const node = getByTestId("retained_field-false-check-mark");
          expect(node).not.toHaveClass("Radio-CheckMark_checked");
        });
      });

      it('"PCP First Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("primaryCarePhysicianFirstName_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"PCP Last Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("primaryCarePhysicianLastName_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Intake Date" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("intakeDate_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Current Pharmacy Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("currentPharmacyName_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Referral Source" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("referralSource_field-input");
          expect(node).toHaveValue("");
        });
      });

      it('"Risk score" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("riskScore_field-input");
          expect(node).toHaveValue("");
        });
      });
    });

    describe("Primary contact", function () {
      it('"Primary contact" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("primaryContact.typeName_field-SELF-check-mark");
          expect(node).not.toHaveClass("Radio-CheckMark_checked");
        });

        await waitFor(() => {
          const node = getByTestId("primaryContact.typeName_field-CARE_TEAM_MEMBER-check-mark");
          expect(node).not.toHaveClass("Radio-CheckMark_checked");
        });
      });

      it('"Primary notification method" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ organizationId });

        await waitFor(() => {
          const node = getByTestId("primaryContact.notificationMethodName_field-EMAIL-check-mark");
          expect(node).not.toHaveClass("Radio-CheckMark_checked");
        });

        await waitFor(() => {
          const node = getByTestId("primaryContact.notificationMethodName_field-PHONE-check-mark");
          expect(node).not.toHaveClass("Radio-CheckMark_checked");
        });
      });
    });
  });

  describe("Business Logic:", function () {
    describe("Community Section:", function () {
      it("should select a Community by default by changing Organization with one Community", async function () {
        const organizationId = 2964;

        const community = findWhere(Community, { organizationId });

        let { getByTestId } = render(<ClientForm organizationId={organizationId} communityIds={[community.id]} />);

        await waitFor(() => {
          const node = getByTestId("communityId_selected-text");
          expect(node).toHaveTextContent(community.name);
        });
      });
    });

    describe("Insurance Section:", function () {
      describe("Add Authorization Section:", function () {
        describe("Authorization Section fields:", function () {
          it('"Add Authorization" button is visible', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            await waitFor(() => {
              const node = getByTestId("add-insurance-auth-btn");
              expect(node).toBeInTheDocument();
              expect(node).toBeVisible();
            });
          });

          it('"Start Date" field is visible', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.0.startDate_field-input");
              expect(node).toBeInTheDocument();
              expect(node).toBeVisible();
            });
          });

          it('"Start Date" field is required', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.0.startDate_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });
          });

          it('"End Date" field is visible', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.0.endDate_field-input");
              expect(node).toBeInTheDocument();
              expect(node).toBeVisible();
            });
          });

          it('"End Date" field is required', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.0.endDate_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });
          });

          it('"Authorization Number" field is visible', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.0.number_field-input");
              expect(node).toBeInTheDocument();
              expect(node).toBeVisible();
            });
          });

          it('"Authorization Number" field is required', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.0.number_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });
          });
        });

        describe("Authorization Section. Second authorization:", function () {
          const now = Date.now();
          const startDate = moment(now).format("MM/DD/YYYY hh:mm A [GMT]Z");
          const endDate = moment(now).add(1, "day").format("MM/DD/YYYY hh:mm A [GMT]Z");

          it('Second "Start Date" field is visible', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            fireEvent.change(getByTestId("insuranceAuthorizations.0.startDate_field-input"), {
              target: { value: startDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.endDate_field-input"), {
              target: { value: endDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.number_field-input"), {
              target: { value: "111111" },
            });

            fireEvent.click(getByTestId("add-insurance-auth-btn"));

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.startDate_field-input");
              expect(node).toBeInTheDocument();
              expect(node).toBeVisible();
            });
          });

          it('Second "End Date" field is visible', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            fireEvent.change(getByTestId("insuranceAuthorizations.0.startDate_field-input"), {
              target: { value: startDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.endDate_field-input"), {
              target: { value: endDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.number_field-input"), {
              target: { value: "111111" },
            });

            fireEvent.click(getByTestId("add-insurance-auth-btn"));

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.endDate_field-input");
              expect(node).toBeInTheDocument();
              expect(node).toBeVisible();
            });
          });

          it('Second "Authorization Number" field is visible', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            fireEvent.change(getByTestId("insuranceAuthorizations.0.startDate_field-input"), {
              target: { value: startDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.endDate_field-input"), {
              target: { value: endDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.number_field-input"), {
              target: { value: "111111" },
            });

            fireEvent.click(getByTestId("add-insurance-auth-btn"));

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.number_field-input");
              expect(node).toBeInTheDocument();
              expect(node).toBeVisible();
            });
          });

          //todo: some codeship issues
          xit('Second All fields are required if "Start Date" is filled', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            fireEvent.change(getByTestId("insuranceAuthorizations.0.startDate_field-input"), {
              target: { value: startDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.endDate_field-input"), {
              target: { value: endDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.number_field-input"), {
              target: { value: "111111" },
            });

            fireEvent.click(getByTestId("add-insurance-auth-btn"));

            fireEvent.change(getByTestId("insuranceAuthorizations.1.startDate_field-input"), {
              target: { value: startDate },
            });

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.startDate_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.endDate_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.number_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });
          });

          //todo: some codeship issues
          xit('Second All fields are required if "End Date" is filled', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            fireEvent.change(getByTestId("insuranceAuthorizations.0.startDate_field-input"), {
              target: { value: startDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.endDate_field-input"), {
              target: { value: endDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.number_field-input"), {
              target: { value: "111111" },
            });

            fireEvent.click(getByTestId("add-insurance-auth-btn"));

            fireEvent.change(getByTestId("insuranceAuthorizations.1.endDate_field-input"), {
              target: { value: endDate },
            });

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.startDate_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.endDate_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.number_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });
          });

          //todo: some codeship issues
          xit('Second All fields are required if "Authorization Number" is filled', async function () {
            let { getByTestId } = renderAndConfigure({ organizationId: 3 });

            fireEvent.click(getByTestId("hasInsuranceAuthorizations_field-checkbox"));

            fireEvent.change(getByTestId("insuranceAuthorizations.0.startDate_field-input"), {
              target: { value: startDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.endDate_field-input"), {
              target: { value: endDate },
            });

            fireEvent.change(getByTestId("insuranceAuthorizations.0.number_field-input"), {
              target: { value: "111111" },
            });

            fireEvent.click(getByTestId("add-insurance-auth-btn"));

            fireEvent.change(getByTestId("insuranceAuthorizations.1.number_field-input"), {
              target: { value: "111111" },
            });

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.startDate_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.endDate_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });

            await waitFor(() => {
              const node = getByTestId("insuranceAuthorizations.1.number_field-label");
              expect(String(node.innerHTML).endsWith("*")).toBeTruthy();
            });
          });
        });
      });
    });
  });

  describe("Edit Client. Initialization:", function () {
    const clientId = 32041;
    const organizationId = 3;

    const client = findWhere(ClientDetails, { id: clientId });

    describe("Demographics:", function () {
      it('"First Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("firstName_field-input");
          expect(node).toHaveValue(client.firstName);
        });
      });

      it('"Last Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("lastName_field-input");
          expect(node).toHaveValue(client.lastName);
        });
      });

      it('"Date Of Birth" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("birthDate_field-input");
          expect(node).toHaveValue(client.birthDate);
        });
      });

      it('"Gender" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("genderId_selected-text");
          expect(node).toHaveTextContent(client.gender);
        });
      });

      it('"Marital Status" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("maritalStatusId_selected-text");
          expect(node).toHaveTextContent(client.maritalStatus);
        });
      });

      it('"Race" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("raceId_selected-text");
          expect(node).toHaveTextContent(client.race);
        });
      });

      it('"Social Security Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("ssn_field-input");
          expect(node).toHaveValue(formatSSN(client.ssn));
        });
      });

      it('"Client doesn\'t have SSN" field initialized correctly', async () => {
        let { queryByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = queryByTestId("hasNoSSN_field-check-mark");

          if (trim(client.ssn).length === 0) {
            expect(node).toBeInTheDocument();
            expect(node).toBeVisible();
          } else {
            expect(node).toBeNull();
          }
        });
      });

      it('"State" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("address.stateId_selected-text");
          expect(node).toHaveTextContent(client.address.stateName);
        });
      });

      it('"City" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("address.city_field-input");
          expect(node).toHaveValue(client.address.city);
        });
      });

      it('"Zip Code" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("address.zip_field-input");
          expect(node).toHaveValue(client.address.zip);
        });
      });

      it('"Street" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("address.street_field-input");
          expect(node).toHaveValue(client.address.street);
        });
      });

      it('"User Photo" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("avatar_field-selected-file");

          if (client.avatarName) {
            expect(node).toHaveTextContent(client.avatarName);
          } else {
            expect(node).toHaveTextContent("File not chosen");
          }
        });
      });
    });

    describe("Community:", function () {
      it('"Organization" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("organizationId_selected-text");
          expect(node).toHaveTextContent(client.organization);
        });
      });

      it('"Community" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("community_field-input");
          expect(node).toHaveValue(client.community);
        });
      });

      it("Unit #", async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("unit_field-input");
          expect(node).toHaveValue(client.unit);
        });
      });
    });

    describe("Telecom", function () {
      it('"Cell Phone" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("cellPhone_field-input");
          expect(node).toHaveValue("+" + client.cellPhone);
        });
      });

      it('"Home Phone" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("phone_field-input");
          expect(node).toHaveValue("+" + client.phone);
        });
      });

      it('"Email" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("email_field-input");
          expect(node).toHaveValue(client.email);
        });
      });

      it('"Client doesn\'t have email" field initialized correctly', async () => {
        let { queryByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = queryByTestId("hasNoEmail_field-check-mark");

          if (!client.email) {
            expect(node).toBeInTheDocument();
            expect(node).toBeVisible();
          } else {
            expect(node).toBeNull();
          }
        });
      });
    });

    describe("Insurance", function () {
      xit('"Network" field initialized correctly', async () => {
        let { queryByTestId } = renderAndConfigure({ clientId, organizationId });

        const network = findWhere(Network2, { id: client.insurances[0].networkId });

        await waitFor(() => {
          const node = queryByTestId("insurances.0.networkId_selected-text");

          if (client.insurances[0].networkId) {
            expect(node).toHaveTextContent(network.title);
          } else {
            expect(node).toBeNull();
          }
        });
      });

      it('"Plan" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("insurances.0.paymentPlan_field-input");
          expect(node).toHaveValue(client.insurances[0].paymentPlan);
        });
      });

      it('"Group Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("insurances.0.groupNumber_field-input");
          expect(node).toHaveValue(client.insurances[0].groupNumber);
        });
      });

      it('"Member Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("insurances.0.memberNumber_field-input");
          expect(node).toHaveValue(client.insurances[0].memberNumber);
        });
      });

      it('"Medicare Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("medicareNumber_field-input");
          expect(node).toHaveValue(client.medicareNumber);
        });
      });

      it('"Medicaid Number" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("medicaidNumber_field-input");
          expect(node).toHaveValue(client.medicaidNumber);
        });
      });

      it('"Add Authorization" field initialized correctly', async () => {
        let { queryByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = queryByTestId("hasInsuranceAuthorizations_field-check-mark");

          if (isEmpty(client.insuranceAuthorizations)) {
            expect(node).toBeNull();
          } else {
            expect(node).toBeInTheDocument();
            expect(node).toBeVisible();
          }
        });
      });

      describe("Authorization Section:", function () {
        if (isNotEmpty(client.insuranceAuthorizations)) {
          for (let i = 0; i < client.insuranceAuthorizations.length; i++) {
            const authorization = client.insuranceAuthorizations[i];

            describe(`Authorization ${i}:`, function () {
              it('"Start Date" field initialized correctly', async function () {
                let { getByTestId } = renderAndConfigure({ clientId, organizationId });

                await waitFor(() => {
                  const node = getByTestId(`insuranceAuthorizations.${i}.startDate_field-input`);
                  expect(node).toHaveValue(format(authorization.startDate, formats.longDateMediumTime12TimeZone));
                });
              });

              it('"End Date" field initialized correctly', async function () {
                let { getByTestId } = renderAndConfigure({ clientId, organizationId });

                await waitFor(() => {
                  const node = getByTestId(`insuranceAuthorizations.${i}.endDate_field-input`);
                  expect(node).toHaveValue(format(authorization.endDate, formats.longDateMediumTime12TimeZone));
                });
              });

              it('"Authorization Number" field initialized correctly', async function () {
                let { getByTestId } = renderAndConfigure({ clientId, organizationId });

                await waitFor(() => {
                  const node = getByTestId(`insuranceAuthorizations.${i}.number_field-input`);
                  expect(node).toHaveValue(format(authorization.endDate, formats.longDateMediumTime12TimeZone));
                });
              });
            });
          }
        }
      });
    });

    describe("Ancillary Information", function () {
      it('"Retained" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("retained_field-true-check-mark");

          if (client.retained) {
            expect(node).toHaveClass("Radio-CheckMark_checked");
          } else {
            expect(node).not.toHaveClass("Radio-CheckMark_checked");
          }
        });

        await waitFor(() => {
          const node = getByTestId("retained_field-false-check-mark");

          if (client.retained === false) {
            expect(node).toHaveClass("Radio-CheckMark_checked");
          } else {
            expect(node).not.toHaveClass("Radio-CheckMark_checked");
          }
        });
      });

      it('"PCP First Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("primaryCarePhysicianFirstName_field-input");
          expect(node).toHaveValue(client.primaryCarePhysicianFirstName);
        });
      });

      it('"PCP Last Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("primaryCarePhysicianLastName_field-input");
          expect(node).toHaveValue(client.primaryCarePhysicianLastName);
        });
      });

      it('"Intake Date" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("intakeDate_field-input");

          if (client.intakeDate) {
            const formatted = moment(client.intakeDate).format("MM/DD/YYYY hh:mm A [GMT]Z");

            const visibleDate = node.value;

            expect(formatted.substring(0, 23) === visibleDate?.substring(0, 23)).toBeTruthy();
          } else {
            expect(node).toHaveValue("");
          }
        });
      });

      it('"Current Pharmacy Name" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("currentPharmacyName_field-input");
          expect(node).toHaveValue(client.currentPharmacyName);
        });
      });

      it('"Referral Source" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("referralSource_field-input");
          expect(node).toHaveValue(client.referralSource);
        });
      });

      it('"Risk score" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("riskScore_field-input");
          expect(node).toHaveValue(client.riskScore);
        });
      });
    });

    describe("Primary contact", function () {
      it('"Primary contact" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("primaryContact.typeName_field-SELF-check-mark");

          if (client.primaryContact?.typeName === "SELF") {
            expect(node).toHaveClass("Radio-CheckMark_checked");
          } else {
            expect(node).not.toHaveClass("Radio-CheckMark_checked");
          }
        });

        await waitFor(() => {
          const node = getByTestId("primaryContact.typeName_field-CARE_TEAM_MEMBER-check-mark");

          if (client.primaryContact?.typeName === "CARE_TEAM_MEMBER") {
            expect(node).toHaveClass("Radio-CheckMark_checked");
          } else {
            expect(node).not.toHaveClass("Radio-CheckMark_checked");
          }
        });
      });

      it('"Primary notification method" field initialized correctly', async () => {
        let { getByTestId } = renderAndConfigure({ clientId, organizationId });

        await waitFor(() => {
          const node = getByTestId("primaryContact.notificationMethodName_field-EMAIL-check-mark");
          if (client.primaryContact?.notificationMethodName === "EMAIL") {
            expect(node).toHaveClass("Radio-CheckMark_checked");
          } else {
            expect(node).not.toHaveClass("Radio-CheckMark_checked");
          }
        });

        await waitFor(() => {
          const node = getByTestId("primaryContact.notificationMethodName_field-PHONE-check-mark");
          if (client.primaryContact?.notificationMethodName === "PHONE") {
            expect(node).toHaveClass("Radio-CheckMark_checked");
          } else {
            expect(node).not.toHaveClass("Radio-CheckMark_checked");
          }
        });
      });
    });
  });

  afterAll(() => {
    server.close();
  });
});
