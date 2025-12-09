import { generateDateTime, getNextId, parsePathParams } from "../utils";
import { practiceIndexes, practices } from "./mockData";

// createPracticeAndIndex - 新增练习及关联的 PracticeIndex
export const createPracticeAndIndex = {
  url: "/api/practice/practice",
  method: "post",
  response: (req) => {
    try {
      console.log("req :", req);
      const { practice, practiceIndex } = req.body;

      // 验证必填字段
      if (!practice || !practiceIndex) {
        return {
          code: 400,
          message: "practice和practiceIndex字段不能为空",
          data: null,
        };
      }
      if (!practice.name || practice.questionNum == null) {
        return {
          code: 400,
          message: "practice.name和practice.questionNum字段不能为空",
          data: null,
        };
      }
      if (practiceIndex.courseId == null) {
        return {
          code: 400,
          message: "practiceIndex.courseId字段不能为空",
          data: null,
        };
      }

      // 生成新练习ID
      const newPracticeId = getNextId(practices);
      // 构造新练习
      const newPractice = {
        id: newPracticeId,
        name: practice.name,
        questionNum: practice.questionNum,
        createdAt: generateDateTime(),
        expiredAt: practice.expiredAt || null,
      };
      // 构造新练习索引
      const newPracticeIndex = {
        id: getNextId(practiceIndexes),
        practiceId: newPracticeId,
        courseSectionId: practiceIndex.courseSectionId,
        courseId: practiceIndex.courseId,
      };

      // 添加
      practices.push(newPractice);
      practiceIndexes.push(newPracticeIndex);

      console.log("createPracticeAndIndex success:", { newPractice, newPracticeIndex });
      return {
        code: 200,
        message: "新增练习成功",
        data: null,
      };
    } catch (error) {
      console.error("createPracticeAndIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// deletePracticeById - 根据练习ID删除练习及关联的 PracticeIndex
export const deletePracticeById = {
  url: "/api/practice/practice/:id",
  method: "delete",
  response: (req) => {
    try {
      console.log("req :", req);
      const practiceId = Number(req.query.id);

      // 验证练习是否存在
      const practiceIndex = practices.find((item) => item.id === practiceId);
      if (!practiceIndex) {
        return {
          code: 404,
          message: "练习不存在",
          data: null,
        };
      }

      // 删除练习和关联的索引
      const practiceIndexIds = practiceIndexes
        .filter((item) => item.practiceId === practiceId)
        .map((item) => item.id);
      practices.splice(
        practices.findIndex((item) => item.id === practiceId),
        1
      );
      practiceIndexes.splice(
        practiceIndexes.findIndex((item) => item.practiceId === practiceId),
        practiceIndexIds.length
      );

      console.log("deletePracticeById success:", {
        practiceId,
        deletedIndexIds: practiceIndexIds,
      });
      return {
        code: 200,
        message: "删除练习成功",
        data: null,
      };
    } catch (error) {
      console.error("deletePracticeById error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// deletePracticeByCourse - 根据课程系列ID和课程ID批量删除练习及关联的 PracticeIndex
export const deletePracticeByCourse = {
  url: "/api/practice/practice",
  method: "delete",
  response: (req) => {
    try {
      console.log("req :", req);
      const { courseSectionId, courseId } = req.query;

      // 验证必填字段
      if (!courseSectionId) {
        return {
          code: 400,
          message: "courseSectionId字段不能为空",
          data: null,
        };
      }

      // 筛选关联的练习ID
      const filterParams = { courseSectionId: Number(courseSectionId) };
      if (courseId !== undefined) {
        filterParams.courseId = courseId === "null" ? 0 : Number(courseId);
      }
      const relatedIndexes = practiceIndexes.filter((item) => {
        return (
          item.courseSectionId === filterParams.courseSectionId &&
          (filterParams.courseId === undefined || item.courseId === filterParams.courseId)
        );
      });
      const practiceIds = [...new Set(relatedIndexes.map((item) => item.practiceId))];

      if (practiceIds.length === 0) {
        return {
          code: 404,
          message: "未找到关联的练习",
          data: null,
        };
      }

      // 删除练习和关联的索引
      practices.splice(
        0,
        practices.length,
        ...practices.filter((item) => !practiceIds.includes(item.id))
      );
      practiceIndexes.splice(
        0,
        practiceIndexes.length,
        ...practiceIndexes.filter(
          (item) => !relatedIndexes.map((i) => i.id).includes(item.id)
        )
      );

      console.log("deletePracticeByCourse success:", {
        practiceIds,
        deletedIndexIds: relatedIndexes.map((i) => i.id),
      });
      return {
        code: 200,
        message: `成功删除${practiceIds.length}个练习`,
        data: null,
      };
    } catch (error) {
      console.error("deletePracticeByCourse error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// updatePractice - 更新练习信息
export const updatePractice = {
  url: "/api/practice/practice",
  method: "put",
  response: (req) => {
    try {
      console.log("req :", req);
      const practice = req.body;

      // 验证必填字段
      if (practice.id == null) {
        return {
          code: 400,
          message: "id字段不能为空",
          data: null,
        };
      }
      const updateFields = Object.keys(practice).filter(
        (key) => key !== "id" && key !== "createdAt"
      );
      if (updateFields.length === 0) {
        return {
          code: 400,
          message: "要更新的字段不能为空",
          data: null,
        };
      }

      // 查找练习并更新
      const practiceIndex = practices.find((item) => item.id === practice.id);
      if (!practiceIndex) {
        return {
          code: 404,
          message: "练习不存在",
          data: null,
        };
      }
      Object.assign(practiceIndex, practice);

      console.log("updatePractice success:", {
        practiceId: practice.id,
        updatedFields: updateFields,
      });
      return {
        code: 200,
        message: "更新练习成功",
        data: null,
      };
    } catch (error) {
      console.error("updatePractice error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getPractices - 按参数查询Practice
export const getPractices = {
  url: "/api/practice/practice",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);
      const { id, createdAtStart, createdAtEnd, expiredAtStart, expiredAtEnd } =
        req.query;

      // 筛选练习
      let result = [...practices];
      if (id) {
        result = result.filter((item) => item.id === Number(id));
      }
      if (createdAtStart) {
        result = result.filter((item) => item.createdAt >= createdAtStart);
      }
      if (createdAtEnd) {
        result = result.filter((item) => item.createdAt <= createdAtEnd);
      }
      if (expiredAtStart) {
        result = result.filter(
          (item) => item.expiredAt && item.expiredAt >= expiredAtStart
        );
      }
      if (expiredAtEnd) {
        result = result.filter(
          (item) => item.expiredAt && item.expiredAt <= expiredAtEnd
        );
      }

      console.log("searchPractices success:", { total: result.length });
      return {
        code: 200,
        message: "查询成功",
        data: result,
      };
    } catch (error) {
      console.error("searchPractices error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getPracticesPage - 按参数分页查询Practice
export const getPracticesPage = {
  url: "/api/practice/practice/:pageNo/:pageSize",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);

      const pathParams = parsePathParams(req, 2);
      const { id, createdAtStart, createdAtEnd, expiredAtStart, expiredAtEnd } =
        req.query;
      const pageNo = Number(pathParams.param1);
      const pageSize = Number(pathParams.param2);

      // 筛选练习
      let filtered = [...practices];
      if (id) {
        filtered = filtered.filter((item) => item.id === Number(id));
      }
      if (createdAtStart) {
        filtered = filtered.filter((item) => item.createdAt >= createdAtStart);
      }
      if (createdAtEnd) {
        filtered = filtered.filter((item) => item.createdAt <= createdAtEnd);
      }
      if (expiredAtStart) {
        filtered = filtered.filter(
          (item) => item.expiredAt && item.expiredAt >= expiredAtStart
        );
      }
      if (expiredAtEnd) {
        filtered = filtered.filter(
          (item) => item.expiredAt && item.expiredAt <= expiredAtEnd
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

      console.log("searchPracticesPage success:", { ...pageResult });
      return {
        code: 200,
        message: "分页查询成功",
        data: pageResult,
      };
    } catch (error) {
      console.error("searchPracticesPage error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getPracticesByIndex - 根据课程索引查询Practice
export const getPracticesByIndex = {
  url: "/api/practice/practice/index",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);
      const { courseSectionId, courseId } = req.query;

      // 验证必填字段
      if (!courseSectionId) {
        return {
          code: 400,
          message: "courseSectionId字段不能为空",
          data: null,
        };
      }

      // 筛选关联的练习ID
      const relatedIndexes = practiceIndexes.filter((item) => {
        // 如果没有指定courseId，则只按courseSectionId筛选
        if (courseId === undefined || courseId === "null") {
          return item.courseSectionId === Number(courseSectionId);
        }
        // 如果指定了courseId，则同时按courseSectionId和courseId筛选
        return (
          item.courseSectionId === Number(courseSectionId) &&
          item.courseId === Number(courseId)
        );
      });
      const practiceIds = [...new Set(relatedIndexes.map((item) => item.practiceId))];
      const result = practices.filter((item) => practiceIds.includes(item.id));

      console.log("getPracticesByIndex success:", { total: result.length, practiceIds });
      return {
        code: 200,
        message: "查询成功",
        data: result,
      };
    } catch (error) {
      console.error("getPracticesByIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// getPracticesPageByIndex - 根据课程索引分页查询Practice
export const getPracticesPageByIndex = {
  url: "/api/practice/practice/index/:pageNo/:pageSize",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);

      const pathParams = parsePathParams(req, 2);
      const { courseSectionId, courseId } = req.query;
      const pageNo = Number(pathParams.param1);
      const pageSize = Number(pathParams.param2);

      // 验证必填字段
      if (!courseSectionId) {
        return {
          code: 400,
          message: "courseSectionId字段不能为空",
          data: null,
        };
      }

      // 筛选关联的练习
      const relatedIndexes = practiceIndexes.filter((item) => {
        // 如果没有指定courseId，则只按courseSectionId筛选
        if (courseId === undefined || courseId === "null") {
          return item.courseSectionId === Number(courseSectionId);
        }
        // 如果指定了courseId，则同时按courseSectionId和courseId筛选
        return (
          item.courseSectionId === Number(courseSectionId) &&
          item.courseId === Number(courseId)
        );
      });
      const practiceIds = [...new Set(relatedIndexes.map((item) => item.practiceId))];
      const filtered = practices.filter((item) => practiceIds.includes(item.id));

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

      console.log("getPracticesPageByIndex success:", { ...pageResult });
      return {
        code: 200,
        message: "分页查询成功",
        data: pageResult,
      };
    } catch (error) {
      console.error("getPracticesPageByIndex error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

export default [
  createPracticeAndIndex,
  deletePracticeById,
  deletePracticeByCourse,
  updatePractice,
  getPractices,
  getPracticesPage,
  getPracticesByIndex,
  getPracticesPageByIndex,
];
