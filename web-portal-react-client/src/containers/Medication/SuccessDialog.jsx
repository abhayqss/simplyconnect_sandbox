import React, { useState, useEffect } from "react";
import successimg from "images/success2.svg";
import "./index.scss";

const SuccessDialog = ({ shouldShow, setShowDialog }) => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (shouldShow) {
      setIsVisible(true);
      const timer = setTimeout(() => {
        setIsVisible(false);
        setShowDialog(false);
      }, 3000); // 3秒后关闭对话框

      return () => clearTimeout(timer); // 清除定时器
    }
  }, [shouldShow]);

  if (!isVisible) {
    return null;
  }

  return (
    <div className="successDialog">
      <img src={successimg} alt="" />
      <div className="successMessageWrap">
        <div className="successMessageTitle">Thank you for your reply</div>
        <div className="successMessageInfo">Your response has been logged in the Simply Connect platform.</div>
      </div>
    </div>
  );
};

export default SuccessDialog;
