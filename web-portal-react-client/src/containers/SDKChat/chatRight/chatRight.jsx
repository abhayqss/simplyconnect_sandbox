import ChatRightBottom from "./chatRightBottom";
import ChatRightHeader from "./chatRightHeader";
import ChatRightContent from "./chatRightContent";
import "./chatRight.scss";

const ChatRight = ({ toggleMultipleParticipantPicker, isMultipleParticipantPickerOpen, onBackToList, isMobile }) => {
  return (
    <div className="chatRight">
      <ChatRightHeader
        toggleMultipleParticipantPicker={toggleMultipleParticipantPicker}
        isMultipleParticipantPickerOpen={isMultipleParticipantPickerOpen}
        onBackToList={onBackToList}
        isMobile={isMobile}
      />
      <ChatRightContent />
      <ChatRightBottom />
    </div>
  );
};

export default ChatRight;
