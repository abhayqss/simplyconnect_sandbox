import React, { useRef, useState } from "react";
import { Loader2 } from "lucide-react";

const AudioWithLoading = ({ src, ...props }) => {
  const [loading, setLoading] = useState(true);
  const audioRef = useRef(null);

  return (
    <div style={{ position: "relative", minHeight: 40, minWidth: 120 }}>
      {loading && (
        <div
          style={{
            position: "absolute",
            left: 0,
            top: 0,
            right: 0,
            bottom: 0,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            background: "rgba(255,255,255,0.6)",
            zIndex: 2,
          }}
        >
          <Loader2 className="animate-spin" size={22} color="#888" />
          <span style={{ marginLeft: 8, color: "#888" }}>Audio is loading...</span>
        </div>
      )}
      <audio
        ref={audioRef}
        src={src}
        controls
        style={{ maxWidth: 500, opacity: loading ? 0.6 : 1 }}
        onCanPlay={() => setLoading(false)}
        onLoadedData={() => setLoading(false)}
        {...props}
      />
    </div>
  );
};

export default AudioWithLoading;
