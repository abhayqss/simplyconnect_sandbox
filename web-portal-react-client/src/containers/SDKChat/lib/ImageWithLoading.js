import React, { useState, useCallback, useRef, useEffect } from "react";
import { Image, X } from "lucide-react";

const ImageWithLoading = ({ src, maxWidth = 200, maxHeight = 200, onLoad, onError, ...rest }) => {
  const [loaded, setLoaded] = useState(false);
  const [error, setError] = useState(false);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const imgRef = useRef(null);

  const handleImageLoad = useCallback(() => {
    setLoaded(true);
    setLoading(false);
    setError(false);
    // 通知父组件图片已加载，可能需要滚动
    onLoad && onLoad();
  }, [onLoad]);

  const handleImageError = useCallback(() => {
    setError(true);
    setLoading(false);
    setLoaded(false);
    onError && onError();
  }, [onError]);

  const handleRetry = useCallback(() => {
    setError(false);
    setLoading(true);
    setLoaded(false);

    // 创建新的图片对象进行重试
    if (imgRef.current) {
      imgRef.current.src = src + (src.includes("?") ? "&" : "?") + "t=" + Date.now();
    }
  }, [src]);

  const openModal = useCallback(() => {
    if (loaded && !error) {
      setModalOpen(true);
    }
  }, [loaded, error]);

  const closeModal = useCallback(() => {
    setModalOpen(false);
  }, []);

  // 键盘事件处理
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (modalOpen && e.key === "Escape") {
        closeModal();
      }
    };

    if (modalOpen) {
      document.addEventListener("keydown", handleKeyDown);
      document.body.style.overflow = "hidden";
    }

    return () => {
      document.removeEventListener("keydown", handleKeyDown);
      document.body.style.overflow = "auto";
    };
  }, [modalOpen, closeModal]);

  // 容器样式 - 确保有固定的初始尺寸
  const containerStyle = {
    width: maxWidth,
    height: maxHeight,
    position: "relative",
    display: "inline-block",
    backgroundColor: "#f5f5f5",
    borderRadius: 8,
    overflow: "hidden",
    cursor: loaded && !error ? "pointer" : "default",
    flexShrink: 0, // 防止在flex容器中被压缩
  };

  const overlayStyle = {
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#f5f5f5",
    zIndex: 2,
    borderRadius: 8,
  };

  const loadingStyle = {
    ...overlayStyle,
    fontSize: 12,
    color: "#666",
    flexDirection: "column",
    gap: 8,
  };

  const errorStyle = {
    ...overlayStyle,
    fontSize: 12,
    color: "#999",
    flexDirection: "column",
    textAlign: "center",
    padding: 12,
    cursor: "pointer",
  };

  // 模态框样式
  const modalOverlayStyle = {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "rgba(0, 0, 0, 0.9)",
    zIndex: 9999,
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    cursor: "pointer",
  };

  const modalContentStyle = {
    maxWidth: "90vw",
    maxHeight: "90vh",
    position: "relative",
  };

  const modalImageStyle = {
    maxWidth: "100%",
    maxHeight: "100%",
    objectFit: "contain",
    borderRadius: 8,
  };

  const closeButtonStyle = {
    position: "absolute",
    top: 16,
    right: 16,
    width: 40,
    height: 40,
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    border: "none",
    borderRadius: "50%",
    color: "white",
    fontSize: 20,
    cursor: "pointer",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 10001,
  };

  // 加载动画
  const LoadingSpinner = () => (
    <div
      style={{
        width: 20,
        height: 20,
        border: "2px solid #e0e0e0",
        borderTop: "2px solid #4A90E2",
        borderRadius: "50%",
        animation: "spin 1s linear infinite",
      }}
    />
  );

  return (
    <>
      <style>
        {`
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}
      </style>

      <div style={containerStyle} onClick={openModal}>
        {/* 加载状态 */}
        {loading && !error && (
          <div style={loadingStyle}>
            <LoadingSpinner />
            <span>Loading...</span>
          </div>
        )}

        {/* 错误状态 */}
        {error && (
          <div
            style={errorStyle}
            onClick={(e) => {
              e.stopPropagation();
              handleRetry();
            }}
          >
            <Image size={20} style={{ marginBottom: 6 }} />
            <div>Failed to load</div>
            <div style={{ fontSize: 10, color: "#ccc", marginTop: 2 }}>Click to retry</div>
          </div>
        )}

        {/* 预加载图片 */}
        <img
          ref={imgRef}
          src={src}
          alt=""
          style={{ display: "none" }}
          onLoad={handleImageLoad}
          onError={handleImageError}
        />

        {/* 成功加载后显示的缩略图 */}
        {loaded && !error && (
          <img
            src={src}
            alt=""
            style={{
              width: "100%",
              height: "100%",
              objectFit: "cover",
              borderRadius: 8,
              display: "block",
            }}
            {...rest}
          />
        )}
      </div>

      {/* 自定义模态框 */}
      {modalOpen && (
        <div style={modalOverlayStyle} onClick={closeModal}>
          <button style={closeButtonStyle} onClick={closeModal} title="Close">
            <X size={18} />
          </button>
          <div style={modalContentStyle} onClick={(e) => e.stopPropagation()}>
            <img src={src} alt="" style={modalImageStyle} />
          </div>
        </div>
      )}
    </>
  );
};

export default ImageWithLoading;
