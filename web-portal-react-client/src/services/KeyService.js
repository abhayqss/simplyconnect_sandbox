import BaseService from "./BaseService";

/**
 * KeyService handles fetching of configuration keys.
 * Specifically, it provides methods to fetch Docseal related keys.
 */
export class KeyService extends BaseService {
  /**
   * Fetches the value of a specific Docseal key.
   *
   * @param {string} key - The configuration key to fetch. Supported: "docseal_key", "docseal_secret", "docseal_api_key".
   * @returns {Promise<Object>} Promise resolving to a key-value pair object, e.g., { docseal_key: "some-value" }
   * @example
   *   keyService.getConfigurationKey('docseal_key').then(keyObj => { ... })
   */
  getConfigurationKey(key) {
    return super.request({
      method: "GET",
      url: `/configurationKeys/${key}`,
    });
  }

  /**
   * Fetches all Docseal related configuration keys.
   *
   * @returns {Promise<Object>} Promise resolving to an object containing all Docseal keys, for example:
   *   {
   *     docseal_key: "key",
   *     docseal_secret: "secret",
   *     docseal_api_key: "api_key"
   *   }
   * @example
   *   keyService.getDocsealKeys().then(keys => { ... })
   */
  getDocsealKeys() {
    return super.request({
      method: "GET",
      url: `/configurationKeys/keys`,
    });
  }
}

// Optional: export an instance for convenience
const keyService = new KeyService();
export default keyService;
