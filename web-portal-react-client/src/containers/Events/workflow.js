import { useRouteMatch } from "react-router-dom";
import { MapAllowedRoutes } from "../../routes";
import React, { memo } from "react";
import SideBar from "../SideBar/SideBar";
import "./workflow.css";
import { useAllowedClientRoutes } from "../../hooks/business/client";

function Workflow({ children: routes }) {
  const match = useRouteMatch();

  const allowedRoutes = useAllowedClientRoutes(routes);
  return (
    <div className={"WorkflowBox"}>
      <MapAllowedRoutes routes={allowedRoutes} basePath={match.path} />
    </div>
  );
}

export default memo(Workflow);
