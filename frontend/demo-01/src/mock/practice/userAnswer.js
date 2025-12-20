import { createPageResult, getNextId, parsePathParams } from "../utils";
import { answers, userAnswers } from "./mockData";

// 增加用户答案记录
export const createUserAnswer = {
  url: "/api/practice/useranswer",
  method: "post",
  response: (req) => {
    try {
      console.log("req :", req);
      const { content, userId, questionId, questionType } = req.body;

      // 验证必填字段
      if (!content || !userId || !questionId || questionType === undefined) {
        return {
          code: 400,
          message: "必填字段不能为空",
          data: null,
        };
      }

      // 创建新用户答案，默认未批改（score=-1）
      const newUserAnswer = {
        id: getNextId(userAnswers),
        content,
        userId,
        questionId,
        questionType,
        score: -1,
      };

      userAnswers.push(newUserAnswer);

      console.log("createUserAnswer success:", { newUserAnswer });
      return {
        code: 200,
        message: "success",
        data: null,
      };
    } catch (error) {
      console.error("createUserAnswer error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

/**
 * deleteUserAnswerById和deleteUserAnswerByUserId - 根据id删除用户答案记录 和 根据用户id删除所有相关的用户答案记录
 * 需要合并，否则两个MockMethod可能会出现冲突，两个接口使用了相同的url /api/practice/useranswer，且都是DELETE请求
 * Mock内部通过检查参数来自动判断使用哪种删除逻辑
 */
export const deleteUserAnswer = {
  url: "/api/practice/useranswer",
  method: "delete",
  response: (req) => {
    try {
      console.log("req :", req);
      const { id, userId } = req.query;

      // 如果有id参数，则按id删除单个答案，deleteUserAnswerById
      if (id) {
        console.log(`deleteUserAnswerById id: ${id}`);
        const index = userAnswers.findIndex((item) => item.id === parseInt(id));

        if (index === -1) {
          return {
            code: 404,
            message: "用户答案不存在",
            data: null,
          };
        }

        // 删除
        userAnswers.splice(index, 1);

        console.log("deleteUserAnswerById success:", { deletedId: parseInt(id) });
        return {
          code: 200,
          message: "success",
          data: null,
        };
      }

      // 如果有userId参数，则按userId删除该用户的所有答案，deleteUserAnswerByUserId
      else if (userId) {
        console.log(`deleteUserAnswerByUserId userId: ${userId}`);
        // 删除该用户的所有答案
        const initialLength = userAnswers.length;
        for (let i = userAnswers.length - 1; i >= 0; i--) {
          if (userAnswers[i].userId === userId) {
            userAnswers.splice(i, 1);
          }
        }

        // 将删除操作前后的数组长度对比，判断是否有删除操作
        if (userAnswers.length === initialLength) {
          return {
            code: 404,
            message: "该用户没有答案记录",
            data: null,
          };
        }

        console.log("deleteUserAnswerByUserId success:", {
          deletedUserId: userId,
          deletedCount: initialLength - userAnswers.length,
        });
        return {
          code: 200,
          message: "success",
          data: null,
        };
      }

      // 如果两个参数都不存在，返回错误
      else {
        return {
          code: 400,
          message: "请求参数错误",
          data: null,
        };
      }
    } catch (error) {
      console.error("deleteUserAnswer error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// 根据id更新用户答案记录
export const updateUserAnswerById = {
  url: "/api/practice/useranswer",
  method: "put",
  response: (req) => {
    try {
      console.log("req :", req);
      const { id, content, userId, questionId, questionType, comment } = req.body;

      if (!id) {
        return {
          code: 400,
          message: "id不能为空",
          data: null,
        };
      }

      const index = userAnswers.findIndex((item) => item.id === parseInt(id));

      if (index === -1) {
        return {
          code: 404,
          message: "用户答案不存在",
          data: null,
        };
      }

      // 更新字段（不更新score）
      if (content !== undefined) userAnswers[index].content = content;
      if (userId !== undefined) userAnswers[index].userId = userId;
      if (questionId !== undefined) userAnswers[index].questionId = questionId;
      if (questionType !== undefined) userAnswers[index].questionType = questionType;
      if (comment !== undefined) userAnswers[index].comment = comment;

      console.log("updateUserAnswerById success:", {
        updatedId: parseInt(id),
        updatedFields: { content, userId, questionId, questionType, comment },
      });
      return {
        code: 200,
        message: "success",
        data: null,
      };
    } catch (error) {
      console.error("updateUserAnswerById error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// 查询用户答案记录
export const getUserAnswers = {
  url: "/api/practice/useranswer",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);
      const { id, userId, questionId, questionType, score } = req.query;

      let filteredAnswers = [...userAnswers];

      // 根据条件过滤
      if (id) {
        filteredAnswers = filteredAnswers.filter((item) => item.id === parseInt(id));
      }
      if (userId) {
        filteredAnswers = filteredAnswers.filter((item) => item.userId === userId);
      }
      if (questionId) {
        filteredAnswers = filteredAnswers.filter(
          (item) => item.questionId === parseInt(questionId)
        );
      }
      if (questionType !== undefined) {
        filteredAnswers = filteredAnswers.filter(
          (item) => item.questionType === parseInt(questionType)
        );
      }
      if (score !== undefined) {
        filteredAnswers = filteredAnswers.filter(
          (item) => item.score === parseInt(score)
        );
      }

      console.log("getUserAnswers success:", { total: filteredAnswers.length });
      return {
        code: 200,
        message: "success",
        data: filteredAnswers,
      };
    } catch (error) {
      console.error("getUserAnswers error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// 分页查询用户答案记录
export const getUserAnswersPage = {
  url: "/api/practice/useranswer/:pageNo/:pageSize",
  method: "get",
  response: (req) => {
    try {
      console.log("req :", req);

      const pathParams = parsePathParams(req, 2);
      const { id, userId, questionId, questionType, score } = req.query;
      const pageNo = Number(pathParams.param1);
      const pageSize = Number(pathParams.param2);

      let filteredAnswers = [...userAnswers];

      // 根据条件过滤
      if (id) {
        filteredAnswers = filteredAnswers.filter((item) => item.id === parseInt(id));
      }
      if (userId) {
        filteredAnswers = filteredAnswers.filter((item) => item.userId === userId);
      }
      if (questionId) {
        filteredAnswers = filteredAnswers.filter(
          (item) => item.questionId === parseInt(questionId)
        );
      }
      if (questionType !== undefined) {
        filteredAnswers = filteredAnswers.filter(
          (item) => item.questionType === parseInt(questionType)
        );
      }
      if (score !== undefined) {
        filteredAnswers = filteredAnswers.filter(
          (item) => item.score === parseInt(score)
        );
      }

      // 分页处理
      const pageResult = createPageResult(filteredAnswers, pageNo, pageSize);

      console.log("getUserAnswersPage success:", { ...pageResult });
      return {
        code: 200,
        message: "success",
        data: pageResult,
      };
    } catch (error) {
      console.error("getUserAnswersPage error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// 自动判题（仅用于判断题和选择题）
export const judgeAnswersAuto = {
  url: "/api/practice/useranswer/judge/auto",
  method: "post",
  response: (req) => {
    try {
      console.log("req :", req);
      const userAnswerList = req.body;

      if (!Array.isArray(userAnswerList) || userAnswerList.length === 0) {
        return {
          code: 400,
          message: "用户答案列表不能为空",
          data: null,
        };
      }

      // 验证每个答案的questionId和questionType
      for (const answer of userAnswerList) {
        if (!answer.questionId) {
          return {
            code: 400,
            message: "每个用户答案的questionId不能为空",
            data: null,
          };
        }

        // 检查是否为支持的题型（仅判断题0和选择题1）
        if (answer.questionType !== 0 && answer.questionType !== 1) {
          return {
            code: 400,
            message: `自动判题接口仅支持判断题(0)和选择题(1)，问题ID ${answer.questionId} 的类型为 ${answer.questionType}`,
            data: null,
          };
        }
      }

      // 自动判题，仅处理判断题(0)和选择题(1)
      const judgedAnswers = userAnswerList.map((answer) => {
        // 查找该问题的正确答案，使用answers数组
        const correctAnswer = answers.find(
          (item) => item.questionId === answer.questionId
        );

        let score = 0;

        if (correctAnswer) {
          // 进行字符串比较判题，去除前后空格并转换为小写
          const userContent = answer.content.trim().toLowerCase();
          const correctContent = correctAnswer.content.trim().toLowerCase();

          // 判断题和选择题的答案比对
          if (userContent === correctContent) {
            // 根据问题类型设置不同分值
            if (answer.questionType === 0) {
              // 判断题
              score = 5;
            } else if (answer.questionType === 1) {
              // 选择题
              score = 10;
            }
          }
        }

        // 更新userAnswers数组中的分数
        const existingAnswer = userAnswers.find((item) => item.id === answer.id);
        if (existingAnswer) {
          existingAnswer.score = score;
        }

        return {
          ...answer,
          score,
        };
      });

      console.log("judgeAnswersAuto success:", { judgedCount: judgedAnswers.length });
      return {
        code: 200,
        message: "success",
        data: judgedAnswers,
      };
    } catch (error) {
      console.error("judgeAnswersAuto error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// 手动判题（简答题）
export const judgeAnswerManual = {
  url: "/api/practice/useranswer/judge/manual",
  method: "post",
  response: (req) => {
    try {
      console.log("req :", req);
      const { id, score, comment } = req.body;

      if (!id || score === undefined) {
        return {
          code: 400,
          message: "id和score不能为空",
          data: null,
        };
      }

      // 先根据id找到用户答案
      const index = userAnswers.findIndex((item) => item.id === parseInt(id));

      if (index === -1) {
        return {
          code: 404,
          message: "用户答案不存在",
          data: null,
        };
      }

      // 验证问题类型是否为简答题
      if (userAnswers[index].questionType !== 2) {
        return {
          code: 400,
          message: `手动判题接口仅支持简答题(2)，当前问题类型为 ${userAnswers[index].questionType}`,
          data: null,
        };
      }

      // 更新分数和批注
      userAnswers[index].score = parseInt(score);
      if (comment !== undefined) {
        userAnswers[index].comment = comment;
      }

      console.log("judgeAnswerManual success:", {
        judgedId: parseInt(id),
        score: parseInt(score),
        comment: comment || "",
      });
      return {
        code: 200,
        message: "success",
        data: null,
      };
    } catch (error) {
      console.error("judgeAnswerManual error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

// 代码判题（编程题），AI分析代码，添加一定timeout
export const judgeCodeAnswer = {
  url: "/api/practice/useranswer/judge/code",
  method: "post",
  timeout: 3000,
  response: (req) => {
    try {
      console.log("req :", req);
      const { codeSandboxInput, userAnswer } = req.body;

      if (!codeSandboxInput || !userAnswer) {
        return {
          code: 400,
          message: "codeSandboxInput和userAnswer不能为空",
          data: null,
        };
      }

      if (!codeSandboxInput.codeLanguage || !codeSandboxInput.code) {
        return {
          code: 400,
          message: "代码语言和代码内容不能为空",
          data: null,
        };
      }

      if (!userAnswer.questionId || !userAnswer.userId) {
        return {
          code: 400,
          message: "问题ID和用户ID不能为空",
          data: null,
        };
      }

      // 验证问题类型是否为编程题
      if (userAnswer.questionType !== undefined && userAnswer.questionType !== 3) {
        return {
          code: 400,
          message: `代码判题接口仅支持编程题(3)，当前问题类型为 ${userAnswer.questionType}`,
          data: null,
        };
      }

      // 模拟代码执行输出（使用文档示例数据）
      const codeSandboxOutput = {
        stdout: "55\n",
        stderr: "",
        status: "Accepted",
        exitStatus: 0,
        memory: 1568768,
        runTime: 4799477,
        procPeak: 1,
        time: 1335000,
      };

      // 模拟AI分析（使用文档示例数据）
      const analysisText =
        "1.可读性\n        - 变量和函数命名清晰，符合C++命名习惯（如fibonacci）。\n        - 代码缩进和格式良好，逻辑块用空格分隔清楚。\n        - 缺少注释说明斐波那契数列的定义和递归逻辑，建议添加简要注释提升可读性。\n\n    2.结构性\n        - 主函数main简洁，仅负责输入输出与函数调用。\n        - fibonacci函数结构清晰，条件分支合理覆盖边界情况（n≤0, n=1, n=2）。\n        - 递归实现直观但未优化，深层调用可能导致栈溢出。\n\n    3.模块化\n        - 功能单一，fibonacci函数专注计算，职责明确。\n        - 无重复代码，但递归方式导致大量重复计算（如F(n-2)被多次调用），影响效率。\n        - 可引入记忆化或迭代方式提升性能并增强复用性。\n\n    4.解题规范\n        - 正确实现了题目要求：F(1)=1, F(2)=1, F(n)=F(n-1)+F(n-2)。\n        - 处理了n≤0的边界情况（返回0），虽超出定义域但仍合理。\n        - 输出格式正确，通过标准输入输出完成交互，且所有测试用例通过（status=Accepted）。";

      // 随机生成评分（1-5分）
      const analysisOutput = {
        analysis: analysisText,
        score: {
          readability: Math.floor(Math.random() * 5) + 1,
          structure: Math.floor(Math.random() * 5) + 1,
          modularity: Math.floor(Math.random() * 5) + 1,
          guidelines: Math.floor(Math.random() * 5) + 1,
        },
      };

      // 计算总分
      const totalScore =
        analysisOutput.score.readability +
        analysisOutput.score.structure +
        analysisOutput.score.modularity +
        analysisOutput.score.guidelines;

      // 构造返回的userAnswer
      const savedUserAnswer = {
        id: userAnswer.id || null,
        content: userAnswer.content || null,
        userId: userAnswer.userId,
        questionId: userAnswer.questionId,
        questionType: userAnswer.questionType || null,
        score: totalScore,
        comment: analysisText,
      };

      // 如果有id，更新现有答案；否则创建新答案
      if (userAnswer.id) {
        const index = userAnswers.findIndex((item) => item.id === userAnswer.id);
        if (index !== -1) {
          userAnswers[index] = savedUserAnswer;
        }
      } else {
        savedUserAnswer.id = getNextId(userAnswers);
        userAnswers.push(savedUserAnswer);
      }

      const judgeCodeResponse = {
        userAnswer: savedUserAnswer,
        codeSandboxOutput: codeSandboxOutput,
        analysisOutput: analysisOutput,
      };

      console.log("judgeCodeAnswer success:", judgeCodeResponse);
      return {
        code: 200,
        message: "代码判题成功",
        data: judgeCodeResponse,
      };
    } catch (error) {
      console.error("judgeCodeAnswer error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

export default [
  createUserAnswer,
  deleteUserAnswer,
  updateUserAnswerById,
  getUserAnswers,
  getUserAnswersPage,
  judgeAnswersAuto,
  judgeAnswerManual,
  judgeCodeAnswer,
];
