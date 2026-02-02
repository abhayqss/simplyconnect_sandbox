import { escapeRegExp } from "lodash";

export function highlightText(text, searchTerm) {
  if (!searchTerm) return text;
  const regex = new RegExp(`(${escapeRegExp(searchTerm)})`, "gi");
  return text.replace(regex, "<mark>$1</mark>");
}
