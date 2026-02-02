const dateISOString = new Date(+new Date() + 864e5).toISOString();
const xAmzDate = dateISOString.split("-").join("").split(":").join("").split(".").join("");
const dateYMD = dateISOString.split("T")[0].split("-").join("");

module.exports = {
  dateISOString,
  xAmzDate,
  dateYMD,
};
