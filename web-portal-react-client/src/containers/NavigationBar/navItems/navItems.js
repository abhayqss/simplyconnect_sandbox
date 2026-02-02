import { any } from "underscore";

const NAV_ITEMS = [
  // { name: "PAPERLESS_HEALTHCARE", title: "Paperless Healthcare", href: "/paperless-healthcare", isHidden: true },
  // { name: 'DASHBOARD', title: 'Dashboard', href: '/dashboard', isHidden: true },
  { name: "RECORD_SEARCH", title: "Record Search", href: "/record-search" },
  { name: "PROSPECTS", title: "Prospects", href: "/prospects" },
  { name: "CLIENTS", title: "Clients", href: "/clients" },
  { name: "NOTIFY", title: "Notify", href: "/notify" },
  { name: "EVENTS", title: "Events", href: "/cl/workflow" },
  { name: "ADMIN_EVENTS", title: "Events", href: "/admin-events" },
  {
    name: "QA_EVENTS",
    title: "Events",
    href: "/qa/events/qa",
    isActive: (path) => any(["/qa/events/qa", "/qa/events/admin", "/qa/feedback"], (o) => path.includes(o)),
  },
  { name: "CHATS", title: "Chats", href: "/chats" },
  { name: "DOCUMENTS", title: "Company Documents", href: "/company-documents" },
  { name: "APPOINTMENTS", title: "Appointments", href: "/appointments" },
  { name: "INCIDENT_REPORTS", title: "Incidents", href: "/incident-reports" },
  { name: "LABS", title: "Labs", href: "/labs" },
  {
    name: "EXTERNAL_PROVIDER_REFERRALS",
    title: "Referrals and Inquiries",
    href: "/external-provider/inbound-referrals",
  },
  {
    name: "REPORTS",
    title: "Reports",
    href: "/reports",
    isActive: (path) => any(["/reports", "/sdoh/reports"], (o) => path.includes(o)),
  },
  {
    name: "REFERRALS",
    title: "Referrals and Inquiries",
    href: "/inbound-referrals",
    isActive: (path) => any(["/inbound-referrals", "/outbound-referrals"], (o) => path.includes(o)),
  },
  { name: "MARKETPLACE", title: "Marketplace", href: "/marketplace" },
  {
    name: "ADMIN",
    title: "Admin",
    href: "/admin/organizations",
    isActive: (path) =>
      any(["/admin/organizations", "/admin/contacts", "/admin/vendors", "/admin/associations", "/admin/fax"], (o) =>
        path.includes(o),
      ),
  },
  { name: "VENDOR_ADMIN", title: "Admin", href: "/admin/vendors" },
  { name: "HELP", title: "Help", href: "/help" },
];
const NAV_ITEMS_MAP = NAV_ITEMS.reduce((itemsMap, current) => {
  itemsMap[current.name] = current;

  return itemsMap;
}, {});

export default NAV_ITEMS_MAP;
