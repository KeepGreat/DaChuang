import request from "@/api/request";
import { useUserStore } from "@/store";

// model
/**
 * 代码沙箱输入实体类
 * @typedef {Object} CodeSandboxInput
 * @property {string} codeLanguage - 代码语言（例如：java、python）
 * @property {string} code - 代码内容
 * @property {string} input - 运行输入
 */

/**
 * 教学输入实体类
 * @typedef {Object} TeachingInput
 * @property {string} question - 用户问题
 * @property {CodeSandboxInput|null} codeSandboxInput - 代码沙箱输入，answer 接口可为空
 * @property {number[]} relativeMaterialIds - 相关资料ID列表，用于RAG检索时元数据过滤
 */

const AI_BASE_PATH = "/api/teaching/ai";

function buildAbsoluteUrl(path) {
    const baseURL = request.defaults.baseURL || window.location.origin;
    return `${baseURL}${path}`;
}

function getAuthHeaders() {
    const userStore = useUserStore();
    const headers = {
        "Content-Type": "application/json",
    };

    if (userStore.token) {
        headers.JwtToken = userStore.token;
    }

    return headers;
}

function assertTeachInput(teachingInput) {
    if (!teachingInput || !teachingInput.question || !teachingInput.codeSandboxInput) {
        throw new Error("teach 接口要求 teachingInput.question 和 teachingInput.codeSandboxInput 均不能为空");
    }
}

function assertAnswerInput(teachingInput) {
    if (!teachingInput || !teachingInput.question) {
        throw new Error("answer 接口要求 teachingInput.question 不能为空");
    }
}

async function postStream(path, teachingInput, onChunk, signal) {
    const response = await fetch(buildAbsoluteUrl(path), {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(teachingInput),
        signal,
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`流式请求失败: ${response.status} ${response.statusText} ${errorText}`);
    }

    if (!response.body) {
        const text = await response.text();
        if (onChunk) {
            onChunk(text);
        }
        return text;
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder("utf-8");
    let fullText = "";

    while (true) {
        const { done, value } = await reader.read();
        if (done) {
            break;
        }
        const chunk = decoder.decode(value, { stream: true });
        fullText += chunk;
        if (onChunk) {
            onChunk(chunk);
        }
    }

    const tailChunk = decoder.decode();
    if (tailChunk) {
        fullText += tailChunk;
        if (onChunk) {
            onChunk(tailChunk);
        }
    }

    return fullText;
}

// API
/**
 * 教学讲解接口（普通调用）
 * 功能：向 /teach 提交问题和代码上下文，返回后端响应（非流式读取）
 * @param {TeachingInput} teachingInput - 教学输入（question、codeSandboxInput 均必填）
 * @returns {Promise<any>} 后端响应数据
 */
export function teach(teachingInput) {
    assertTeachInput(teachingInput);
    return request({
        url: `${AI_BASE_PATH}/teach`,
        method: "POST",
        data: teachingInput,
        headers: {
            "Content-Type": "application/json",
        },
    });
}

/**
 * 问答接口（普通调用）
 * 功能：向 /answer 提交问题，返回后端响应（非流式读取）
 * @param {TeachingInput} teachingInput - 教学输入（question 必填，codeSandboxInput 可为空）
 * @returns {Promise<any>} 后端响应数据
 */
export function answer(teachingInput) {
    assertAnswerInput(teachingInput);
    return request({
        url: `${AI_BASE_PATH}/answer`,
        method: "POST",
        data: teachingInput,
        headers: {
            "Content-Type": "application/json",
        },
    });
}

/**
 * 教学讲解接口（流式调用）
 * 功能：基于 Fetch + ReadableStream 按块读取 /teach 输出
 * @param {TeachingInput} teachingInput - 教学输入（question、codeSandboxInput 均必填）
 * @param {(chunk: string) => void} [onChunk] - 每收到一个文本块时的回调
 * @param {AbortSignal} [signal] - 可选取消信号
 * @returns {Promise<string>} 完整拼接后的流式文本
 */
export function teachStream(teachingInput, onChunk, signal) {
    assertTeachInput(teachingInput);
    return postStream(`${AI_BASE_PATH}/teach`, teachingInput, onChunk, signal);
}

/**
 * 问答接口（流式调用）
 * 功能：基于 Fetch + ReadableStream 按块读取 /answer 输出
 * @param {TeachingInput} teachingInput - 教学输入（question 必填，codeSandboxInput 可为空）
 * @param {(chunk: string) => void} [onChunk] - 每收到一个文本块时的回调
 * @param {AbortSignal} [signal] - 可选取消信号
 * @returns {Promise<string>} 完整拼接后的流式文本
 */
export function answerStream(teachingInput, onChunk, signal) {
    assertAnswerInput(teachingInput);
    return postStream(`${AI_BASE_PATH}/answer`, teachingInput, onChunk, signal);
}