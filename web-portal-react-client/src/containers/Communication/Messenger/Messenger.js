import React, { memo } from "react";

import cn from "classnames";

import { useAuthUser } from "hooks/common";

import { Loader } from "components";

import "./Messenger.scss";
import SdkChat from "../../SDKChat/sdkChat";

function Messenger({ className }) {
  const user = useAuthUser();

  let isLoading = !user;

  if (isLoading) return <Loader />;

  return (
    <div className={cn("Messenger", className)}>
      <SdkChat />
    </div>
  );
}

export default memo(Messenger);
