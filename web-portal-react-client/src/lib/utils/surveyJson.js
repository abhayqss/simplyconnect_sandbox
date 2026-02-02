export function openDatabase() {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open("SurveyDatabase", 1);

    request.onupgradeneeded = function (event) {
      const db = event.target.result;
      if (!db.objectStoreNames.contains("surveys")) {
        db.createObjectStore("surveys", { keyPath: "id" });
      }
    };

    request.onsuccess = function (event) {
      resolve(event.target.result);
    };

    request.onerror = function (event) {
      reject("Error opening IndexedDB: " + event.target.errorCode);
    };
  });
}

export function saveSurvey(db, surveyData) {
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(["surveys"], "readwrite");
    const store = transaction.objectStore("surveys");

    const surveyObject = {
      id: "survey-json", // The key to identify the survey
      data: surveyData, // The survey data
    };

    const request = store.put(surveyObject);

    request.onsuccess = function () {
      resolve("Survey saved successfully.");
    };

    request.onerror = function (event) {
      reject("Error saving survey: " + event.target.errorCode);
    };
  });
}

export function deleteSurvey(db) {
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(["surveys"], "readwrite");
    const store = transaction.objectStore("surveys");

    const request = store.delete("survey-json");

    request.onsuccess = function () {
      resolve("Survey data deleted successfully.");
    };

    request.onerror = function (event) {
      reject("Error deleting survey data: " + event.target.errorCode);
    };
  });
}

export function getSurvey(db) {
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(["surveys"], "readonly");
    const store = transaction.objectStore("surveys");

    const request = store.get("survey-json");

    request.onsuccess = function (event) {
      const result = event.target.result;
      if (result) {
        resolve(result.data); // 获取存储的数据
      } else {
        resolve(null); // 如果没有找到数据，返回 null
      }
    };

    request.onerror = function (event) {
      reject("Error retrieving survey: " + event.target.errorCode);
    };
  });
}
