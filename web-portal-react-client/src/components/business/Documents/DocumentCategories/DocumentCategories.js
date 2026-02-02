import React, { memo, useState } from "react";

import cn from "classnames";
import PTypes from "prop-types";

import { map } from "lib/utils/ArrayUtils";

import DocumentCategory from "../DocumentCategory/DocumentCategory";

import "./DocumentCategories.scss";

function DocumentCategories({ data }) {
  const [isCollapsed, setCollapsed] = useState(true);

  const hasMoreThanOne = data?.length > 1;
  const list = hasMoreThanOne && isCollapsed ? data.slice(0, 1) : data;

  function toggle() {
    setCollapsed((v) => !v);
  }

  return (
    <div className="DocumentCategories">
      <div className={cn(hasMoreThanOne && "padding-right-55")}>
        {map(list, (o) => (
          <DocumentCategory {...o} key={o.id} />
        ))}
      </div>

      {hasMoreThanOne && isCollapsed && (
        <span onClick={toggle} className="DocumentCategories-Button">
          +More {data.length - 1}
        </span>
      )}

      {hasMoreThanOne && !isCollapsed && (
        <span onClick={toggle} className="DocumentCategories-Button">
          Close
        </span>
      )}
    </div>
  );
}

DocumentCategories.propTypes = {
  data: PTypes.arrayOf(PTypes.object),
};

export default memo(DocumentCategories);
