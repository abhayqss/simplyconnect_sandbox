import fs from "fs";
import path from "path";
import archiver from "archiver";
import { exec } from "child_process";

// 定义要打包的文件和目录
const filesToZip = ["build"];

console.log("当前环境变量 ENV_NAME:", process.env.ENV_NAME);
const envName = process.env.ENV_NAME || "build"; // 默认值为 'build'
const outputFile = `${envName}.zip`; // 动态生成 ZIP 文件名

// 创建一个可写流，用于写入 ZIP 文件
const output = fs.createWriteStream(outputFile);
const archive = archiver("zip", {
  zlib: { level: 9 }, // 设置压缩级别
});

// 监听完成事件
output.on("close", () => {
  const zipFilePath = path.resolve(outputFile); // 获取 ZIP 文件的绝对路径
  const parentDir = path.dirname(zipFilePath); // 获取 ZIP 文件的父目录
  const fileSizeInBytes = archive.pointer(); // 获取 ZIP 文件的大小（字节）
  const fileSizeInMB = (fileSizeInBytes / (1024 * 1024)).toFixed(2); // 转换为 MB

  console.log(`ZIP 文件已生成，大小：${fileSizeInMB} MB`);
  console.log(`ZIP 文件路径：${zipFilePath}`);
  console.log(`ZIP 文件父目录：file://${parentDir}`);

  // 打开目录
  let openCommand;
  switch (process.platform) {
    case "win32":
      openCommand = `explorer "${parentDir}"`;
      break;
    case "darwin":
      openCommand = `open "${parentDir}"`;
      break;
    case "linux":
      openCommand = `xdg-open "${parentDir}"`;
      break;
    default:
      console.error("不支持的操作系统");
      return;
  }

  exec(openCommand, (err) => {
    if (err) {
      console.error("无法打开目录:", err);
    }
  });

  // 删除 build 目录
  try {
    fs.rmSync(path.resolve("build"), { recursive: true, force: true });
    console.log("build 目录已删除");
  } catch (err) {
    console.error("删除 build 目录时出错:", err);
  }
});

// 监听错误事件
archive.on("error", (err) => {
  throw err;
});

// 将 ZIP 文件与输出流关联
archive.pipe(output);

// 遍历文件和目录，添加到 ZIP 文件中
filesToZip.forEach((file) => {
  const filePath = path.resolve(file);

  // 检查文件或目录是否存在
  if (fs.existsSync(filePath)) {
    const stats = fs.statSync(filePath);

    if (stats.isDirectory()) {
      // 如果是目录，递归添加
      archive.directory(filePath, file);
    } else if (stats.isFile()) {
      // 如果是文件，直接添加
      archive.file(filePath, { name: file });
    }
  } else {
    console.warn(`文件或目录不存在: ${filePath}`);
  }
});

// 完成打包
archive.finalize();
