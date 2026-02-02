import { useEffect, useState } from "react";

import { getA4WidthByHeight } from "lib/utils/Utils";

function useESignDocumentTemplateWidth({ containerRef, margin = 0, maxPercentageOfContainer = 100 }) {
  const [templateWidth, setTemplateWidth] = useState(0);

  function setWidth() {
    const expectedWidth = containerRef.current.clientWidth * maxPercentageOfContainer / 100;
    const containerHeight = containerRef.current.clientHeight;
    const requiredWIdth = getA4WidthByHeight(containerHeight);
    const width = Math.min(requiredWIdth, expectedWidth) - margin;

    setTemplateWidth(width)
  }

  useEffect(setWidth, [])

  return templateWidth;
}

export default useESignDocumentTemplateWidth