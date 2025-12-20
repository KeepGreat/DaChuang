import { ElMessage } from "element-plus";

/**
 * 表单验证 hook
 * 只负责验证表单并给出提示，不包含业务逻辑
 */
export function useFormValidation() {
  /**
   * 验证表单是否通过校验。组件内的函数可以用isValid来接收验证结果，if (!isValid)，可以直接return，不执行后面的逻辑
   * @param formRef 表单引用
   * @returns Promise<boolean> 返回验证是否通过
   */
  function validateForm(formRef) {
    return new Promise((resolve) => {
      if (!formRef) {
        console.error("表单引用不存在");
        ElMessage.error("表单引用不存在");
        resolve(false);
        return;
      }

      // 一般情况，调用表单的validate方法
      formRef.validate((valid) => {
        if (!valid) {
          console.log("表单校验未通过");
          ElMessage.warning("请检查表单填写是否正确");
          resolve(false);
        } else {
          // 校验通过的情况，不输出
          resolve(true);
        }
      });
    });
  }

  return {
    validateForm,
  };
}