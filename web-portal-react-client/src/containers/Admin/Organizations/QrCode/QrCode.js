import React, { useCallback, useEffect } from "react";
import Modal from "../../../../components/Modal/Modal";
import { Button, } from 'reactstrap'
import './QrCode.scss'
import { useDispatch, useSelector } from "react-redux";
import { featAssociationsList, featBuildingQrCode } from "../../../../redux/QrCode/QrcodeActions";

function QrCode(props) {
  const dispatch = useDispatch();
  const {qrCode,buildingQrCode} = useSelector(state =>state.Qrcode);


  const {
    isOpen, QrTitle, showQrModalFc,organizationId,communityId
  } = props;

  const  downloadBase64Image = (base64Image) => {
    // 将 Base64 字符串转换为 Blob
    const byteCharacters = atob(base64Image);
    const byteNumbers = Array.from(byteCharacters, char => char.charCodeAt(0));
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: 'image/jpeg' }); // 你可以根据实际情况修改 MIME 类型

    // 创建 Blob URL 并触发下载
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `${QrTitle}.jpg`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  const onDownload = () => {
    downloadBase64Image(qrCode);
  }

  useEffect(() => {
    if (organizationId) {
      dispatch(featAssociationsList(organizationId));
    }

    if (communityId) {
      dispatch(featBuildingQrCode(communityId));
    }
  }, []);

  return (
    <>
      <Modal
        isOpen={isOpen}
        className='QrCode'
        hasCloseBtn={false}
        hasFooter={false}
        hasHeader={false}
        title={''}
        bodyClassName={'QrCodeBody'}
      >
        <div className={'QrcodeImg'}>
          <img src={`data:image/png;base64,${organizationId ? qrCode : buildingQrCode}`} alt=""/>
        </div>

        <div className={'QrTitle'}>{QrTitle}</div>


        <div className={'QrButtonBox'}>

          <Button
            color='success'
            outline='success'
            onClick={showQrModalFc}
          >
            Cancel
          </Button>

          <Button color={'success'} onClick={onDownload}>Download</Button>

        </div>


      </Modal>
    </>
  );
}


export default QrCode;
