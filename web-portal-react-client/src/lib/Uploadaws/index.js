import shortId from "short-uuid";
import { dateYMD, xAmzDate } from "./Date";
import { throwUploadError } from "./ErrorThrower";
import GetUrl from "./Url";
import Policy from "./Policy";
import Signature from "./Signature";
import AWS from "aws-sdk";
import axios from "axios";

class ReactS3Client {
  constructor(config) {
    this.config = config;
    this.axiosInstance = axios.create();

    this.axiosInstance.interceptors.request.use((config) => {
      config.headers["Content-Type"] = "multipart/form-data";
      return config;
    });
  }

  async uploadFile(file, progressCallback, newFileName) {
    throwUploadError(this.config, file);
    let fileExtension = "";
    const fd = new FormData();

    if (file.name) {
      fileExtension = file.name.split(".").pop() || "";
    }

    if (!fileExtension && file.type !== null) {
      fileExtension = file.type.split("/").pop() || "";
    }

    const fileName = `${newFileName || shortId.generate()}${fileExtension && "." + fileExtension}`;
    const dirName = (this.config.dirName ? this.config.dirName + "/" : "").replace(/([^:]\/)\/+/g, "$1");
    const key = `${dirName}${fileName}`;
    const url = GetUrl(this.config);

    fd.append("key", key);
    fd.append("acl", "public-read");
    fd.append("Content-Type", file.type);
    fd.append("x-amz-meta-uuid", "14365123651274");
    fd.append("x-amz-server-side-encryption", "AES256");
    fd.append("X-Amz-Credential", `${this.config.accessKeyId}/${dateYMD}/${this.config.region}/s3/aws4_request`);
    fd.append("X-Amz-Algorithm", "AWS4-HMAC-SHA256");
    fd.append("X-Amz-Date", xAmzDate);
    fd.append("x-amz-meta-tag", "");
    fd.append("Policy", Policy.getPolicy(this.config));
    fd.append("X-Amz-Signature", Signature.getSignature(this.config, dateYMD, Policy.getPolicy(this.config)));
    fd.append("file", file);

    this.cancelTokenSource = axios.CancelToken.source();

    try {
      const response = await axios.post(url, fd, {
        onUploadProgress: (progressEvent) => {
          const percentage =
            progressEvent.loaded && progressEvent.total
              ? Math.round((progressEvent.loaded * 100) / progressEvent.total)
              : 0;
          if (progressCallback) {
            progressCallback(percentage);
          }
        },
        cancelToken: this.cancelTokenSource.token,
      });

      return {
        status: response.status,
        bucket: this.config.bucketName,
        key: `${this.config.dirName ? this.config.dirName + "/" : ""}${fileName}`,
        location: `${url}/${this.config.dirName ? this.config.dirName + "/" : ""}${fileName}`,
      };
    } catch (e) {
      return Promise.reject(e);
    }
  }

  cancelUpload() {
    if (this.cancelTokenSource) {
      this.cancelTokenSource.cancel();
    }
  }

  async deleteFile(key) {
    const awsConfig = {
      region: this.config.region,
      accessKeyId: this.config.accessKeyId,
      secretAccessKey: this.config.secretAccessKey,
    };
    AWS.config.update(awsConfig);

    const s3 = new AWS.S3({
      apiVersion: "2006-03-01",
      params: {
        Bucket: this.config.bucketName,
      },
    });

    return new Promise((resolve, reject) => {
      s3.deleteObject({ Bucket: this.config.bucketName, Key: key }, (err, data) => {
        if (err) {
          reject(err);
        } else {
          resolve({
            message: "File deleted",
            key,
            data,
          });
        }
      });
    });
  }

  async listFiles() {
    const awsConfig = {
      region: this.config.region,
      accessKeyId: this.config.accessKeyId,
      secretAccessKey: this.config.secretAccessKey,
    };
    AWS.config.update(awsConfig);

    const s3 = new AWS.S3({
      apiVersion: "2006-03-01",
      params: {
        Bucket: this.config.bucketName,
      },
    });
    const url = GetUrl(this.config);

    try {
      const req = await s3.listObjects({ Bucket: this.config.bucketName }).promise();

      if (req.$response.error) {
        return Promise.reject({
          err: req.$response.error.name,
          errMessage: req.$response.error.message,
          data: req.$response.error,
        });
      }

      if (!req.$response.data) {
        return Promise.reject({
          err: "Something went wrong!",
          errMessage: "Unknown error occured. Please try again",
          data: null,
        });
      }

      return {
        message: "Objects listed successfully",
        data: {
          ...req.$response.data,
          Contents: req.$response.data.Contents.map((e) => ({ ...e, publicUrl: `${url}/${e.Key}` })),
        },
      };
    } catch (err) {
      return Promise.reject({
        err: "Something went wrong!",
        errMessage: "Unknown error occured. Please try again",
        data: err,
      });
    }
  }
}

// module.exports = ReactS3Client;

export default ReactS3Client;
