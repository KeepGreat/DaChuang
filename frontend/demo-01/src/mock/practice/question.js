import { getNextId, parsePathParams } from "../utils";
import { questionIndexes, questions } from "./mockData";

// createQuestionAndIndex - 新增问题及关联的QuestionIndex
export const createQuestionAndIndex = {
  url: "/api/practice/question",
  method: "post",
  response: (req) => {
    try {
      console.log("req :", req);
      const { question, questionIndex } = req.body;

      // 验证必填字段
      if (!question || !questionIndex) {
        return {
          code: 400,
          message: "question和questionIndex字段不能为空",
          data: null,
        };
      }
      if (question.type == null || !question.content || question.hasResource == null) {
        return {
          code: 400,
          message: "question.type、question.content、question.hasResource字段不能为空",
          data: null,
        };
      }
      if (!questionIndex.practiceId || !questionIndex.questionId) {
        return {
          code: 400,
          message: "questionIndex.practiceId、questionIndex.questionId字段不能为空",
          data: null,
        };
      }

      // 生成新问题ID
      const newQuestionId = getNextId(questions);
      // 构造新问题
      const newQuestion = {
        id: newQuestionId,
        name: question.name || `问题${newQuestionId}`,
        type: question.type,
        content: question.content,
        hasResource: question.hasResource,
      };

      // 构造新问题索引
      const newQuestionIndex = {
        id: getNextId(questionIndexes),
        questionId: newQuestionId,
        practiceId: questionIndex.practiceId,
      };

      // 添加到数据中
      questions.push(newQuestion);
      questionIndexes.push(newQuestionIndex);

      console.log("createQuestionAndIndex success:", { newQuestion, newQuestionIndex });
      return {
        code: 200,
        message: "新增问题成功",
        data: null,
      };
    } catch (error) {
      console.error("createQuestionAndIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// deleteQuestionById - 根据问题ID删除问题及关联的QuestionIndex
export const deleteQuestionById = {
  url: "/api/practice/question/:id",
  method: "delete",
  response: (req) => {
    try {
      console.log("req :", req);
      const questionId = Number(req.query.id);

      // 验证问题是否存在
      const question = questions.find((item) => item.id === questionId);
      if (!question) {
        return {
          code: 404,
          message: "问题不存在",
          data: null,
        };
      }

      // 删除问题和关联的索引
      const relatedIndexIds = questionIndexes
        .filter((item) => item.questionId === questionId)
        .map((item) => item.id);
      questions.splice(
        questions.findIndex((item) => item.id === questionId),
        1
      );
      questionIndexes.splice(
        0,
        questionIndexes.length,
        ...questionIndexes.filter((item) => !relatedIndexIds.includes(item.id))
      );

      console.log("deleteQuestionById success:", {
        questionId,
        deletedIndexIds: relatedIndexIds,
      });
      return {
        code: 200,
        message: "删除问题成功",
        data: null,
      };
    } catch (error) {
      console.error("deleteQuestionById error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// deleteQuestionByIndex - 按练习ID批量删除问题及关联的QuestionIndex
export const deleteQuestionByIndex = {
  url: "/api/practice/question",
  method: "delete",
  response: (req) => {
    try {
      console.log("req :", req);
      const { practiceId } = req.query;

      // 验证必填字段
      if (!practiceId) {
        return {
          code: 400,
          message: "practiceId字段不能为空",
          data: null,
        };
      }

      // 筛选关联的问题ID
      const relatedIndexes = questionIndexes.filter(
        (item) => item.practiceId === Number(practiceId)
      );
      const questionIds = [...new Set(relatedIndexes.map((item) => item.questionId))];

      if (questionIds.length === 0) {
        return {
          code: 404,
          message: "未找到关联的问题",
          data: null,
        };
      }

      // 删除问题和关联的索引
      questions.splice(
        0,
        questions.length,
        ...questions.filter((item) => !questionIds.includes(item.id))
      );
      questionIndexes.splice(
        0,
        questionIndexes.length,
        ...questionIndexes.filter(
          (item) => !relatedIndexes.map((i) => i.id).includes(item.id)
        )
      );

      console.log("deleteQuestionByIndex success:", {
        questionIds,
        deletedIndexIds: relatedIndexes.map((i) => i.id),
      });
      return {
        code: 200,
        message: `成功删除${questionIds.length}个问题`,
        data: null,
      };
    } catch (error) {
      console.error("deleteQuestionByIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// updateQuestion - 更新问题信息
export const updateQuestion = {
  url: "/api/practice/question",
  method: "put",
  response: (req) => {
    try {
      console.log("req :", req);
      const question = req.body;

      // 验证必填字段
      if (question.id == null) {
        return {
          code: 400,
          message: "id字段不能为空",
          data: null,
        };
      }
      const updateFields = Object.keys(question).filter((key) => key !== "id");
      if (updateFields.length === 0) {
        return {
          code: 400,
          message: "要更新的字段不能为空",
          data: null,
        };
      }

      // 查找问题并更新
      const questionToUpdate = questions.find((item) => item.id === question.id);
      if (!questionToUpdate) {
        return {
          code: 404,
          message: "问题不存在",
          data: null,
        };
      }
      Object.assign(questionToUpdate, question);

      console.log("updateQuestion success:", {
        questionId: question.id,
        updatedFields: updateFields,
      });
      return {
        code: 200,
        message: "更新问题成功",
        data: null,
      };
    } catch (error) {
      console.error("updateQuestion error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getQuestions - 按参数查询问题
export const getQuestions = {
  url: "/api/practice/question",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);
      const { id, name, type, hasResource } = req.query;

      // 筛选问题
      let result = [...questions];
      if (id) {
        result = result.filter((item) => item.id === Number(id));
      }
      if (name) {
        result = result.filter((item) => item.name.includes(name));
      }
      if (type != null) {
        result = result.filter((item) => item.type === Number(type));
      }
      if (hasResource != null) {
        result = result.filter((item) => item.hasResource === (hasResource === "true"));
      }

      console.log("getQuestions success:", { total: result.length });
      return {
        code: 200,
        message: "查询成功",
        data: result,
      };
    } catch (error) {
      console.error("getQuestions error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getQuestionsPage - 按参数分页查询问题
export const getQuestionsPage = {
  url: "/api/practice/question/:pageNo/:pageSize",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);

      const pathParams = parsePathParams(req, 2);
      const { id, name, type, hasResource } = req.query;
      const pageNo = Number(pathParams.param1);
      const pageSize = Number(pathParams.param2);

      // 筛选问题
      let filtered = [...questions];
      if (id) {
        filtered = filtered.filter((item) => item.id === Number(id));
      }
      if (name) {
        filtered = filtered.filter((item) => item.name.includes(name));
      }
      if (type != null) {
        filtered = filtered.filter((item) => item.type === Number(type));
      }
      if (hasResource != null) {
        filtered = filtered.filter(
          (item) => item.hasResource === (hasResource === "true")
        );
      }

      // 分页处理
      const total = filtered.length;
      const pages = total > 0 ? Math.ceil(total / pageSize) : 0;
      const records =
        total > 0 ? filtered.slice((pageNo - 1) * pageSize, pageNo * pageSize) : [];

      const pageResult = {
        records,
        total,
        current: total > 0 ? pageNo : null,
        size: total > 0 ? pageSize : null,
        pages,
      };

      console.log("getQuestionsPage success:", { ...pageResult });
      return {
        code: 200,
        message: "分页查询成功",
        data: pageResult,
      };
    } catch (error) {
      console.error("getQuestionsPage error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getQuestionByIndex - 根据练习ID查询问题
export const getQuestionByIndex = {
  url: "/api/practice/question/index",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);
      const { practiceId } = req.query;

      // 验证必填字段
      if (!practiceId) {
        return {
          code: 400,
          message: "practiceId字段不能为空",
          data: null,
        };
      }

      // 筛选关联的问题ID
      const relatedIndexes = questionIndexes.filter(
        (item) => item.practiceId === Number(practiceId)
      );
      const questionIds = [...new Set(relatedIndexes.map((item) => item.questionId))];
      const result = questions.filter((item) => questionIds.includes(item.id));

      console.log("getQuestionByIndex success:", { total: result.length, questionIds });
      return {
        code: 200,
        message: "查询成功",
        data: result,
      };
    } catch (error) {
      console.error("getQuestionByIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getQuestionPageByIndex - 根据练习ID分页查询问题
export const getQuestionPageByIndex = {
  url: "/api/practice/question/index/:pageNo/:pageSize",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);

      const pathParams = parsePathParams(req, 2);
      const { practiceId } = req.query;
      const pageNo = Number(pathParams.param1);
      const pageSize = Number(pathParams.param2);

      // 验证必填字段
      if (!practiceId) {
        return {
          code: 400,
          message: "practiceId字段不能为空",
          data: null,
        };
      }

      // 筛选关联的问题
      const relatedIndexes = questionIndexes.filter(
        (item) => item.practiceId === Number(practiceId)
      );
      const questionIds = [...new Set(relatedIndexes.map((item) => item.questionId))];
      const filtered = questions.filter((item) => questionIds.includes(item.id));

      // 分页处理
      const total = filtered.length;
      const pages = total > 0 ? Math.ceil(total / pageSize) : 0;
      const records =
        total > 0 ? filtered.slice((pageNo - 1) * pageSize, pageNo * pageSize) : [];

      const pageResult = {
        records,
        total,
        current: total > 0 ? pageNo : null,
        size: total > 0 ? pageSize : null,
        pages,
      };

      console.log("getQuestionPageByIndex success:", { ...pageResult });
      return {
        code: 200,
        message: "分页查询成功",
        data: pageResult,
      };
    } catch (error) {
      console.error("getQuestionPageByIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

export default [
  createQuestionAndIndex,
  deleteQuestionById,
  deleteQuestionByIndex,
  updateQuestion,
  getQuestions,
  getQuestionsPage,
  getQuestionByIndex,
  getQuestionPageByIndex,
];
