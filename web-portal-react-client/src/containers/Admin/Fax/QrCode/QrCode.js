import React, { useEffect } from "react";
import Modal from "../../../../components/Modal/Modal";
import { Button, } from 'reactstrap'
import './QrCode.scss'
import { useDownloadingStatusInfoToast } from "../../../../hooks/common";
import { useBoundActions } from "../../../../hooks/common/redux";
import detailsActions from "../../../../redux/client/document/details/clientDocumentDetailsActions";
import { useParams } from "react-router-dom";
import { featBuildingQrCode } from "../../../../redux/QrCode/QrcodeActions";
import { useDispatch, useSelector } from "react-redux";

function QrCode(props) {
  const params = useParams()
  const dispatch = useDispatch();

  const { buildingQrCode } = useSelector(state => state.Qrcode);

  const clientId = parseInt(params.assId)

  const {
    isOpen, QrTitle, showQrModalFc, format, mimeType, id
  } = props;


  const { download } = useBoundActions(detailsActions)

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
    link.setAttribute('download', 'image.jpg');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  const onDownload = () => {
    downloadBase64Image(buildingQrCode);
  }


  useEffect(() => {
      dispatch(featBuildingQrCode(clientId));
  }, [clientId]);

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
          <img src={`data:image/png;base64,${buildingQrCode}`} alt=""/>
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
  )
}


export default QrCode;
