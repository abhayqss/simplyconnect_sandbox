import ReactS3Client from "../../../lib/Uploadaws";

// 通用文件上传（支持图片和音频）
/**
 * 文件上传（支持图片和音频，入口可指定 fileType 区分错误提示）
 * @param {*} file
 * @param {*} onProgress
 * @param {*} awsConfig
 * @param {'image'|'audio'} fileType // 可选，指定入口类型，错误提示区分
 * @returns
 */
export function uploadFileToS3(file, onProgress, awsConfig, fileType = undefined) {
  // Supported image and audio types
  const imageTypes = ["image/png", "image/jpeg", "image/gif", "image/webp", "image/svg+xml"];
  const audioTypes = ["audio/mpeg", "audio/wav", "audio/x-m4a", "audio/mp3"];

  const isImage = imageTypes.includes(file.type);
  const isAudio = audioTypes.includes(file.type);

  // English error messages for upload
  if (fileType === "image" && !isImage) {
    return Promise.reject(new Error("Only image files (PNG/JPG/JPEG/GIF/WEBP/SVG) are allowed."));
  }
  if (fileType === "audio" && !isAudio) {
    return Promise.reject(new Error("Only audio files (MP3/WAV/M4A) are allowed."));
  }
  if (!isImage && !isAudio) {
    return Promise.reject(
      new Error("Only image (PNG/JPG/JPEG/GIF/WEBP/SVG) and audio (MP3/WAV/M4A) formats are supported."),
    );
  }

  // Size check: 2MB for image, 10MB for audio
  const maxImageSize = 2 * 1024 * 1024; // 2MB
  const maxAudioSize = 10 * 1024 * 1024; // 10MB

  if (isImage && file.size > maxImageSize) {
    return Promise.reject(new Error("Image size cannot exceed 2MB."));
  }
  if (isAudio && file.size > maxAudioSize) {
    return Promise.reject(new Error("Audio size cannot exceed 10MB."));
  }

  const S3 = new ReactS3Client({
    bucketName: awsConfig.bucketName,
    region: awsConfig.region,
    accessKeyId: awsConfig.accessKeyId,
    secretAccessKey: awsConfig.secretAccessKey,
  });

  return S3.uploadFile(file, onProgress);
}
