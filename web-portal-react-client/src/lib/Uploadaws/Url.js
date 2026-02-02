const buildUrl = ({ bucketName, region }) => {
  return `https://${bucketName}.s3.${region}.amazonaws.com`;
};

const getUrl = (config) => {
  if (config.s3Url && config.s3Url !== "") {
    return config.s3Url;
  }

  return buildUrl(config);
};

module.exports = getUrl;
