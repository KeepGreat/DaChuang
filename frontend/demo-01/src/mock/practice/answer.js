import { answers, getNextId, parsePathParams } from "./mockData";

// createAnswer - 新增答案
export const createAnswer = {
  url: "/api/practice/answer",
  method: "post",
  response: (req) => {
    try {
      console.log("req :", req);
      const answer = req.body;

      // 验证必填字段
      if (!answer.content || !answer.questionId) {
        return {
          code: 400,
          message: "content和questionId字段不能为空",
          data: null,
        };
      }

      // 构造新答案
      const newAnswer = {
        id: getNextId(answers),
        content: answer.content,
        analysis: answer.analysis || null,
        questionId: answer.questionId,
      };

      // 添加到数据中
      answers.push(newAnswer);

      console.log("addAnswer success:", { newAnswer });
      return {
        code: 200,
        message: "新增答案成功",
        data: null,
      };
    } catch (error) {
      console.error("addAnswer error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// deleteAnswerById - 根据答案ID删除答案
export const deleteAnswerById = {
  url: "/api/practice/answer/:id",
  method: "delete",
  response: (req) => {
    try {
      console.log("req :", req);
      const answerId = Number(req.query.id);

      // 验证答案是否存在
      const answerIndex = answers.findIndex((item) => item.id === answerId);
      if (answerIndex === -1) {
        return {
          code: 404,
          message: "答案不存在",
          data: null,
        };
      }

      // 删除答案
      const deletedAnswer = answers[answerIndex];
      answers.splice(answerIndex, 1);

      console.log("deleteAnswer success:", { deletedAnswer });
      return {
        code: 200,
        message: "删除答案成功",
        data: null,
      };
    } catch (error) {
      console.error("deleteAnswer error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// updateAnswer - 更新答案
export const updateAnswer = {
  url: "/api/practice/answer",
  method: "put",
  response: (req) => {
    try {
      console.log("req :", req);
      const answer = req.body;

      // 验证必填字段
      if (answer.id == null) {
        return {
          code: 400,
          message: "id字段不能为空",
          data: null,
        };
      }

      const updateFields = Object.keys(answer).filter((key) => key !== "id");
      if (updateFields.length === 0) {
        return {
          code: 400,
          message: "要更新的字段不能为空",
          data: null,
        };
      }

      // 查找答案并更新
      const answerToUpdate = answers.find((item) => item.id === answer.id);
      if (!answerToUpdate) {
        return {
          code: 404,
          message: "答案不存在",
          data: null,
        };
      }

      Object.assign(answerToUpdate, answer);

      console.log("updateAnswerById success:", {
        id: answer.id,
        updatedFields: updateFields,
      });
      return {
        code: 200,
        message: "更新答案成功",
        data: null,
      };
    } catch (error) {
      console.error("updateAnswerById error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

/**
 * getAnswersByQuestionIds和getAnswers - 根据问题id数组获得答案数组 和 根据参数查询答案数组
 * 需要合并，否则两个MockMethod可能会出现冲突，两个接口使用了相同的url /api/practice/answer，且都是GET请求
 * Mock内部通过检查参数来自动判断使用哪种查询逻辑
 *
 */
export const getAnswers = {
  url: "/api/practice/answer",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);
      const { questionIds, id, questionId } = req.query;

      // 如果有questionIds参数，则按问题id数组查询，getAnswersByQuestionIds
      if (questionIds) {
        // 验证必填字段
        // Mock接口无法测试到这个case，前端调用getAnswersByQuestionIds时应该保证传入有效参数
        if (!questionIds) {
          return {
            code: 400,
            message: "questionIds不能为空",
            data: null,
          };
        }

        // 将字符串转换为数组（如果传入的是逗号分隔的字符串）
        let idsToQuery;
        if (Array.isArray(questionIds)) {
          idsToQuery = questionIds.map((id) => Number(id));
        } else if (typeof questionIds === "string") {
          idsToQuery = questionIds.split(",").map((id) => Number(id));
        } else {
          return {
            code: 400,
            message: "questionIds参数格式错误",
            data: null,
          };
        }

        // 筛选答案
        const result = answers.filter((item) => idsToQuery.includes(item.questionId));

        console.log("getAnswersByQuestionIds success:", {
          queryIds: idsToQuery,
          total: result.length,
        });
        return {
          code: 200,
          message: "查询成功",
          data: result,
        };
      }

      // 否则按其他参数查询，id、questionId等，getAnswers
      else {
        // 筛选答案
        let result = [...answers];
        if (id) {
          result = result.filter((item) => item.id === Number(id));
        }
        if (questionId) {
          result = result.filter((item) => item.questionId === Number(questionId));
        }

        console.log("getAnswers success:", { total: result.length });
        return {
          code: 200,
          message: "查询成功",
          data: result,
        };
      }
    } catch (error) {
      console.error("getAnswers error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getAnswersPage - 按参数分页查询答案
export const getAnswersPage = {
  url: "/api/practice/answer/:pageNo/:pageSize",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);

      const pathParams = parsePathParams(req, 2);
      const { id, questionId } = req.query;
      const pageNo = Number(pathParams.param1);
      const pageSize = Number(pathParams.param2);

      // 筛选答案
      let filtered = [...answers];
      if (id) {
        filtered = filtered.filter((item) => item.id === Number(id));
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

      console.log("getAnswersPage success:", { ...pageResult });
      return {
        code: 200,
        message: "分页查询成功",
        data: pageResult,
      };
    } catch (error) {
      console.error("getAnswersPage error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

export default [createAnswer, deleteAnswerById, updateAnswer, getAnswers, getAnswersPage];
