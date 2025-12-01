/**
 * 业务错误类，处理业务逻辑错误
 */
export class BusinessError extends Error {
  constructor(code, message, data = null) {
    super(message);
    this.code = code;
    this.data = data;
  }
}