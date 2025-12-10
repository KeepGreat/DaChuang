import { createPageResult, getNextId, parsePathParams } from "../utils";
import { practiceIndexes } from "./mockData";

// createPracticeIndex - 新增练习与课程关系
export const createPracticeIndex = {
  url: "/api/practice/practiceindex/index",
  method: "post",
  response: (req) => {
    try {
      console.log("req :", req);
      const indexList = req.body.indexList;

      // 验证必填字段
      if (!indexList || !Array.isArray(indexList) || indexList.length === 0) {
        return {
          code: 400,
          message: "indexList不能为空且必须是数组",
          data: null,
        };
      }

      // 验证每个索引项的必填字段
      for (const index of indexList) {
        if (!index.practiceId || !index.courseSectionId) {
          return {
            code: 400,
            message: "每项元素的practiceId、courseSectionId不能为空",
            data: null,
          };
        }
      }

      // 构造新的练习索引
      // 优化了ID生成逻辑，使用循环而不是map函数，确保每次调用getNextId时都能获得正确的递增ID
      const newIndexes = [];
      for (const index of indexList) {
        const newId = getNextId(practiceIndexes);
        const newIndex = {
          id: newId,
          practiceId: index.practiceId,
          courseSectionId: index.courseSectionId,
          courseId: index.courseId || 0,
        };
        practiceIndexes.push(newIndex);
        newIndexes.push(newIndex);
      }

      console.log("createPracticeIndex success:", { newIndexes });
      return {
        code: 200,
        message: "新增练习与课程关系成功",
        data: null,
      };
    } catch (error) {
      console.error("createPracticeIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// deletePracticeIndexByIds - 批量删除练习与课程关系
export const deletePracticeIndexByIds = {
  url: "/api/practice/practiceindex/index",
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
      const existingIds = practiceIndexes.map((item) => item.id);
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
      practiceIndexes.splice(
        0,
        practiceIndexes.length,
        ...practiceIndexes.filter((item) => !idsToDelete.includes(item.id))
      );

      console.log("deletePracticeIndexByIds success:", {
        deletedCount,
        deletedIds: idsToDelete,
      });
      return {
        code: 200,
        message: `成功删除${deletedCount}个练习与课程关系`,
        data: null,
      };
    } catch (error) {
      console.error("deletePracticeIndexByIds error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// updatePracticeIndex - 更新练习与课程关系
export const updatePracticeIndex = {
  url: "/api/practice/practiceindex/index",
  method: "put",
  response: (req) => {
    try {
      console.log("req :", req);
      const practiceIndex = req.body;

      // 验证必填字段
      if (practiceIndex.id == null) {
        return {
          code: 400,
          message: "id字段不能为空",
          data: null,
        };
      }

      const updateFields = Object.keys(practiceIndex).filter((key) => key !== "id");
      if (updateFields.length === 0) {
        return {
          code: 400,
          message: "要更新的字段不能为空",
          data: null,
        };
      }

      // 查找索引并更新
      const indexToUpdate = practiceIndexes.find((item) => item.id === practiceIndex.id);
      if (!indexToUpdate) {
        return {
          code: 404,
          message: "练习索引不存在",
          data: null,
        };
      }

      Object.assign(indexToUpdate, practiceIndex);

      console.log("updatePracticeIndex success:", {
        id: practiceIndex.id,
        updatedFields: updateFields,
      });
      return {
        code: 200,
        message: "更新练习与课程关系成功",
        data: null,
      };
    } catch (error) {
      console.error("updatePracticeIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getPracticeIndexes - 按参数查询练习索引
export const getPracticeIndexes = {
  url: "/api/practice/practiceindex",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);
      const { practiceId, courseSectionId, courseId } = req.query;

      // 筛选索引
      let result = [...practiceIndexes];
      if (practiceId) {
        result = result.filter((item) => item.practiceId === Number(practiceId));
      }
      if (courseSectionId) {
        result = result.filter(
          (item) => item.courseSectionId === Number(courseSectionId)
        );
      }
      if (courseId) {
        result = result.filter((item) => item.courseId === Number(courseId));
      }

      console.log("getPracticeIndexes success:", { total: result.length });
      return {
        code: 200,
        message: "查询成功",
        data: result,
      };
    } catch (error) {
      console.error("getPracticeIndexes error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getPracticeIndexesPage - 按参数分页查询练习索引
export const getPracticeIndexesPage = {
  url: "/api/practice/practiceindex/:pageNo/:pageSize",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);

      const pathParams = parsePathParams(req, 2);
      const { practiceId, courseSectionId, courseId } = req.query;
      const pageNo = Number(pathParams.param1);
      const pageSize = Number(pathParams.param2);

      // 筛选索引
      let filtered = [...practiceIndexes];
      if (practiceId) {
        filtered = filtered.filter((item) => item.practiceId === Number(practiceId));
      }
      if (courseSectionId) {
        filtered = filtered.filter(
          (item) => item.courseSectionId === Number(courseSectionId)
        );
      }
      if (courseId) {
        filtered = filtered.filter((item) => item.courseId === Number(courseId));
      }

      // 分页处理
      const pageResult = createPageResult(filtered, pageNo, pageSize);

      console.log("getPracticeIndexesPage success:", { ...pageResult });
      return {
        code: 200,
        message: "分页查询成功",
        data: pageResult,
      };
    } catch (error) {
      console.error("getPracticeIndexesPage error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

export default [
  createPracticeIndex,
  deletePracticeIndexByIds,
  updatePracticeIndex,
  getPracticeIndexes,
  getPracticeIndexesPage,
];
