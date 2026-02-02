import { Modal } from "components";
import React, { useCallback, useEffect, useState, useMemo, useRef } from "react";
import service from "services/EsignService";

import { decryptAES } from "lib/utils/KeysUtils";

import { Button, Progress } from "reactstrap";
import { DropzoneField, TextField } from "components/Form";
import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS } from "lib/Constants";
import { DocusealBuilder } from "@docuseal/react";
import DocumentFolderService from "services/DocumentFolderService";
import "../ESignDocumentTemplateEditor.scss";
import { ErrorDialog } from "../../../../../components/dialogs";
import {
  useESignDocumentTemplateAutofillFieldTypesQuery,
  useESignDocumentTemplateOrgAutofillFieldTypesQuery,
} from "hooks/business/directory/query";
import ReactS3Client from "../../../../../lib/Uploadaws";

// Environment variables constants
const ENV_VARS = {
  AES_KEY: process.env.REACT_APP_PRIVATE_KEY,
  S3_BUCKET_NAME: process.env.REACT_APP_S3_BUCKETNAME,
  S3_REGION: process.env.REACT_APP_S3_REGION,
  DOCSEAL_USER_EMAIL: process.env.REACT_APP_DOCSEAL_USER_EMAIL,
  SENTRY_ENVIRONMENT: process.env.REACT_APP_SENTRY_ENVIRONMENT,
};

/**
 *
 * @param {Object} props
 * @param {Boolean} props.isOpen - 模版是否打开
 * @param {Function} props.onClose - 模版关闭方法
 *
 * @param {String} props.organizationId - 组织ID
 * @param {String} props.communityId - 社区ID
 * @param {Function} props.saveMulTemplateEsign - 保存模版
 *
 * @param {Object} props.editRowData - 编辑模版的数据
 * @param {String} props.editRowData.title - 模版名称 需要有 .pdf 数据
 * @param {String} props.editRowData.documentUrl - 模版URL
 * @param {String} props.editRowData.applicationKey - 模版 唯一标志
 * @param {String} props.editRowData.templateId - 数据库模版id
 *
 * @param {Function} props.setEditMultipleSignaturesRowData - 设置编辑数据 []
 *
 * @return {Element}
 * @constructor
 */
const ESignMultipleSignatures = (props) => {
  const {
    isOpen,
    onClose,
    folderId,
    organizationId,
    communityId,
    saveMulTemplateEsign,

    setEditMultipleSignaturesRowData,
    editRowData,
    docsealKeysObj = {},
    docsealKeysLoading = false,
    docsealKeysError = null,
  } = props;

  console.log(docsealKeysObj, "docsealKeysObj");

  const [esToken, setEstoken] = useState("");

  const [loadFile, setLoadFile] = useState([]);
  const [fileUrl, setFileUrl] = useState("");

  const [uploadError, setUploadError] = useState(null);
  const [showEsign, setShowEsign] = useState(false);
  const [templateName, setTemplateName] = useState("");
  const [nameError, setNameError] = useState("");
  const [esignKey, setEsignKey] = useState("");
  const [percentage, setPercentage] = useState(0);
  const [showPercentage, setShowPercentage] = useState(false);

  const [fileErrorDialog, setFileErrorDialog] = useState("");
  const [EFolderId, setEFolderId] = useState();

  const editRowDataRef = useRef();
  const editRowDataString = editRowData ? JSON.stringify(editRowData) : null;

  useEffect(() => {
    if (!editRowData && isOpen) {
      setEstoken("");
      setFileUrl("");
      setLoadFile([]);
      setUploadError(null);
      setPercentage(0);
      setShowPercentage(false);
      setTemplateName("");
      setShowEsign(false);
      setEsignKey("");
      setFileErrorDialog("");
      setNameError("");
      editRowDataRef.current = null;
    }
  }, [editRowDataString, isOpen]);

  const { data: autofillFieldTypes = [], isFetching: isFetchingAutofillFieldTypes } =
    useESignDocumentTemplateAutofillFieldTypesQuery();

  const { data: autofillOrgFieldTypes = [], isFetching: isFetchingAutofillOrganizationFieldTypes } =
    useESignDocumentTemplateOrgAutofillFieldTypesQuery();

  const filterAutofillOrgFieldTypes = useMemo(() => {
    return autofillOrgFieldTypes.filter((item) => {
      return item.id !== 7 && item.id !== 8;
    });
  }, [autofillOrgFieldTypes]);

  const getFolderId = useCallback((id) => {
    DocumentFolderService.find({
      communityIds: id,
      types: "TEMPLATE",
    })
      .then((res) => {
        const data = res.filter((item) => item.name === "E-sign Documents and Forms");
        setEFolderId(data[0]?.id);
      })
      .catch((error) => {
        console.error("Error fetching folder ID:", error);
      });
  }, []);

  useEffect(() => {
    if (communityId && !EFolderId && isOpen) {
      getFolderId(communityId);
    }
  }, [communityId, getFolderId, EFolderId, isOpen]);

  const { AES_KEY, S3_BUCKET_NAME, S3_REGION, DOCSEAL_USER_EMAIL, SENTRY_ENVIRONMENT } = ENV_VARS;

  // 环境变量检查 - 用于调试本地与线上差异
  useEffect(() => {
    if (isOpen) {
      console.log("=== Environment Variables Check ===");
      console.log("NODE_ENV:", process.env.NODE_ENV);
      console.log("REACT_APP_SENTRY_ENVIRONMENT:", SENTRY_ENVIRONMENT);
      console.log("REACT_APP_PRIVATE_KEY exists:", !!AES_KEY);
      console.log("REACT_APP_S3_BUCKETNAME:", S3_BUCKET_NAME);
      console.log("REACT_APP_S3_REGION:", S3_REGION);
      console.log("REACT_APP_DOCSEAL_USER_EMAIL exists:", !!DOCSEAL_USER_EMAIL);
      console.log("=== docsealKeysObj Check ===");
      console.log("docsealKeysObj exists:", !!docsealKeysObj);
      console.log("docsealKeysObj keys:", docsealKeysObj ? Object.keys(docsealKeysObj) : "N/A");
      console.log("docseal_key exists:", !!docsealKeysObj?.docseal_key);
      console.log("docseal_secret exists:", !!docsealKeysObj?.docseal_secret);
      if (docsealKeysObj?.docseal_key) {
        console.log("docseal_key length:", docsealKeysObj.docseal_key.length);
        console.log("docseal_key starts with:", docsealKeysObj.docseal_key.substring(0, 10) + "...");
      }
      if (docsealKeysObj?.docseal_secret) {
        console.log("docseal_secret length:", docsealKeysObj.docseal_secret.length);
        console.log("docseal_secret starts with:", docsealKeysObj.docseal_secret.substring(0, 10) + "...");
      }
      console.log("=== End Environment Check ===");
    }
  }, [docsealKeysObj, isOpen, AES_KEY]);

  // 内联解密测试 - 用于调试
  useEffect(() => {
    if (docsealKeysObj && isOpen) {
      console.log("=== Inline Decryption Test ===");
      console.log("docseal_key exists:", !!docsealKeysObj?.docseal_key);
      console.log("docseal_secret exists:", !!docsealKeysObj?.docseal_secret);
      console.log("AES_KEY exists:", !!AES_KEY);

      if (docsealKeysObj?.docseal_key && docsealKeysObj?.docseal_secret && AES_KEY) {
        try {
          const decryptedAccessKey = decryptAES(docsealKeysObj.docseal_key, AES_KEY);
          const decryptedSecretKey = decryptAES(docsealKeysObj.docseal_secret, AES_KEY);
          console.log("Decrypted access key exists:", !!decryptedAccessKey);
          console.log("Decrypted secret key exists:", !!decryptedSecretKey);
          console.log("Decryption success:", !!(decryptedAccessKey && decryptedSecretKey));
        } catch (error) {
          console.error("Decryption test error:", error);
        }
      } else {
        console.warn("Missing required data for decryption test");
      }
      console.log("=== End Inline Decryption Test ===");
    }
  }, [docsealKeysObj, isOpen, AES_KEY]);

  const S3 = useMemo(() => {
    console.log("S3 useMemo - Environment:", SENTRY_ENVIRONMENT);
    console.log("S3 useMemo - docsealKeysObj:", docsealKeysObj);
    console.log("S3 useMemo - docsealKeysLoading:", docsealKeysLoading);
    console.log("S3 useMemo - docsealKeysError:", docsealKeysError);
    console.log("S3 useMemo - AES_KEY exists:", !!AES_KEY);
    console.log("S3 useMemo - Bucket name:", S3_BUCKET_NAME);
    console.log("S3 useMemo - Region:", S3_REGION);

    // If still loading, don't create S3 client yet
    if (docsealKeysLoading) {
      console.log("S3 useMemo - Still loading docseal keys");
      return null;
    }

    // If there's an error loading keys
    if (docsealKeysError) {
      console.error("S3 useMemo - Error loading docseal keys:", docsealKeysError);
      return null;
    }

    if (!docsealKeysObj) {
      console.warn("S3 useMemo - docsealKeysObj is null or undefined");
      return null;
    }

    if (!docsealKeysObj.docseal_key) {
      console.warn("S3 useMemo - docseal_key is missing");
      return null;
    }

    if (!docsealKeysObj.docseal_secret) {
      console.warn("S3 useMemo - docseal_secret is missing");
      return null;
    }

    if (!AES_KEY) {
      console.warn("S3 useMemo - AES_KEY is missing");
      return null;
    }

    try {
      const decryptedAccessKey = decryptAES(docsealKeysObj.docseal_key, AES_KEY);
      const decryptedSecretKey = decryptAES(docsealKeysObj.docseal_secret, AES_KEY);

      console.log("S3 useMemo - Decrypted access key exists:", !!decryptedAccessKey);
      console.log("S3 useMemo - Decrypted secret key exists:", !!decryptedSecretKey);

      if (!decryptedAccessKey || !decryptedSecretKey) {
        console.warn("S3 useMemo - Decryption failed, keys are empty");
        return null;
      }

      const config = {
        bucketName: S3_BUCKET_NAME,
        region: S3_REGION,
        accessKeyId: decryptedAccessKey,
        secretAccessKey: decryptedSecretKey,
      };
      console.log("S3 useMemo - Config:", {
        ...config,
        accessKeyId: config.accessKeyId ? `${config.accessKeyId.substring(0, 4)}****` : "empty",
        secretAccessKey: config.secretAccessKey ? "****" : "empty",
      });

      const s3Client = new ReactS3Client(config);
      console.log("S3 useMemo - Client created successfully");
      return s3Client;
    } catch (error) {
      console.error("S3 useMemo - Error creating S3 client:", error);
      return null;
    }
  }, [
    docsealKeysObj?.docseal_key,
    docsealKeysObj?.docseal_secret,
    AES_KEY,
    S3_BUCKET_NAME,
    S3_REGION,
    docsealKeysLoading,
    docsealKeysError,
  ]);

  console.log("S3---->>>>", S3);

  const upFile = useCallback(
    (file) => {
      console.log("upFile - Starting upload process");
      console.log("upFile - S3 client exists:", !!S3);
      console.log("upFile - docsealKeysLoading:", docsealKeysLoading);
      console.log("upFile - File data:", file);

      if (docsealKeysLoading) {
        console.log("upFile - Keys still loading, cannot upload yet");
        setFileErrorDialog("Configuration is still loading. Please wait a moment and try again.");
        return;
      }

      if (docsealKeysError) {
        console.error("upFile - Keys loading error:", docsealKeysError);
        setFileErrorDialog("Failed to load configuration. Please refresh the page and try again.");
        return;
      }

      if (!S3) {
        console.error("upFile - S3 client is null, cannot upload file");
        setFileErrorDialog("File upload service is not available. Please check configuration.");
        return;
      }

      console.log("Uploading file:", file);
      const maxSize = 1024 * 1024 * 20;
      if (file && file.length > 0) {
        if (file[0].type !== "application/pdf") {
          console.warn("upFile - Invalid file type:", file[0].type);
          setFileErrorDialog("Please upload the PDF.");
          return;
        }

        if (file[0].size > maxSize) {
          console.warn("upFile - File too large:", file[0].size);
          setFileErrorDialog("Please upload a file smaller than 20MB.");
          return;
        }

        console.log("upFile - Starting S3 upload");
        S3.uploadFile(file[0], (percentage) => {
          console.log("Upload progress:", percentage);
          setShowPercentage(true);
          setPercentage(percentage);
        })
          .then(
            (res) => {
              console.log("File uploaded successfully:", res);
              setFileUrl(res.location);
              setLoadFile(file);
              setPercentage(100);
              setShowPercentage(false);
            },
            (error) => {
              console.error("upFile - Upload failed (promise rejection):", error);
              setFileErrorDialog("File upload failed. Please try again.");
              setLoadFile([]);
              setFileUrl("");
              setShowPercentage(false);
            },
          )
          .catch((error) => {
            console.error("upFile - Upload error (catch block):", error);
            setUploadError(error);
            setFileErrorDialog("File upload error occurred. Please try again.");
            setPercentage(0);
            setLoadFile([]);
            setFileUrl("");
            setShowPercentage(false);
          });
      } else {
        console.warn("upFile - No file provided or empty file array");
      }
    },
    [S3],
  );

  const { PDF } = ALLOWED_FILE_FORMATS || {};

  const ALLOWED_FILE_MIME_TYPES = useMemo(() => {
    if (PDF && ALLOWED_FILE_FORMAT_MIME_TYPES && ALLOWED_FILE_FORMAT_MIME_TYPES[PDF]) {
      return [ALLOWED_FILE_FORMAT_MIME_TYPES[PDF]];
    }
    return ["application/pdf"];
  }, [PDF]);

  // 清理文件相关状态的函数
  const clearFileState = useCallback(() => {
    setLoadFile([]);
    setFileUrl("");
    setUploadError(null);
    setPercentage(0);
    setShowPercentage(false);
  }, []);

  const onChangeFileField = useCallback(
    (name, value) => {
      // 如果值为空数组或长度为0，说明是删除操作
      if (!value || value.length === 0) {
        clearFileState();
        return;
      }

      // 如果有新文件且当前没有文件，说明是添加操作
      if (value.length > 0 && loadFile.length === 0) {
        upFile(value);
        return;
      }

      // 如果新文件数量小于当前文件数量，说明是删除操作
      if (value.length < loadFile.length) {
        setLoadFile(value);
        // 如果删除后没有文件了，清空相关状态
        if (value.length === 0) {
          clearFileState();
        }
        return;
      }

      // 如果文件数量相同但内容可能不同，或其他情况
      if (value.length > 0) {
        // 检查是否是相同的文件
        const isSameFile =
          loadFile.length > 0 &&
          value[0] &&
          loadFile[0] &&
          value[0].name === loadFile[0].name &&
          value[0].size === loadFile[0].size;

        if (!isSameFile) {
          upFile(value);
        }
      }
    },
    [upFile, loadFile, clearFileState],
  );

  const changeField = useCallback((name, value) => {
    setTemplateName(value);
  }, []);

  /*  const isSingle = useCallback(
      _.debounce((title) => {
        service.queryForDuplicateNames(title).then((res) => {
          if (!res.data) {
            setNameError("");
          } else {
            setNameError("Duplicate name");
          }
        });
      }, 600),
      [],
    );*/
  /*  useEffect(() => {
      if (templateName) {
        // 查重
        // isSingle(templateName);
      }
    }, [templateName, isSingle]);*/

  useEffect(() => {
    if (editRowData && !esToken && editRowDataRef.current !== editRowDataString && isOpen) {
      editRowDataRef.current = editRowDataString;

      const params = {
        userEmail: DOCSEAL_USER_EMAIL,
        integrationEmail: "",
        name: editRowData.title.split(".")[0],
        documentUrls: [editRowData.documentUrl],
        applicationKey: editRowData.applicationKey,
        folderName: SENTRY_ENVIRONMENT,
      };

      setTemplateName(editRowData.title.split(".")[0]);

      service
        .getEsignToken(params)
        .then((res) => {
          setEsignKey(res.data.key);
          setEstoken(res.data.token);
          setShowEsign(true);
        })
        .catch((error) => {
          console.error("Error getting esign token:", error);
        });
    }
  }, [editRowDataString, esToken, isOpen]);

  const esignNext = useCallback(() => {
    let params;

    if (editRowData) {
      params = {
        userEmail: DOCSEAL_USER_EMAIL,
        integrationEmail: "",
        name: editRowData.title.split(".")[0],
        documentUrls: [editRowData.documentUrl],
        applicationKey: editRowData.applicationKey,
        folderName: SENTRY_ENVIRONMENT,
      };

      service.getEsignToken(params).then((res) => {
        setEsignKey(res.data.key);
        setEstoken(res.data.token);
      });
    } else {
      params = {
        userEmail: DOCSEAL_USER_EMAIL,
        integrationEmail: "",
        name: templateName,
        documentUrls: [fileUrl],
        folderName: SENTRY_ENVIRONMENT,
      };

      service.getEsignToken(params).then((res) => {
        setEsignKey(res.data.key);
        setEstoken(res.data.token);
      });
    }
  }, [editRowData, templateName, fileUrl]);

  const saveEsign = useCallback(() => {
    const params = {
      title: templateName,
      documentUrls: [fileUrl],
      organizationId: organizationId,
      communityIds: [communityId],
      configuration: {
        folders: [
          {
            folderId: folderId || EFolderId,
            communityId: communityId,
          },
        ],
      },
      applicationKey: esignKey,
      templateId: editRowData?.templateId,
    };
    saveMulTemplateEsign(params);
  }, [
    templateName,
    fileUrl,
    organizationId,
    communityId,
    folderId,
    EFolderId,
    esignKey,
    editRowData?.templateId,
    saveMulTemplateEsign,
  ]);

  const handleModalClose = useCallback(() => {
    // 清理所有状态
    setEstoken("");
    clearFileState();
    setTemplateName("");
    setShowEsign(false);
    setEsignKey("");
    setFileErrorDialog("");
    setNameError("");
    setEFolderId(null);
    editRowDataRef.current = null;

    setEditMultipleSignaturesRowData(null);
    onClose(false);
    saveEsign();
  }, [setEditMultipleSignaturesRowData, onClose, saveEsign, clearFileState]);

  const handleNextClick = useCallback(() => {
    setShowEsign(true);
    esignNext();
  }, [esignNext]);

  const handleSaveClick = useCallback(() => {
    setEstoken("");
    clearFileState();
    setTemplateName("");
    setEditMultipleSignaturesRowData(null);
    saveEsign(true);
    onClose(true);
  }, [setEditMultipleSignaturesRowData, saveEsign, onClose, clearFileState]);

  const handleCloseErrorDialog = useCallback(() => {
    setFileErrorDialog("");
  }, []);

  return (
    <>
      {isOpen && (
        <Modal
          hasCloseBtn
          isOpen={isOpen}
          onClose={handleModalClose}
          className="ESignDocumentTemplateEditor"
          title={editRowData ? "Edit E-sign Template " : "Create E-sign Template"}
          hasFooter={false}
        >
          {isOpen && !editRowData && !esToken && (
            <div className="uploadFileBox">
              {docsealKeysLoading && (
                <div style={{ textAlign: "center", padding: "20px" }}>
                  <div>Loading configuration...</div>
                </div>
              )}

              {docsealKeysError && (
                <div style={{ textAlign: "center", padding: "20px", color: "red" }}>
                  <div>Failed to load configuration. Please refresh the page.</div>
                </div>
              )}

              {!docsealKeysLoading && !docsealKeysError && (
                <>
                  <TextField
                    type="text"
                    name="TemplateName"
                    value={templateName}
                    label="Template Name*"
                    // className="OrganizationForm-TextField"
                    maxLength={200}
                    errorText={nameError}
                    onChange={changeField}
                  />

                  <DropzoneField
                    name="file"
                    label="Upload File*"
                    value={loadFile || []}
                    maxCount={1}
                    allowedTypes={ALLOWED_FILE_MIME_TYPES || ["application/pdf"]}
                    hintText="Supported file types: PDF; Max 20 MB"
                    className="ESignDocumentTemplateFileUploadForm-DropzoneField"
                    errors={uploadError || {}}
                    onChange={onChangeFileField}
                  />

                  {showPercentage && (
                    <Progress className="margin-top-15" value={percentage}>
                      {percentage}%
                    </Progress>
                  )}
                  {templateName && !nameError && loadFile.length > 0 && (
                    <Button outline color="success" className={"esignNext"} onClick={handleNextClick}>
                      Next
                    </Button>
                  )}
                </>
              )}
            </div>
          )}

          {templateName && showEsign && esToken && (
            <>
              <DocusealBuilder
                token={esToken}
                host={"docseal.simplyconnect.me"}
                withRecipientsButton={false}
                withSignYourselfButton={false}
                preview={false}
                fields={[...filterAutofillOrgFieldTypes, ...autofillFieldTypes]}
                withUploadButton={false}
              />

              {
                <div style={{ padding: 20 }}>
                  <Button outline color="success" className={"esignSave"} onClick={handleSaveClick}>
                    Save
                  </Button>
                </div>
              }
            </>
          )}
        </Modal>
      )}

      {fileErrorDialog && (
        <ErrorDialog
          isOpen
          title={fileErrorDialog}
          buttons={[
            {
              text: "Close",
              onClick: handleCloseErrorDialog,
            },
          ]}
        />
      )}
    </>
  );
};

export default ESignMultipleSignatures;
