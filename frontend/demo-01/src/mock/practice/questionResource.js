import { getNextId, parsePathParams, questionResources, storedFiles } from "./mockData";

/**
 * 遍历输出问题资源和文件存储信息
 */
function logResourcesAndFiles() {
  console.log("\n========================");
  console.log("=== 问题资源信息 ===");
  console.log(`questionResources 总数: ${questionResources.length}`);
  questionResources.forEach((resource) => {
    console.log(
      `资源ID: ${resource.id}, 名称: ${resource.name}, 类型: ${resource.type}, 大小: ${resource.size}, 问题ID: ${resource.questionId}`
    );
  });

  console.log("\n=== 文件存储信息 ===");
  console.log(`storedFiles 总数: ${storedFiles.size}`);
  storedFiles.forEach((fileInfo, fileId) => {
    console.log(
      `文件ID: ${fileId}, 文件名: ${fileInfo.filename}, 大小: ${fileInfo.size}, 上传时间: ${fileInfo.uploadedAt}`
    );
  });
  console.log("========================\n");
}

/**
 * 从multipart字符串中提取questionResourceJSON
 * @param {string} multipartString - 包含questionResourceJSON的multipart字符串
 * @returns {string|null} - 提取的questionResourceJSON字符串，或null如果未找到
 */
function extractQuestionResourceJSON(multipartString) {
  // 查找questionResourceJSON字段标识
  const fieldMarker = '"questionResourceJSON"';
  const fieldIndex = multipartString.indexOf(fieldMarker);

  if (fieldIndex === -1) {
    console.log("未找到questionResourceJSON字段标识");
    return null;
  }

  // 查找JSON开始位置，查找字段标识后的第一个{字符
  const afterFieldIndex = fieldIndex + fieldMarker.length;
  const jsonStartIndex = multipartString.indexOf("{", afterFieldIndex);

  if (jsonStartIndex === -1) {
    console.log("未找到JSON开始标记");
    return null;
  }

  // 查找JSON结束位置（优先查找换行符，其次是边界分隔符）
  const jsonEndIndex = findJsonEndIndex(multipartString, jsonStartIndex);
  console.log("jsonEndIndex :", jsonEndIndex);

  if (jsonEndIndex === -1) {
    console.log("未找到JSON结束位置");
    return null;
  }

  return multipartString.substring(jsonStartIndex, jsonEndIndex);
}

/**
 * 查找JSON字符串的结束位置
 * multipart格式中，JSON数据后紧跟换行符，然后是边界分隔符
 * @param {string} multipartString - 包含JSON字符串的multipart字符串
 * @param {number} jsonStartIndex - JSON字符串开始位置索引
 * @returns {number} - JSON字符串结束位置索引，或-1如果未找到
 */
function findJsonEndIndex(multipartString, jsonStartIndex) {
  // 查找JSON后的换行符（multipart格式的标准分隔方式）
  let endIndex = multipartString.indexOf("\r\n", jsonStartIndex);
  if (endIndex === -1) {
    endIndex = multipartString.indexOf("\n", jsonStartIndex);
  }

  return endIndex;
}

export default [
  // createQuestionResource - 新增问题资源
  {
    url: "/api/question/questionresource",
    method: "post",
    response: (req) => {
      try {
        console.log("createQuestionResource req.body keys:", Object.keys(req.body));

        let questionResourceJSON;

        // vite-plugin-mock将multipart作为对象传递，键是multipart字符串的开始
        if (typeof req.body === "object" && Object.keys(req.body).length === 1) {
          console.log("解析multipart对象...");

          // 获取唯一的键，这个键包含整个multipart内容
          const multipartKey = Object.keys(req.body)[0];
          const multipartString = req.body[multipartKey];
          console.log("multipart字符串长度:", multipartString.length);
          console.log(
            `multipart字符串前200字符: \n\n(begin)\n${multipartString.substring(0, 200)}\n(end)\n`
          );

          // 取消注释可以查看实际multipart格式，如果复杂文件可能输出过多
          // console.log("multipartString :", multipartString);

          // 使用提取的函数解析JSON
          questionResourceJSON = extractQuestionResourceJSON(multipartString);
          console.log("questionResourceJSON:", questionResourceJSON);
        } else if (req.body.questionResourceJSON) {
          // 直接传递JSON字符串的情况，非multipart请求
          questionResourceJSON = req.body.questionResourceJSON;
        }

        if (!questionResourceJSON) {
          return {
            code: 400,
            message: "questionResourceJSON字段不能为空",
            data: null,
          };
        }

        // 解析JSON数据
        let questionResource;
        try {
          questionResource = JSON.parse(questionResourceJSON);
          console.log("JSON解析成功, questionResource: ", questionResource);
        } catch (e) {
          console.error("JSON解析错误:", e);
          return {
            code: 400,
            message: "questionResourceJSON格式错误",
            data: null,
          };
        }

        // 验证必填字段，使用 == null 判断，只处理null和undefined，如果直接用 ! 会把0也判断为false
        if (
          questionResource.type == null ||
          questionResource.size == null ||
          questionResource.questionId == null
        ) {
          console.log(
            "字段验证失败 - type:",
            questionResource.type,
            "size:",
            questionResource.size,
            "questionId:",
            questionResource.questionId
          );
          return {
            code: 400,
            message: "type、size、questionId字段不能为空",
            data: null,
          };
        }

        // 生成新的资源记录
        const newResource = {
          id: getNextId(questionResources),
          description: questionResource.description || null,
          name: questionResource.name || "unknown_file",
          type: questionResource.type,
          size: questionResource.size,
          questionId: questionResource.questionId,
        };

        // 添加到存储
        questionResources.push(newResource);

        // 模拟文件存储
        storedFiles.set(newResource.id, {
          filename: newResource.name,
          size: newResource.size,
          uploadedAt: new Date().toISOString(),
        });

        // 输出当前数据状态
        logResourcesAndFiles();

        console.log("createQuestionResource success:", newResource);
        return {
          code: 200,
          message: "新增问题资源成功",
          data: null,
        };
      } catch (error) {
        console.error("createQuestionResource error:", error);
        return {
          code: 500,
          message: "Internal Server Error",
          data: null,
        };
      }
    },
  },

  // deleteQuestionResource - 删除问题资源
  {
    url: "/api/question/questionresource/:id",
    method: "delete",
    response: (req) => {
      try {
        console.log("req:", req);
        const id = Number(req.query.id || req.pathParams?.id);

        // 验证资源是否存在
        const resourceIndex = questionResources.findIndex((item) => item.id === id);
        if (resourceIndex === -1) {
          return {
            code: 404,
            message: "问题资源不存在",
            data: null,
          };
        }

        // 删除资源记录和模拟文件
        questionResources.splice(resourceIndex, 1);
        storedFiles.delete(id);

        console.log("deleteQuestionResource success:", { id });

        // 输出当前数据状态
        logResourcesAndFiles();

        return {
          code: 200,
          message: "删除问题资源成功",
          data: null,
        };
      } catch (error) {
        console.error("deleteQuestionResource error:", error);
        return {
          code: 500,
          message: "Internal Server Error",
          data: null,
        };
      }
    },
  },

  // updateQuestionResource - 更新问题资源
  {
    url: "/api/question/questionresource",
    method: "put",
    response: (req) => {
      try {
        console.log("updateQuestionResource req.body keys:", Object.keys(req.body));

        let questionResourceJSON;

        // vite-plugin-mock将multipart作为对象传递，键是multipart字符串的开始
        if (typeof req.body === "object" && Object.keys(req.body).length === 1) {
          console.log("解析multipart对象...");

          // 获取唯一的键，这个键包含整个multipart内容
          const multipartKey = Object.keys(req.body)[0];
          const multipartString = req.body[multipartKey];
          console.log("multipart字符串长度:", multipartString.length);

          // 使用提取的函数解析JSON
          questionResourceJSON = extractQuestionResourceJSON(multipartString);
          console.log("questionResourceJSON:", questionResourceJSON);
        } else if (req.body.questionResourceJSON) {
          // 直接传递JSON字符串的情况，非multipart请求
          questionResourceJSON = req.body.questionResourceJSON;
        }

        if (!questionResourceJSON) {
          return {
            code: 400,
            message: "questionResourceJSON字段不能为空",
            data: null,
          };
        }

        // 解析JSON数据
        let questionResource;
        try {
          questionResource = JSON.parse(questionResourceJSON);
          console.log("JSON解析成功, questionResource: ", questionResource);
        } catch (e) {
          console.error("JSON解析错误:", e);
          return {
            code: 400,
            message: "questionResourceJSON格式错误",
            data: null,
          };
        }

        // 验证必填字段，使用 == null 判断，只处理null和undefined
        if (questionResource.id == null) {
          console.log("字段验证失败 - id:", questionResource.id);
          return {
            code: 400,
            message: "id字段不能为空",
            data: null,
          };
        }

        // 查找资源并更新
        const resourceToUpdate = questionResources.find((item) => item.id === questionResource.id);
        if (!resourceToUpdate) {
          return {
            code: 404,
            message: "问题资源不存在",
            data: null,
          };
        }

        // 更新字段
        Object.assign(resourceToUpdate, questionResource);

        // 如果有文件更新，也更新storedFiles
        if (questionResource.name || questionResource.size) {
          const fileToUpdate = storedFiles.get(questionResource.id);
          if (fileToUpdate) {
            if (questionResource.name) fileToUpdate.filename = questionResource.name;
            if (questionResource.size) fileToUpdate.size = questionResource.size;
            fileToUpdate.uploadedAt = new Date().toISOString();
          }
        }

        // 输出当前数据状态
        logResourcesAndFiles();

        console.log("updateQuestionResource success:", resourceToUpdate);
        return {
          code: 200,
          message: "更新问题资源成功",
          data: null,
        };
      } catch (error) {
        console.error("updateQuestionResource error:", error);
        return {
          code: 500,
          message: "Internal Server Error",
          data: null,
        };
      }
    },
  },

  // getQuestionResources - 按参数查询问题资源
  {
    url: "/api/question/questionresource",
    method: "get",
    response: (req) => {
      try {
        console.log("req:", req);
        const { id, name, type, size, questionId } = req.query;

        // 筛选资源
        let result = [...questionResources];
        if (id) {
          result = result.filter((item) => item.id === Number(id));
        }
        if (name) {
          result = result.filter((item) => item.name.includes(name));
        }
        if (type) {
          result = result.filter((item) => item.type === Number(type));
        }
        if (size) {
          result = result.filter((item) => item.size === Number(size));
        }
        if (questionId) {
          result = result.filter((item) => item.questionId === Number(questionId));
        }

        console.log("getQuestionResources success:", { total: result.length });
        return {
          code: 200,
          message: "查询成功",
          data: result,
        };
      } catch (error) {
        console.error("getQuestionResources error:", error);
        return {
          code: 500,
          message: "Internal Server Error",
          data: null,
        };
      }
    },
  },

  // downloadQuestionResource - 下载问题资源文件
  {
    url: "/api/question/questionresource/download/:id",
    method: "get",
    rawResponse: async (req, res) => {
      try {
        // 解析路径参数
        const pathParams = parsePathParams(req, 1);
        const id = Number(pathParams.param1);

        // 验证资源是否存在
        const resource = questionResources.find((item) => item.id === id);
        if (!resource) {
          res.statusCode = 404;
          res.setHeader("Content-Type", "application/json");
          res.end(
            JSON.stringify({
              code: 404,
              message: "问题资源不存在",
              data: null,
            })
          );
          return;
        }

        // 检查是否有模拟文件
        const file = storedFiles.get(id);
        if (!file) {
          res.statusCode = 404;
          res.setHeader("Content-Type", "application/json");
          res.end(
            JSON.stringify({
              code: 404,
              message: "文件不存在",
              data: null,
            })
          );
          return;
        }

        // 生成简单的模拟文件内容
        const mockContent = `# 问题资源文件
文件名: ${resource.name}
资源ID: ${resource.id}
资源类型: ${resource.type} (0:测试用例,1:用例答案,2:问题描述资料)
资源大小: ${resource.size} bytes
问题ID: ${resource.questionId}
描述: ${resource.description || "无描述"}
生成时间: ${new Date().toISOString()}

这是一个模拟的文件内容，用于测试文件下载功能。
`;

        console.log("downloadQuestionResource success:", {
          id,
          filename: resource.name,
          size: mockContent.length,
          contentPreview: mockContent.substring(0, 100) + "...",
          contentEnd: mockContent.substring(mockContent.length - 50),
        });

        // 根据文件扩展名获取正确的 MIME 类型（不确定，但是如果只是 res.setHeader text/plain 这样可能不太对）
        function getContentType(filename) {
          const ext = filename.split(".").pop().toLowerCase();
          // https://www.runoob.com/http/http-content-type.html
          const mimeTypes = {
            txt: "text/plain; charset=utf-8",
            json: "application/json; charset=utf-8",
            pdf: "application/pdf",
            doc: "application/msword",
            docx: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            ppt: "application/vnd.ms-powerpoint",
            pptx: "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            xls: "application/vnd.ms-excel",
            xlsx: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            zip: "application/zip",
            rar: "application/x-rar-compressed",
            jpg: "image/jpeg",
            jpeg: "image/jpeg",
            png: "image/png",
            gif: "image/gif",
            svg: "image/svg+xml",
            mp4: "video/mp4",
            mp3: "audio/mpeg",
          };
          return mimeTypes[ext] || "application/octet-stream";
        }

        // 设置响应头
        res.statusCode = 200;
        const contentType = getContentType(resource.name);
        console.log("contentType :", contentType);
        res.setHeader("Content-Type", contentType);
        res.setHeader("Content-Disposition", `attachment; filename="${resource.name}"`);
        // 不设置 Content-Length，让浏览器自动处理，否则文件会被截断

        // 发送文件内容
        // 对于二进制文件类型，不指定编码；对于文本文件，使用 utf-8
        if (
          contentType.includes("text/") ||
          contentType.includes("json") ||
          contentType.includes("xml")
        ) {
          res.write(mockContent, "utf8");
        } else {
          res.write(mockContent);
        }
        res.end();
      } catch (error) {
        console.error("downloadQuestionResource error:", error);
        res.statusCode = 500;
        res.setHeader("Content-Type", "application/json");
        res.end(
          JSON.stringify({
            code: 500,
            message: "Internal Server Error",
            data: null,
          })
        );
      }
    },
  },
  // getQuestionResourcesPage - 按参数分页查询问题资源
  // 更通用的url匹配放在后面，否则会在调用downloadQuestionResource时被匹配到getQuestionResourcesPage
  {
    url: "/api/question/questionresource/:pageNo/:pageSize",
    method: "get",
    response: (req) => {
      try {
        console.log("req:", req);

        const pathParams = parsePathParams(req, 2);
        const { id, name, type, size, questionId } = req.query;
        const pageNo = Number(pathParams.param1);
        const pageSize = Number(pathParams.param2);

        // 筛选资源
        let filtered = [...questionResources];
        if (id) {
          filtered = filtered.filter((item) => item.id === Number(id));
        }
        if (name) {
          filtered = filtered.filter((item) => item.name.includes(name));
        }
        if (type) {
          filtered = filtered.filter((item) => item.type === Number(type));
        }
        if (size) {
          filtered = filtered.filter((item) => item.size === Number(size));
        }
        if (questionId) {
          filtered = filtered.filter((item) => item.questionId === Number(questionId));
        }

        // 分页处理
        const total = filtered.length;
        const pages = total > 0 ? Math.ceil(total / pageSize) : 0;
        const records = total > 0 ? filtered.slice((pageNo - 1) * pageSize, pageNo * pageSize) : [];

        const pageResult = {
          records,
          total,
          current: total > 0 ? pageNo : null,
          size: total > 0 ? pageSize : null,
          pages,
        };

        console.log("getQuestionResourcesPage success:", { ...pageResult });
        return {
          code: 200,
          message: "分页查询成功",
          data: pageResult,
        };
      } catch (error) {
        console.error("getQuestionResourcesPage error:", error);
        return {
          code: 500,
          message: "Internal Server Error",
          data: null,
        };
      }
    },
  },
];
