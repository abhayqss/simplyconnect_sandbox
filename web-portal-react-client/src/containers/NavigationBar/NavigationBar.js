import React, { useCallback, useEffect, useMemo } from "react";

import { isBoolean } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { Link, useHistory, useLocation, withRouter } from "react-router-dom";

import { Collapse, Nav, Navbar, NavbarBrand, NavItem } from "reactstrap";

import { useToggle, useWindowResize } from "hooks/common";

import { useCanViewLabQuery } from "hooks/business/labs";
import { useNewMessageCheck } from "hooks/business/conversations";
import { useCanViewDocumentsQuery } from "hooks/business/documents";
import { useExternalProviderRoleCheck, userIsVendorConcierge } from "hooks/business/external";
import { useCanViewAppointmentsQuery } from "hooks/business/appointments";
import { useCanViewIncidentReports } from "hooks/business/incident-report";
import { useCanViewPaperlessHealthcareQuery } from "hooks/business/paperless-healthcare";

import Logo from "components/Logo/Logo";

import UserNavItem from "./UserNavItem/UserNavItem";
import MobileNavigation from "./MobileNavigation/MobileNavigation";

import LoadCanViewMarketplaceAction from "actions/marketplace/LoadCanViewMarketplaceAction";

import * as canViewReportActions from "redux/report/can/view/canViewReportActions";

import { PROFESSIONAL_SYSTEM_ROLES, SYSTEM_ROLES } from "lib/Constants";

import PrivateRoutesConfig from "routes/config/private";

import { isNotEmpty } from "lib/utils/Utils";

import { path } from "lib/utils/ContextUtils";
import { getAllowedRoutes } from "lib/utils/UrlUtils";

import {
  hiddenNavItems as tabletLandscapeHiddenNavItems,
  navItems as tabletLandscapeNavItems,
} from "./navItems/tabletLandscapeNavItems";

import { hiddenNavItems as laptopHiddenNavItems, navItems as laptopNavItems } from "./navItems/laptopNavItems";

import { hiddenNavItems as fullHDHiddenNavItems, navItems as fullHDNavItems } from "./navItems/fullHDNavItems";

import { ReactComponent as Burger } from "images/mini-burger.svg";
import { ReactComponent as Cross } from "images/close-nav.svg";

import "./NavigationBar.scss";
import useIsVendorAdmin from "../../hooks/business/external/useIsVendorAdmin";
import { GROUPS } from "../../routes/config/Roles";

const {
  PHARMACIST,
  PARENT_GUARDIAN,
  CONTENT_CREATOR,
  PHARMACY_TECHNICIAN,
  QUALITY_ASSURANCE,
  PERSON_RECEIVING_SERVICES,
  SUPER_ADMINISTRATOR,
  ORGANIZATION_ADMIN,
  VENDOR,
  DOCTOR,
  CAREGIVER,
  PREMIUM,
  CLINICIAN,
  PHARMACIST_VENDOR,
  NAVI_GUIDE,
  ADMINISTRATOR,
  BEHAVIORAL_HEALTH,
  COMMUNITY_ADMINISTRATOR,
  VENDOR_CONCIERGE,
  ASSOCIATION,
  NON_CLINICAL_STAFF,
} = SYSTEM_ROLES;

const ACTIVE_NAV_ITEM_LINK_CSS = {
  color: "#ffffff",
  backgroundColor: "#2493e5",
  borderRadius: 2,
  padding: "2px 6px",
};

function NavItems({ items, getItemLinkStyle }) {
  return (
    <>
      {items.map((o) => (
        <NavItem key={o.href} className="Navigation-Item">
          <Link to={path(o.href)} style={getItemLinkStyle(o)} onClick={o.onClick}>
            {o.title}
          </Link>

          {o.hasIndicator && <div className="Navigation-Indicator" />}
        </NavItem>
      ))}
    </>
  );
}

NavItems.defaultProps = {
  items: [],
};

function mapStateToProps(state) {
  const { lab, auth, client, report, incident, prospect, marketplace } = state;

  return {
    auth,
    clientRoute: client.route.value,
    canViewLabs: lab.can.view.value,
    prospectRoute: prospect.route.value,
    canViewReports: report.can.view.value,
    canViewMarketplace: marketplace.can.view.value,
    canViewIncidentReports: incident.report.can.view.value,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      report: {
        can: {
          view: bindActionCreators(canViewReportActions, dispatch),
        },
      },
    },
  };
}

function getNavItemsByScreenWidth(width) {
  switch (true) {
    case width >= 1920:
      return {
        navItems: fullHDNavItems,
        hiddenNavItems: fullHDHiddenNavItems,
      };

    case width >= 1366:
      return {
        navItems: laptopNavItems,
        hiddenNavItems: laptopHiddenNavItems,
      };

    case width > 1024:
      return {
        navItems: tabletLandscapeNavItems,
        hiddenNavItems: tabletLandscapeHiddenNavItems,
      };
    default:
      return {
        navItems: fullHDNavItems,
        hiddenNavItems: fullHDHiddenNavItems,
      };
  }
}

function NavigationBar({
  auth,
  actions,
  clientRoute,
  canViewLabs,
  prospectRoute,
  canViewReports,
  canViewMarketplace,
  canViewIncidentReports,
}) {
  const [_, forceUpdate] = useToggle();
  const [isOpen, toggleOpen] = useToggle(false);

  let history = useHistory();
  let location = useLocation();
  let user = auth.login.user.data;

  const { hasNewMessages } = useNewMessageCheck();

  const isExtProvider = useExternalProviderRoleCheck();
  const isProfessionalRole = !!PROFESSIONAL_SYSTEM_ROLES.includes(user?.roleName);
  const isContentCreatorRole = user?.roleName === CONTENT_CREATOR;
  const isVendorConcierge = userIsVendorConcierge();
  const isVendorAdmin = useIsVendorAdmin();

  const canViewProspects = false;

  /*const {
        data: canViewProspects = true
    } = useCanViewProspectsQuery({}, { staleTime: 0 })*/

  const { data: canViewDocuments = false } = useCanViewDocumentsQuery({}, { staleTime: 0 });

  const { data: canViewPaperlessHealthcare = false } = useCanViewPaperlessHealthcareQuery({}, { staleTime: 0 });

  const { data: canViewAppointments = false } = useCanViewAppointmentsQuery({}, { staleTime: 0 });

  const allowedRoutes = useMemo(() => getAllowedRoutes(PrivateRoutesConfig), []);
  const {
    PROFESSIONALS_CARE_MANAGEMENT,
    PROFESSIONALS_OTHER,
    PHARMACY,
    NON_PROFESSIONALS,
    PROFESSIONALS_PRIMARY_PHYSICIAN,
  } = GROUPS;
  const permissions = useMemo(
    () => ({
      // PAPERLESS_HEALTHCARE: canViewPaperlessHealthcare,
      RECORD_SEARCH: !(isExtProvider || user?.roleName === PERSON_RECEIVING_SERVICES),
      REFERRALS: !isExtProvider && isProfessionalRole && ![PHARMACIST, PHARMACY_TECHNICIAN].includes(user?.roleName),
      NOTIFY: false,
      EVENTS: ![
        SUPER_ADMINISTRATOR,
        ORGANIZATION_ADMIN,
        VENDOR,
        ASSOCIATION,
        NON_CLINICAL_STAFF,
        DOCTOR,
        CAREGIVER,
        PREMIUM,
        CLINICIAN,
        PHARMACIST_VENDOR,
        BEHAVIORAL_HEALTH,
        NAVI_GUIDE,
        ADMINISTRATOR,
        COMMUNITY_ADMINISTRATOR,
      ].includes(user?.roleName),
      ADMIN_EVENTS: [
        SUPER_ADMINISTRATOR,
        ORGANIZATION_ADMIN,
        VENDOR,
        DOCTOR,
        CAREGIVER,
        PREMIUM,
        CLINICIAN,
        PHARMACIST_VENDOR,
        BEHAVIORAL_HEALTH,
        NAVI_GUIDE,
        ADMINISTRATOR,
        COMMUNITY_ADMINISTRATOR,
        ...PROFESSIONALS_CARE_MANAGEMENT,
        ...PROFESSIONALS_OTHER,
        ...PHARMACY,
        ...NON_PROFESSIONALS,
        ...PROFESSIONALS_PRIMARY_PHYSICIAN,
      ].includes(user?.roleName),
      QA_EVENTS: [QUALITY_ASSURANCE].includes(user?.roleName),
      PROSPECTS: canViewProspects,
      DOCUMENTS: canViewDocuments,
      LABS: canViewLabs,
      CHATS: user?.areConversationsEnabled,
      APPOINTMENTS: canViewAppointments,
      REPORTS: canViewReports,
      MARKETPLACE: canViewMarketplace,
      INCIDENT_REPORTS: canViewIncidentReports,
      EXTERNAL_PROVIDER_REFERRALS: isExtProvider,
      ADMIN: (isProfessionalRole || isContentCreatorRole || isVendorConcierge) && !isExtProvider,
      VENDOR_ADMIN: isVendorAdmin,
    }),
    [
      user,
      canViewLabs,
      isExtProvider,
      canViewReports,
      canViewDocuments,
      isProfessionalRole,
      canViewMarketplace,
      canViewAppointments,
      isContentCreatorRole,
      canViewIncidentReports,
      canViewPaperlessHealthcare,
    ],
  );

  const behaviorReducer = useCallback(
    (item) => {
      switch (item.name) {
        case "CHATS":
          return {
            ...item,
            hasIndicator: hasNewMessages,
          };

        case "CLIENTS": {
          return {
            ...item,
            onClick: (event) => {
              const isClientsPath = history.location.pathname.includes("/clients/");

              if (clientRoute && clientRoute !== path("/clients") && !isClientsPath) {
                event.stopPropagation();
                event.preventDefault();

                history.push(clientRoute);
              }
            },
          };
        }

        case "PROSPECTS": {
          return {
            ...item,
            onClick: (event) => {
              const isProspectsPath = history.location.pathname.includes("/prospects/");

              if (prospectRoute && prospectRoute !== path("/prospects") && !isProspectsPath) {
                event.stopPropagation();
                event.preventDefault();

                history.push(prospectRoute);
              }
            },
          };
        }

        default:
          return item;
      }
    },
    [history, clientRoute, prospectRoute, hasNewMessages],
  );

  const isAllowedItem = useCallback(
    (item) => {
      return allowedRoutes.find((route) => item.href.includes(route.path));
    },
    [allowedRoutes],
  );

  const clientWidth = document.documentElement.clientWidth;

  const items = useMemo(() => getNavItemsByScreenWidth(clientWidth), [clientWidth]);

  const visibleItems = useMemo(
    () =>
      items.navItems
        .filter((item) => {
          const isAllowed = permissions[item.name];
          return isBoolean(isAllowed) ? isAllowed : !isExtProvider;
        })
        .filter(isAllowedItem)
        .map(behaviorReducer),
    [items, permissions, behaviorReducer, isAllowedItem, isExtProvider],
  );

  const hiddenItems = useMemo(
    () =>
      items.hiddenNavItems
        .filter((item) => {
          const isAllowed = permissions[item.name];
          return isBoolean(isAllowed) ? isAllowed : !isExtProvider;
        })
        .filter(isAllowedItem)
        .map(behaviorReducer),
    [items, permissions, behaviorReducer, isAllowedItem, isExtProvider],
  );

  for (let i = 0; i < items.navItems.length - visibleItems.length; i++) {
    if (isNotEmpty(hiddenItems)) {
      visibleItems.push(hiddenItems.shift());
    }
  }

  const getLinkStyle = useCallback(
    (o) =>
      (o.isActive ? o.isActive(location.pathname) : location.pathname.includes(path(o.href)))
        ? ACTIVE_NAV_ITEM_LINK_CSS
        : {},
    [location.pathname],
  );

  const fetchCanViewReports = useCallback(() => {
    actions.report.can.view.load();
  }, [actions.report.can.view]);

  useCanViewLabQuery();
  useCanViewIncidentReports(null);

  useWindowResize(forceUpdate, { debounceTime: 200 });

  useEffect(() => {
    if (user !== null) {
      fetchCanViewReports();
    }
  }, [user, fetchCanViewReports]);

  const Toggler = isOpen ? Cross : Burger;

  return (
    <>
      <LoadCanViewMarketplaceAction />

      <Navbar container={false} className="NavigationBar hide-on-mobile">
        <NavbarBrand>
          <Logo iconSize={52} className="NavigationBar-Logo" />
        </NavbarBrand>

        <div className="NavigationBar-Panel">
          {user && (
            <>
              <div className="Navigation-Container">
                <Nav className="Navigation">
                  <NavItems items={visibleItems} getItemLinkStyle={getLinkStyle} />
                  {isNotEmpty(hiddenItems) && <Toggler onClick={toggleOpen} className="Navigation-Toggler" />}
                </Nav>

                <Collapse isOpen={isOpen}>
                  <Nav className="Navigation">
                    <NavItems items={hiddenItems} getItemLinkStyle={getLinkStyle} />
                  </Nav>
                </Collapse>
              </div>
            </>
          )}
        </div>

        <div className="Navigation-UserNavItem">
          <UserNavItem />
        </div>
      </Navbar>

      <MobileNavigation items={visibleItems} />
    </>
  );
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(NavigationBar));
