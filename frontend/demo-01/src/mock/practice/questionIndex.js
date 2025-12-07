import { getNextId, parsePathParams, questionIndexes } from "./mockData";

// createQuestionIndexes - 新增问题与练习关系
export const createQuestionIndexes = {
  url: "/api/practice/questionindex",
  method: "post",
  response: (req) => {
    try {
      console.log("req :", req);
      const questionIndexList = req.body;

      // 验证必填字段
      if (!Array.isArray(questionIndexList) || questionIndexList.length === 0) {
        return {
          code: 400,
          message: "questionIndexList不能为空且必须是数组",
          data: null,
        };
      }

      // 验证每个索引项的必填字段
      for (const index of questionIndexList) {
        if (!index.practiceId || !index.questionId) {
          return {
            code: 400,
            message: "每项元素的practiceId、questionId不能为空",
            data: null,
          };
        }
      }

      // 构造新的问题索引
      const newIndexes = [];
      for (const index of questionIndexList) {
        const newId = getNextId(questionIndexes);
        const newIndex = {
          id: newId,
          practiceId: index.practiceId,
          questionId: index.questionId,
        };
        questionIndexes.push(newIndex);
        newIndexes.push(newIndex);
      }

      console.log("createQuestionIndexes success:", { newIndexes });
      return {
        code: 200,
        message: "新增问题与练习关系成功",
        data: null,
      };
    } catch (error) {
      console.error("createQuestionIndexes error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// deleteQuestionIndexByIds - 批量删除问题与练习关系
export const deleteQuestionIndexByIds = {
  url: "/api/practice/questionindex",
  method: "delete",
  response: (req) => {
    try {
      console.log("req :", req);
      const { indexIds } = req.query;

      // 验证必填字段
      if (!indexIds) {
        return {
          code: 400,
          message: "indexIds不能为空",
          data: null,
        };
      }

      // 将字符串转换为数组（如果传入的是逗号分隔的字符串）
      let idsToDelete;
      if (Array.isArray(indexIds)) {
        idsToDelete = indexIds.map((id) => Number(id));
      } else if (typeof indexIds === "string") {
        idsToDelete = indexIds.split(",").map((id) => Number(id));
      } else {
        return {
          code: 400,
          message: "indexIds参数格式错误",
          data: null,
        };
      }

      // 验证索引是否存在
      const existingIds = questionIndexes.map((item) => item.id);
      const nonExistingIds = idsToDelete.filter((id) => !existingIds.includes(id));

      if (nonExistingIds.length > 0) {
        return {
          code: 404,
          message: `以下索引ID不存在: ${nonExistingIds.join(", ")}`,
          data: null,
        };
      }

      // 删除索引
      const deletedCount = idsToDelete.length;
      questionIndexes.splice(
        0,
        questionIndexes.length,
        ...questionIndexes.filter((item) => !idsToDelete.includes(item.id))
      );

      console.log("deleteQuestionIndexByIds success:", {
        deletedCount,
        deletedIds: idsToDelete,
      });
      return {
        code: 200,
        message: `成功删除${deletedCount}个问题与练习关系`,
        data: null,
      };
    } catch (error) {
      console.error("deleteQuestionIndexByIds error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// updateQuestionIndex - 更新问题与练习关系
export const updateQuestionIndex = {
  url: "/api/practice/questionindex",
  method: "put",
  response: (req) => {
    try {
      console.log("req :", req);
      const questionIndex = req.body;

      // 验证必填字段
      if (questionIndex.id == null) {
        return {
          code: 400,
          message: "id字段不能为空",
          data: null,
        };
      }

      const updateFields = Object.keys(questionIndex).filter((key) => key !== "id");
      if (updateFields.length === 0) {
        return {
          code: 400,
          message: "要更新的字段不能为空",
          data: null,
        };
      }

      // 查找索引并更新
      const indexToUpdate = questionIndexes.find((item) => item.id === questionIndex.id);
      if (!indexToUpdate) {
        return {
          code: 404,
          message: "问题索引不存在",
          data: null,
        };
      }

      Object.assign(indexToUpdate, questionIndex);

      console.log("updateQuestionIndex success:", {
        id: questionIndex.id,
        updatedFields: updateFields,
      });
      return {
        code: 200,
        message: "更新问题与练习关系成功",
        data: null,
      };
    } catch (error) {
      console.error("updateQuestionIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getQuestionIndexes - 按参数查询问题索引
export const getQuestionIndexes = {
  url: "/api/practice/questionindex",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);
      const { practiceId, questionId } = req.query;

      // 筛选索引
      let result = [...questionIndexes];
      if (practiceId) {
        result = result.filter((item) => item.practiceId === Number(practiceId));
      }
      if (questionId) {
        result = result.filter((item) => item.questionId === Number(questionId));
      }

      console.log("getQuestionIndexes success:", { total: result.length });
      return {
        code: 200,
        message: "查询成功",
        data: result,
      };
    } catch (error) {
      console.error("getQuestionIndexes error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getQuestionIndexesPage - 按参数分页查询问题索引
export const getQuestionIndexesPage = {
  url: "/api/practice/questionindex/:pageNo/:pageSize",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);

      const pathParams = parsePathParams(req, 2);
      const { practiceId, questionId } = req.query;
      const pageNo = Number(pathParams.param1);
      const pageSize = Number(pathParams.param2);

      // 筛选索引
      let filtered = [...questionIndexes];
      if (practiceId) {
        filtered = filtered.filter((item) => item.practiceId === Number(practiceId));
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

      console.log("getQuestionIndexesPage success:", { ...pageResult });
      return {
        code: 200,
        message: "分页查询成功",
        data: pageResult,
      };
    } catch (error) {
      console.error("getQuestionIndexesPage error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

export default [
  createQuestionIndexes,
  deleteQuestionIndexByIds,
  updateQuestionIndex,
  getQuestionIndexes,
  getQuestionIndexesPage,
];
