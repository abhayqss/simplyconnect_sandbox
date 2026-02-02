import React from "react";

import { ReactComponent as List } from "images/list.svg";
import { ReactComponent as Persons } from "images/persons.svg";
import { ReactComponent as Logs } from "images/logs.svg";
import { ReactComponent as Vendor } from "images/vendor.svg";
import { ReactComponent as Association } from "images/association.svg";
import { ReactComponent as FaxList } from "images/fax.svg";
import { useAuthUser } from "../../hooks/common";

export function getSideBarItems(permissions = {}) {
  const { canViewAuditLogs } = permissions;
  const user = useAuthUser();
  const sections = [
    {
      section: {
        title: "ADMINISTRATION",
        items: [
          {
            title: "Organizations",
            name: "ORGANIZATIONS",
            href: "/admin/organizations",
            hintText: "Organization Listing",
            renderIcon: (className) => <List className={className} />,
          },
          {
            title: "Associations",
            name: "ASSOCIATIONS",
            href: "/admin/associations",
            hintText: "Associations Listing",
            renderIcon: (className) => <Association className={className} />,
          },
          {
            title: "Vendors",
            name: "VENDORS",
            href: "/admin/vendors",
            hintText: "Vendors Listing",
            renderIcon: (className) => <Vendor className={className} />,
          },
          /*    {
                  title: 'Bids',
                  name: 'BIDS',
                  href: '/admin/bids',
                  hintText: 'Bids Listing',
                  renderIcon: (className) => <List className={className}/>
              },*/
          {
            title: "Contacts",
            name: "CONTACTS",
            href: "/admin/contacts",
            hintText: "Contact Listing",
            renderIcon: (className) => <Persons className={className} />,
          },
          {
            title: "Fax",
            name: "FAX",
            href: "/admin/fax",
            hintText: "Fax Listing",
            renderIcon: (className) => <FaxList className={className} />,
          },
        ],
      },
    },
  ];

  if (canViewAuditLogs) {
    sections.push({
      section: {
        title: "AUDIT LOGS",
        items: [
          {
            title: "Audit Logs",
            name: "AUDIT_LOGS",
            href: "/admin/audit-logs",
            hintText: "Audit Logs",
            renderIcon: (className) => <Logs className={className} />,
          },
        ],
      },
    });
  }

  // localhost 限制
  if (process.env.REACT_APP_SENTRY_ENVIRONMENT === "localhost") {
    if ((user.roleName === "ROLE_ADMINISTRATOR" && user.id === 53499) || user.roleName === "ROLE_SUPER_ADMINISTRATOR") {
      // 添加 Workflow Library 条目到数组中
      sections[0].section.items.push({
        title: "Workflow Library",
        name: "WORKFLOW_LIBRARY",
        href: "/admin/workflowManagement",
        hintText: "Workflow Listing",
        renderIcon: (className) => <FaxList className={className} />,
      });
    }
  }

  // sandbox 限制
  if (process.env.REACT_APP_SENTRY_ENVIRONMENT === "sandbox") {
    if ((user.roleName === "ROLE_ADMINISTRATOR" && user.id === 79321) || user.roleName === "ROLE_SUPER_ADMINISTRATOR") {
      // 添加 Workflow Library 条目到数组中
      sections[0].section.items.push({
        title: "Workflow Library",
        name: "WORKFLOW_LIBRARY",
        href: "/admin/workflowManagement",
        hintText: "Workflow Listing",
        renderIcon: (className) => <FaxList className={className} />,
      });
    }
  }

  //  stg 环境限制
  if (process.env.REACT_APP_SENTRY_ENVIRONMENT === "staging") {
    if (
      user.roleName === "ROLE_SUPER_ADMINISTRATOR" ||
      (user.roleName === "ROLE_ORGANIZATION_ADMIN_CODE" && user.id === 149222)
    ) {
      sections[0].section.items.push({
        title: "Workflow Library",
        name: "WORKFLOW_LIBRARY",
        href: "/admin/workflowManagement",
        hintText: "Workflow Listing",
        renderIcon: (className) => <FaxList className={className} />,
      });
    }
  }

  // app 限制
  if (process.env.REACT_APP_SENTRY_ENVIRONMENT === "production") {
    if (
      user.roleName === "ROLE_SUPER_ADMINISTRATOR" ||
      (user.roleName === "ROLE_ADMINISTRATOR" && user.id === 165265) ||
      (user.roleName === "ROLE_ADMINISTRATOR" && user.id === 339582) ||
      (user.roleName === "ROLE_ADMINISTRATOR" && user.id === 83190) ||
      // Amanda Schroeder
      (user.roleName === "ROLE_ORGANIZATION_ADMIN_CODE" && user.id === 100461) ||
      //  test 用户
      (user.roleName === "ROLE_ADMINISTRATOR" && user.id === 339738)
    ) {
      sections[0].section.items.push({
        title: "Workflow Library",
        name: "WORKFLOW_LIBRARY",
        href: "/admin/workflowManagement",
        hintText: "Workflow Listing",
        renderIcon: (className) => <FaxList className={className} />,
      });
    }
  }

  return sections;
}
