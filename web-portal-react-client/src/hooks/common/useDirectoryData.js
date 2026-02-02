import { useMemo } from "react";

import { useSelector } from "react-redux";

import { each } from "underscore";

import useRefCurrent from "./useRefCurrent";

function useDirectoryData(config) {
  config = useRefCurrent(config);

  const directory = useSelector((state) => state.directory);

  return useMemo(() => {
    const out = {};

    each(config, (path, name) => {
      out[name] = directory.getIn([...path, "list", "dataSource", "data"]) || [];
    });

    return out;
  }, [config, directory]);
}

export default useDirectoryData;
