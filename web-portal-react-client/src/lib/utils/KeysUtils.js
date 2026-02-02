import CryptoJS from "crypto-js";

/**
 * 将 docsealKeys 数组转换为对象
 * @param {Array} docsealKeysArr - docseal keys 数组
 * @returns {Object} - 转换后的键值对对象
 */
export function docsealKeysToObject(docsealKeysArr) {
  if (!Array.isArray(docsealKeysArr)) return {};
  return Object.fromEntries(docsealKeysArr.map((i) => [i.key, i.value]));
}

/**
 * AES 解密函数
 * @param {string} encryptedValue - 加密的值
 * @param {string} key - 解密密钥，默认使用环境变量
 * @returns {string} - 解密后的字符串
 */
export function decryptAES(encryptedValue, key = process.env.REACT_APP_PRIVATE_KEY) {
  try {
    const bytes = CryptoJS.AES.decrypt(encryptedValue, CryptoJS.enc.Utf8.parse(key), {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7,
    });
    return bytes.toString(CryptoJS.enc.Utf8);
  } catch (e) {
    console.error("Decrypt error:", e);
    return "";
  }
}

/**
 * 创建解密后的 S3 配置对象
 * @param {Object} docsealKeysObj - docseal keys 对象
 * @returns {Object} - S3 配置对象
 */
export function createS3Config(docsealKeysObj) {
  return {
    bucketName: process.env.REACT_APP_S3_BUCKETNAME,
    region: process.env.REACT_APP_S3_REGION,
    accessKeyId: decryptAES(docsealKeysObj.docseal_key),
    secretAccessKey: decryptAES(docsealKeysObj.docseal_secret),
  };
}

/**
 * 处理 docseal keys 并创建 S3 配置
 * @param {Array} docsealKeysArr - docseal keys 数组
 * @returns {Object} - S3 配置对象
 */
export function processDocsealKeysForS3(docsealKeysArr) {
  const docsealKeysObj = docsealKeysToObject(docsealKeysArr);
  return createS3Config(docsealKeysObj);
}

/**
 * 测试解密函数 - 用于调试
 * @param {Object} docsealKeysObj - docseal keys 对象
 * @returns {Object} - 包含解密状态的测试结果
 */
export function testDecryption(docsealKeysObj) {
  const result = {
    hasDocsealKeysObj: !!docsealKeysObj,
    hasDocsealKey: !!docsealKeysObj?.docseal_key,
    hasDocsealSecret: !!docsealKeysObj?.docseal_secret,
    hasAESKey: !!process.env.REACT_APP_PRIVATE_KEY,
    decryptedAccessKey: null,
    decryptedSecretKey: null,
    decryptionSuccess: false,
  };

  if (result.hasDocsealKeysObj && result.hasDocsealKey && result.hasDocsealSecret && result.hasAESKey) {
    try {
      result.decryptedAccessKey = decryptAES(docsealKeysObj.docseal_key);
      result.decryptedSecretKey = decryptAES(docsealKeysObj.docseal_secret);
      result.decryptionSuccess = !!(result.decryptedAccessKey && result.decryptedSecretKey);
    } catch (error) {
      console.error("Decryption test error:", error);
      result.decryptionError = error.message;
    }
  }

  console.log("Decryption test results:", result);
  return result;
}
