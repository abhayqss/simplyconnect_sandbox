import "./card.scss";
import { ReactComponent as AddMember } from "images/add-care-member.svg";
import { Button } from "reactstrap";

const NoMemberCard = (props) => {
  const { clickToAdd, className, itemDetail, canAdd } = props;
  return (
    <div className={className}>
      <Button
        className={"card-no-member-avatar"}
        disabled={!canAdd}
        onClick={() => {
          clickToAdd(itemDetail.text);
        }}
      >
        <AddMember className={"add-member-icon"} />
      </Button>
      <div className={"card-role-show"}>{itemDetail?.text}</div>
      <div className={"card-no-member-desc"}>No default {itemDetail?.text} member of the Team yet.</div>
    </div>
  );
};
export default NoMemberCard;
