import Cropper from "react-easy-crop";
import React, { useEffect, useState } from "react";
import "./imageCrop.scss";
import getCroppedImg from "./cropImage";
import { Button } from "reactstrap";

const ImageCrop = ({ file, onCropComplete, changeCropModelShow, aspect = 1 }) => {
  const [crop, setCrop] = useState({ x: 0, y: 0 });
  const [zoom, setZoom] = useState(1);

  const [image, setImage] = useState(null);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);

  useEffect(() => {
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImage(reader.result);
      };
      reader.readAsDataURL(file);
    }
  }, [file]);

  if (!image) return null;

  const showCroppedImage = async () => {
    try {
      const croppedImage = await getCroppedImg(image, file.name, croppedAreaPixels);
      onCropComplete(croppedImage);
    } catch (e) {
      console.error(e);
    }
  };

  const onCropCompleted = (croppedArea, croppedAreaPixels) => {
    setCroppedAreaPixels(croppedAreaPixels);
  };

  return (
    <div className="image-crop-container">
      <Cropper
        image={image}
        crop={crop}
        zoom={zoom}
        aspect={aspect}
        onCropChange={setCrop}
        onCropComplete={onCropCompleted}
        onZoomChange={setZoom}
        minZoom={0.5}
      />

      <div className="OperationButton">
        <Button
          outline
          color="success"
          onClick={() => {
            changeCropModelShow(false);
          }}
        >
          Cancel
        </Button>

        <Button color="success" onClick={() => showCroppedImage()}>
          Confirm
        </Button>
      </div>
    </div>
  );
};

export default ImageCrop;
