const throwError = (config) => {
  if (config.bucketName === null || config.bucketName === "") {
    throw new Error(`Your bucketName cannot be empty`);
  }
  if (config.region === null || config.region === "") {
    throw new Error(`Must provide a valid region in order to use your bucket`);
  }
  if (config.accessKeyId === null || config.accessKeyId === "") {
    throw new Error(`Must provide accessKeyId`);
  }
  if (config.secretAccessKey === null || config.secretAccessKey === "") {
    throw new Error(`Must provide secretAccessKey`);
  }
};

const throwUploadError = (config, file) => {
  throwError(config);

  if (!file) {
    throw new Error(`File cannot be empty`);
  }
};

module.exports = {
  throwError,
  throwUploadError,
};
